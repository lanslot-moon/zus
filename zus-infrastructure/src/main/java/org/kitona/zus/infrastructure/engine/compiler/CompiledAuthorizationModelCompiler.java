package org.kitona.zus.infrastructure.engine.compiler;

import org.kitona.zus.domain.authorization.model.AuthorizationModelAggregate;
import org.kitona.zus.domain.authorization.model.ConditionDefinition;
import org.kitona.zus.domain.authorization.model.RelationDefinition;
import org.kitona.zus.domain.authorization.model.TypeDefinition;
import org.kitona.zus.domain.authorization.evaluation.compiled.CompiledAuthorizationModel;
import org.kitona.zus.domain.authorization.evaluation.compiled.CompiledRelation;
import org.kitona.zus.domain.authorization.evaluation.nodes.RewriteNode;
import org.kitona.zus.domain.port.ICompiledModelCompiler;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 无状态的已编译模型编译器。
 * 该类负责将领域模型聚合编译为可执行的只读模型。
 */
@Component
public class CompiledAuthorizationModelCompiler implements ICompiledModelCompiler {
    /**
     * 表达式解析器适配器
     * 用于解析和编译表达式相关的逻辑
     */
    private final RewriteExpressionParserAdapter rewriteExpressionParser;

    /**
     * 构造函数，初始化表达式解析器适配器
     */
    public CompiledAuthorizationModelCompiler() {
        this.rewriteExpressionParser = new RewriteExpressionParserAdapter();
    }

    /**
     * 将领域模型聚合编译为求值可直接使用的只读编译模型。
     * <p>该方法只负责流程编排，语法解析与 AST 生成由专门适配器承载。
     */
    @Override
    public CompiledAuthorizationModel compile(AuthorizationModelAggregate aggregate) {
        Objects.requireNonNull(aggregate, "aggregate must not be null");
        return new CompiledAuthorizationModel(compileRelations(aggregate), compileConditions(aggregate));
    }

    /**
     * 编译授权模型聚合中的所有关系
     *
     * @param aggregate 授权模型聚合对象，包含所有类型定义
     * @return 返回一个有序的Map，键为关系名称，值为编译后的关系对象
     */
    private Map<String, CompiledRelation> compileRelations(AuthorizationModelAggregate aggregate) {
        Map<String, CompiledRelation> relations = new LinkedHashMap<>();
        for (TypeDefinition typeDefinition : aggregate.getTypeDefinitions()) {
            compileTypeRelations(typeDefinition, relations);
        }
        return relations;
    }

    /**
     * 编译类型定义中的关系，并将编译后的关系存入目标映射中。
     *
     * @param typeDefinition 包含关系定义的类型定义对象
     * @param target         存储编译后关系的目标映射，键为关系键，值为编译后的关系
     */
    private void compileTypeRelations(TypeDefinition typeDefinition, Map<String, CompiledRelation> target) {
        String resourceType = typeDefinition.getSubjectType();
        for (RelationDefinition relationDefinition : typeDefinition.getRelations().values()) {
            CompiledRelation compiledRelation = buildCompiledRelation(resourceType, relationDefinition);
            target.put(relationKey(resourceType, relationDefinition.relationName()), compiledRelation);
        }
    }

    /**
     * 构建并返回一个编译后的关系对象。
     *
     * @param resourceType       资源类型
     * @param relationDefinition 关系定义对象，包含关系名称、重写表达式和限制条件
     * @return 编译后的关系对象，包含资源类型、关系名称、重写节点和限制条件
     */
    private CompiledRelation buildCompiledRelation(String resourceType, RelationDefinition relationDefinition) {
        RewriteNode rewriteNode = compileRewrite(resourceType, relationDefinition.rewriteExpression());
        return new CompiledRelation(
                resourceType,
                relationDefinition.relationName(),
                rewriteNode,
                relationDefinition.restrictions()
        );
    }

    /**
     * 根据资源类型和关系名称生成关系键
     *
     * @param resourceType 资源类型
     * @param relationName 关系名称
     * @return 组合后的关系键，格式为"resourceType#relationName"
     */
    private String relationKey(String resourceType, String relationName) {
        return resourceType + "#" + relationName;
    }

    /**
     * 编译并聚合条件定义
     * <p>
     * 将授权模型聚合中的条件定义转换为以ID为键的映射表，便于后续快速查找。
     *
     * @param aggregate 授权模型聚合对象，包含需要编译的条件定义集合
     * @return 包含所有条件定义的映射表，键为条件定义ID，值为对应的条件定义对象
     */
    private Map<Long, ConditionDefinition> compileConditions(AuthorizationModelAggregate aggregate) {
        Map<Long, ConditionDefinition> conditions = new LinkedHashMap<>();
        for (ConditionDefinition definition : aggregate.getConditionDefinitions()) {
            conditions.put(definition.getId(), definition);
        }
        return conditions;
    }

    /**
     * 编译重写表达式
     *
     * @param resourceType 资源类型，用于指定表达式适用的资源范围
     * @param expression   需要编译的重写表达式字符串，可能包含\t、\r和\n等特殊字符
     * @return 编译后的RewriteNode对象，表示解析后的重写规则节点
     */
    private RewriteNode compileRewrite(String resourceType, String expression) {
        return rewriteExpressionParser.parse(resourceType, expression);
    }
}
