package org.kitona.zus.infrastructure.external.cel;

import dev.cel.common.CelAbstractSyntaxTree;
import dev.cel.common.CelValidationException;
import dev.cel.common.types.SimpleType;
import dev.cel.compiler.CelCompiler;
import dev.cel.compiler.CelCompilerFactory;
import dev.cel.runtime.CelEvaluationException;
import dev.cel.runtime.CelRuntime;
import dev.cel.runtime.CelRuntimeFactory;
import jakarta.annotation.Resource;
import org.kitona.zus.common.utils.JacksonUtil;
import org.kitona.zus.domain.authorization.evaluation.runtime.TupleMatchContext;
import org.kitona.zus.domain.port.IConditionEvaluator;
import org.kitona.zus.domain.authorization.model.ConditionDefinition;
import org.kitona.zus.infrastructure.cache.FgaCacheManager;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 基于 CEL 的条件求值器。
 */
@Component
public class CelConditionEvaluator implements IConditionEvaluator {

    private static final CelCompiler CEL_COMPILER = CelCompilerFactory.standardCelCompilerBuilder()
            .addVar("context", SimpleType.DYN)
            .addVar("params", SimpleType.DYN)
            .addVar("subject", SimpleType.DYN)
            .addVar("object", SimpleType.DYN)
            .addVar("tuple", SimpleType.DYN)
            .build();

    private static final CelRuntime CEL_RUNTIME = CelRuntimeFactory.standardCelRuntimeBuilder().build();

    @Resource
    private FgaCacheManager cacheManager;

    @Override
    public boolean evaluate(ConditionDefinition definition, TupleMatchContext context) {
        try {
            CelAbstractSyntaxTree ast = getAst(definition);
            Object result = CEL_RUNTIME.createProgram(ast).eval(buildBindings(context));
            return result instanceof Boolean bool && bool;
        } catch (CelValidationException | CelEvaluationException ex) {
            return false;
        }
    }

    private CelAbstractSyntaxTree getAst(ConditionDefinition definition) throws CelValidationException {
        String cacheKey = "condition:" + definition.getId() + ":" + definition.getExpression();
        Object cached = cacheManager.getModel("condition", cacheKey);
        if (cached instanceof CelAbstractSyntaxTree ast) {
            return ast;
        }
        CelAbstractSyntaxTree ast = CEL_COMPILER.compile(definition.getExpression()).getAst();
        cacheManager.setModel("condition", cacheKey, ast);
        return ast;
    }

    private Map<String, Object> buildBindings(TupleMatchContext context) {
        Map<String, Object> bindings = new HashMap<>();
        bindings.put("context", context.request().context());
        bindings.put("params", parseConditionContext(context.tuple().getConditionContext()));
        bindings.put("subject", Map.of(
                "type", context.request().subject().type(),
                "id", context.request().subject().id(),
                "relation", context.request().subject().relation()
        ));
        bindings.put("object", Map.of(
                "type", context.request().object().type(),
                "id", context.request().object().id()
        ));
        bindings.put("tuple", Map.of(
                "objectType", context.tuple().getObjectType(),
                "objectId", context.tuple().getObjectId(),
                "relation", context.tuple().getRelation(),
                "subjectType", context.tuple().getSubjectType(),
                "subjectId", context.tuple().getSubjectId(),
                "subjectRelation", context.tuple().getSubjectRelation(),
                "expiresAt", context.tuple().getExpiresAt()
        ));
        return bindings;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> parseConditionContext(String conditionContext) {
        if (conditionContext == null || conditionContext.isBlank()) {
            return Map.of();
        }
        Object value = JacksonUtil.parseObject(conditionContext, Map.class);
        if (value instanceof Map<?, ?> map) {
            return (Map<String, Object>) map;
        }
        return Map.of();
    }
}
