package org.kitona.zus.business.entity.antlr4;

import org.kitona.zus.business.entity.bo.ExprNode;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * RewriteEvaluator 类用于解析表达式抽象语法树(AST)中的所有依赖关系
 * 通过函数映射的方式为不同类型的表达式节点提供对应的处理逻辑
 */
public class RewriteEvaluator {

    /**
     * 使用函数映射来代替策略模式
     * evaluators Map存储不同节点类型与其对应的处理函数之间的映射关系
     */
    private final Map<Class<?>, Function<ExprNode, Set<String>>> evaluators = new HashMap<>();


    public RewriteEvaluator() {
        // 注册各个节点类型的处理函数
        evaluators.put(ExprNode.OrNode.class, this::evaluateOrNode);        // 注册OR节点的处理函数
        evaluators.put(ExprNode.AndNode.class, this::evaluateAndNode);      // 注册AND节点的处理函数
        evaluators.put(ExprNode.TupleToUsersetNode.class, this::evaluateTupleToUsersetNode);  // 注册TupleToUserset节点的处理函数
        evaluators.put(ExprNode.FromNode.class, this::evaluateFromNode);    // 注册From节点的处理函数
        evaluators.put(ExprNode.RelationNode.class, this::evaluateRelationNode);  // 注册Relation节点的处理函数
        evaluators.put(ExprNode.ListNode.class, this::evaluateListNode);     // 注册List节点的处理函数
        evaluators.put(ExprNode.SelfNode.class, this::evaluateSelfNode);     // 注册Self节点的处理函数
    }

    /**
     * 从表达式 AST 中解析所有依赖关系（扁平化）
     *
     * @param node 当前表达式节点
     * @return 依赖的 "type#relation" 集合
     * @throws IllegalStateException 当找不到对应节点类型的处理函数时抛出
     */
    public Set<String> evaluate(ExprNode node) {
        if (node == null) {    // 处理空节点情况
            return Set.of();
        }

        // 根据节点类型选择合适的处理函数
        Function<ExprNode, Set<String>> evaluator = evaluators.get(node.getClass());
        if (evaluator == null) {
            throw new IllegalStateException("No evaluator found for node type: " + node.getClass());
        }

        return evaluator.apply(node);
    }

    // 处理 OR 节点：合并所有子节点的依赖关系
    private Set<String> evaluateOrNode(ExprNode node) {
        ExprNode.OrNode orNode = (ExprNode.OrNode) node;
        return orNode.children().stream()
                .flatMap(child -> evaluate(child).stream())
                .collect(Collectors.toSet());
    }

    // 处理 AND 节点：合并所有子节点的依赖关系
    private Set<String> evaluateAndNode(ExprNode node) {
        ExprNode.AndNode andNode = (ExprNode.AndNode) node;
        return andNode.children().stream()
                .flatMap(child -> evaluate(child).stream())
                .collect(Collectors.toSet());
    }

    // 处理 tupleToUserset 节点：返回from和to的组合关系
    private Set<String> evaluateTupleToUsersetNode(ExprNode node) {
        ExprNode.TupleToUsersetNode tupleNode = (ExprNode.TupleToUsersetNode) node;
        if (tupleNode.from() != null && tupleNode.to() != null) {
            return Set.of(tupleNode.from() + "#" + tupleNode.to());
        }
        return Collections.emptySet();
    }

    // 处理 From 节点：处理类型转换关系
    private Set<String> evaluateFromNode(ExprNode node) {
        ExprNode.FromNode fromNode = (ExprNode.FromNode) node;
        Set<String> base = evaluate(fromNode.left());
        Set<String> refs = new HashSet<>();
        for (String rel : base) {
            String[] parts = rel.split("#");
            if (parts.length == 2) {
                refs.add(fromNode.fromType() + "#" + parts[1]);
                continue;
            }
            refs.add(fromNode.fromType() + "#" + rel);
        }
        return refs;
    }

    // 处理 Relation 节点：返回类型和关系的组合
    private Set<String> evaluateRelationNode(ExprNode node) {
        ExprNode.RelationNode relationNode = (ExprNode.RelationNode) node;
        if (relationNode.type() != null) {
            return Set.of(relationNode.type() + "#" + relationNode.relation());
        }
        return Set.of("currentType" + "#" + relationNode.relation());
    }

    // 处理 List 节点
    private Set<String> evaluateListNode(ExprNode node) {
        ExprNode.ListNode listNode = (ExprNode.ListNode) node;
        return listNode.elements().stream()
                .flatMap(item -> evaluate(item).stream())
                .collect(Collectors.toSet());
    }

    // 处理 SELF 节点
    private Set<String> evaluateSelfNode(ExprNode node) {
        return Collections.emptySet();
    }
}
