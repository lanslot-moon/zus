package org.kitona.zus.service.dto.response;

import org.kitona.zus.domain.authorization.evaluation.explain.EvaluationExplainNode;
import org.kitona.zus.domain.authorization.evaluation.explain.EvaluationExplainReason;
import org.kitona.zus.domain.authorization.evaluation.explain.EvaluationExplainReason.NodeCompletionReason;
import org.kitona.zus.domain.authorization.evaluation.explain.TupleExplainDetail;
import org.kitona.zus.domain.valueobject.PermissionCheckStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Explain 展示叙事装配器。
 * <p>
 * 领域层只负责产生结构化证明树，应用 DTO 层负责把树转换为更适合调试页面展示的
 * 摘要和调用时序，避免把中文文案泄漏进领域模型。
 */
final class ExplainNarrativeAssembler {

    /**
     * 目标占位符。
     */
    private static final String TARGET_PLACEHOLDER = "{target}";

    /**
     * tuple 占位符。
     */
    private static final String TUPLE_PLACEHOLDER = "{tuple}";

    /**
     * object 与 relation 的分隔符。
     */
    private static final String RELATION_SEPARATOR = "#";

    /**
     * tuple object#relation 与 subject 的分隔符。
     */
    private static final String SUBJECT_SEPARATOR = "@";

    private ExplainNarrativeAssembler() {
    }

    /**
     * 生成 Explain 摘要。
     *
     * @param root    explain 根节点
     * @param allowed 最终是否允许
     * @return 面向调试者的摘要文案
     */
    static String summarize(EvaluationExplainNode root, boolean allowed) {
        if (root != null) {
            return messageOf(root);
        }
        return allowed ? PermissionCheckStatus.ALLOWED.name() : PermissionCheckStatus.DENIED.name();
    }

    /**
     * 生成 Explain 调用时序。
     *
     * @param root explain 根节点
     * @return 前序展开后的调用步骤
     */
    static List<ExplainTimelineStepDTO> timeline(EvaluationExplainNode root) {
        if (root == null) {
            return List.of();
        }
        List<ExplainTimelineStepDTO> steps = new ArrayList<>();
        appendStep(root, 0, steps);
        return List.copyOf(steps);
    }

    /**
     * 前序遍历 explain 树并生成 timeline 步骤。
     *
     * @param node  当前 explain 节点
     * @param depth 当前树深度
     * @param steps 输出步骤集合
     */
    private static void appendStep(EvaluationExplainNode node, int depth,
                                   List<ExplainTimelineStepDTO> steps) {
        steps.add(ExplainTimelineStepDTO.builder()
                .step(steps.size() + 1)
                .depth(depth)
                .nodeType(node.nodeType().name())
                .target(node.target())
                .subject(node.subject())
                .relation(node.relation())
                .status(node.allowed() ? PermissionCheckStatus.ALLOWED.name() : PermissionCheckStatus.DENIED.name())
                .allowed(node.allowed())
                .reason(node.reason() != null ? node.reason().name() : null)
                .message(messageOf(node))
                .tuple(node.tuple() != null ? formatTuple(node.tuple()) : null)
                .keyStep(isKeyStep(node))
                .build());
        node.children().forEach(child -> appendStep(child, depth + 1, steps));
    }

    /**
     * 生成单个节点的调试文案。
     *
     * @param node explain 节点
     * @return 节点文案
     */
    private static String messageOf(EvaluationExplainNode node) {
        EvaluationExplainReason reason = node.reason();
        if (reason == null) {
            return NodeCompletionReason.resolve(node.nodeType(), node.allowed()).getDesc().replace(TARGET_PLACEHOLDER, targetOf(node));
        }

        return reason.getDesc().replace(TARGET_PLACEHOLDER, targetOf(node)).replace(TUPLE_PLACEHOLDER, tupleOrTarget(node));
    }

    /**
     * 判断节点是否属于关键步骤。
     *
     * @param node explain 节点
     * @return 关键步骤返回 {@code true}
     */
    private static boolean isKeyStep(EvaluationExplainNode node) {
        EvaluationExplainReason reason = node.reason();
        return reason != null && reason.isKeyStep();
    }

    /**
     * 格式化 tuple 详情。
     *
     * @param tuple tuple 详情
     * @return tuple 字符串
     */
    private static String formatTuple(TupleExplainDetail tuple) {
        return tuple.object() + RELATION_SEPARATOR + tuple.relation() + SUBJECT_SEPARATOR + tuple.subject();
    }

    /**
     * 返回 tuple 文本；没有 tuple 时退回当前 target。
     *
     * @param node explain 节点
     * @return tuple 或 target 文本
     */
    private static String tupleOrTarget(EvaluationExplainNode node) {
        return node.tuple() == null ? targetOf(node) : formatTuple(node.tuple());
    }

    /**
     * 返回节点目标文本。
     *
     * @param node explain 节点
     * @return 目标文本
     */
    private static String targetOf(EvaluationExplainNode node) {
        if (node.target() != null && node.target().contains(RELATION_SEPARATOR)) {
            return node.target();
        }
        if (node.target() != null && node.relation() != null) {
            return node.target() + RELATION_SEPARATOR + node.relation();
        }
        if (node.target() != null) {
            return node.target();
        }
        return node.nodeType().name();
    }
}
