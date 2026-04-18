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
import org.kitona.zus.domain.authorization.model.ConditionDefinition;
import org.kitona.zus.domain.port.IConditionEvaluator;
import org.kitona.zus.infrastructure.cache.FgaCacheManager;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

/**
 * 基于 CEL 的条件求值器。
 * <p>
 * 该类实现了 IConditionEvaluator 接口，用于评估基于 CEL 表达式的条件定义。
 * 它支持缓存编译后的 CEL 表达式以提高性能，并提供丰富的绑定变量供表达式使用。
 * </p>
 */
@Component
public class CelConditionEvaluator implements IConditionEvaluator {

    /**
     * 缓存命名空间。
     */
    private static final String CACHE_NAMESPACE = "condition";

    /**
     * 缓存键模板，格式为 "condition:{id}:{expression}"。
     */
    private static final String CACHE_KEY_TEMPLATE = "condition:%s:%s";

    /**
     * 绑定变量名：input。
     */
    private static final String BINDING_INPUT = "input";

    /**
     * 输入上下文变量名：context。
     */
    private static final String INPUT_CONTEXT = "context";

    /**
     * 输入参数变量名：params。
     */
    private static final String INPUT_PARAMS = "params";

    /**
     * 输入主体变量名：subject。
     */
    private static final String INPUT_SUBJECT = "subject";

    /**
     * 输入对象变量名：object。
     */
    private static final String INPUT_OBJECT = "object";

    /**
     * 输入元组变量名：tuple。
     */
    private static final String INPUT_TUPLE = "tuple";

    /**
     * 类型键名：type。
     */
    private static final String KEY_TYPE = "type";

    /**
     * ID 键名：id。
     */
    private static final String KEY_ID = "id";

    /**
     * 关系键名：relation。
     */
    private static final String KEY_RELATION = "relation";

    /**
     * 对象类型键名：objectType。
     */
    private static final String KEY_OBJECT_TYPE = "objectType";

    /**
     * 对象 ID 键名：objectId。
     */
    private static final String KEY_OBJECT_ID = "objectId";

    /**
     * 主体类型键名：subjectType。
     */
    private static final String KEY_SUBJECT_TYPE = "subjectType";

    /**
     * 主体 ID 键名：subjectId。
     */
    private static final String KEY_SUBJECT_ID = "subjectId";

    /**
     * 主体关系键名：subjectRelation。
     */
    private static final String KEY_SUBJECT_RELATION = "subjectRelation";

    /**
     * 过期时间键名：expiresAt。
     */
    private static final String KEY_EXPIRES_AT = "expiresAt";

    /**
     * CEL 编译器实例，用于编译 CEL 表达式。
     */
    private static final CelCompiler CEL_COMPILER = CelCompilerFactory.standardCelCompilerBuilder()
            .addVar(BINDING_INPUT, SimpleType.DYN)
            .build();

    /**
     * CEL 运行时实例，用于执行编译后的 CEL 表达式。
     */
    private static final CelRuntime CEL_RUNTIME = CelRuntimeFactory.standardCelRuntimeBuilder().build();

    /**
     * FGA 缓存管理器，用于缓存编译后的 CEL 表达式。
     */
    @Resource
    private FgaCacheManager cacheManager;

    /**
     * 评估给定的条件定义是否满足当前匹配上下文。
     *
     * @param definition 条件定义，包含需要评估的 CEL 表达式
     * @param context    元组匹配上下文，包含评估所需的输入数据
     * @return 如果条件评估结果为 true 则返回 true，否则返回 false
     * @throws IllegalArgumentException 如果 definition 或 context 为 null
     */
    @Override
    public boolean evaluate(ConditionDefinition definition, TupleMatchContext context) {
        Objects.requireNonNull(definition, "definition must not be null");
        Objects.requireNonNull(context, "context must not be null");
        try {
            CelAbstractSyntaxTree ast = getAst(definition);
            Object result = CEL_RUNTIME.createProgram(ast).eval(buildBindings(context));
            return result instanceof Boolean bool && bool;
        } catch (CelValidationException | CelEvaluationException ex) {
            return false;
        }
    }

    /**
     * 获取条件定义对应的 CEL 抽象语法树。
     * <p>
     * 首先尝试从缓存中获取，如果缓存不存在则编译表达式并存入缓存。
     * </p>
     *
     * @param definition 条件定义
     * @return CEL 抽象语法树
     * @throws CelValidationException 如果表达式编译失败
     */
    private CelAbstractSyntaxTree getAst(ConditionDefinition definition) throws CelValidationException {
        String cacheKey = buildCacheKey(definition);
        Object cached = cacheManager.getModel(CACHE_NAMESPACE, cacheKey);
        if (cached instanceof CelAbstractSyntaxTree ast) {
            return ast;
        }
        CelAbstractSyntaxTree ast = CEL_COMPILER.compile(definition.getExpression()).getAst();
        cacheManager.setModel(CACHE_NAMESPACE, cacheKey, ast);
        return ast;
    }

    /**
     * 构建条件定义的缓存键。
     *
     * @param definition 条件定义
     * @return 缓存键字符串
     */
    private String buildCacheKey(ConditionDefinition definition) {
        return CACHE_KEY_TEMPLATE.formatted(definition.getId(), definition.getExpression());
    }

    /**
     * 构建 CEL 表达式所需的绑定变量。
     *
     * @param context 元组匹配上下文
     * @return 绑定变量映射
     */
    private Map<String, Object> buildBindings(TupleMatchContext context) {
        return Map.of(BINDING_INPUT, buildInputBinding(context));
    }

    /**
     * 统一条件输入根对象，表达式约定为 input.context / input.params / input.subject / input.object / input.tuple。
     * <p>
     * 该方法构建包含所有可用输入数据的映射，供 CEL 表达式使用。
     * </p>
     *
     * @param context 元组匹配上下文
     * @return 包含所有输入数据的映射
     */
    private Map<String, Object> buildInputBinding(TupleMatchContext context) {
        return Map.of(
                INPUT_CONTEXT, context.request().context(),
                INPUT_PARAMS, parseConditionContext(context.tuple().getConditionContext()),
                INPUT_SUBJECT, buildSubjectBinding(context),
                INPUT_OBJECT, buildObjectBinding(context),
                INPUT_TUPLE, buildTupleBinding(context)
        );
    }

    /**
     * 构建主体（subject）相关的绑定变量。
     *
     * @param context 元组匹配上下文
     * @return 包含主体类型、ID 和关系的映射
     */
    private Map<String, Object> buildSubjectBinding(TupleMatchContext context) {
        return Map.of(
                KEY_TYPE, context.request().subject().type(),
                KEY_ID, context.request().subject().id(),
                KEY_RELATION, context.request().subject().relation()
        );
    }

    /**
     * 构建对象（object）相关的绑定变量。
     *
     * @param context 元组匹配上下文
     * @return 包含对象类型和 ID 的映射
     */
    private Map<String, Object> buildObjectBinding(TupleMatchContext context) {
        return Map.of(
                KEY_TYPE, context.request().object().type(),
                KEY_ID, context.request().object().id()
        );
    }

    /**
     * 构建元组（tuple）相关的绑定变量。
     *
     * @param context 元组匹配上下文
     * @return 包含元组所有属性的映射
     */
    private Map<String, Object> buildTupleBinding(TupleMatchContext context) {
        return Map.of(
                KEY_OBJECT_TYPE, context.tuple().getObjectType(),
                KEY_OBJECT_ID, context.tuple().getObjectId(),
                KEY_RELATION, context.tuple().getRelation(),
                KEY_SUBJECT_TYPE, context.tuple().getSubjectType(),
                KEY_SUBJECT_ID, context.tuple().getSubjectId(),
                KEY_SUBJECT_RELATION, context.tuple().getSubjectRelation(),
                KEY_EXPIRES_AT, context.tuple().getExpiresAt()
        );
    }

    /**
     * 解析条件上下文字符串为映射。
     * <p>
     * 如果输入为空或解析失败，返回空映射。
     * </p>
     *
     * @param conditionContext 条件上下文字符串，应为 JSON 格式
     * @return 解析后的映射，如果解析失败则返回空映射
     */
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
