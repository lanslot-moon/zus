package org.kitona.zus.business.entity.graph;


public class AuthorizationModelGraphBuilder {

    private final DirectedGraph graph = new DirectedGraph();

    public GraphNode getOrAddNode(String id, String label, NodeType type) {
        return graph.addNode(id, label, type);
    }

    public void addEdge(GraphNode from, GraphNode to) {
        graph.addEdge(from.id(), to.id());
    }

    public DirectedGraph build() {
        return graph;
    }
}
