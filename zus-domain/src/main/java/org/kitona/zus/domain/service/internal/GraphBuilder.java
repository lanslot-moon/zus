package org.kitona.zus.domain.service.internal;

/**
 * 授权模型图构建器
 * <p>
 * 用于构建授权模型的有向图结构。
 * 该类供 {@link org.kitona.zus.domain.port.outbound.IModelCompiler} 实现使用。
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
public class GraphBuilder {

    private final DirectedGraph graph = new DirectedGraph();

    /**
     * 获取或添加节点
     *
     * @param id    节点唯一标识
     * @param label 显示标签
     * @param type  节点类型
     * @return 节点
     */
    public GraphNode getOrAddNode(String id, String label, NodeType type) {
        return graph.addNode(id, label, type);
    }

    /**
     * 添加从 from 到 to 的有向边
     *
     * @param from 起始节点
     * @param to   目标节点
     */
    public void addEdge(GraphNode from, GraphNode to) {
        graph.addEdge(from.id(), to.id());
    }

    /**
     * 构建并返回有向图
     *
     * @return 构建完成的有向图
     */
    public DirectedGraph build() {
        return graph;
    }
}
