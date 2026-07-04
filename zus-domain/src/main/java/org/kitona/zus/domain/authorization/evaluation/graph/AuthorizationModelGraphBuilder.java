package org.kitona.zus.domain.authorization.evaluation.graph;

import org.jgrapht.Graph;
import org.jgrapht.graph.builder.GraphTypeBuilder;
import org.kitona.zus.domain.authorization.evaluation.compiled.CompiledRelation;
import org.kitona.zus.domain.authorization.evaluation.nodes.ComputedUsersetNode;
import org.kitona.zus.domain.authorization.evaluation.nodes.DirectRelationReferenceNode;
import org.kitona.zus.domain.authorization.evaluation.nodes.ExclusionNode;
import org.kitona.zus.domain.authorization.evaluation.nodes.IntersectionNode;
import org.kitona.zus.domain.authorization.evaluation.nodes.RewriteNode;
import org.kitona.zus.domain.authorization.evaluation.nodes.SelfNode;
import org.kitona.zus.domain.authorization.evaluation.nodes.TupleToUsersetNode;
import org.kitona.zus.domain.authorization.evaluation.nodes.UnionNode;
import org.kitona.zus.domain.valueobject.Subject;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * 授权模型图构建器——从已编译关系构建 JGraphT 多图。
 *
 * <p>每条 relation 只展开自己 rewrite 树的直接子节点，不递归展开其他 relation。
 * 跨 relation 的连接只放一条边，交由 BFS 串联。
 * union / intersection / exclusion 创建虚拟操作节点，对齐 OpenFGA 的 operator node 模式。
 */
public final class AuthorizationModelGraphBuilder {

    private AuthorizationModelGraphBuilder() {
    }

    public static Graph<String, ModelEdge> build(Map<String, CompiledRelation> relations) {
        Graph<String, ModelEdge> g = emptyGraph();
        for (CompiledRelation rel : relations.values()) {
            String key = relationKey(rel.resourceType(), rel.relationName());
            walkNode(rel.rewriteNode(), rel.restrictions(), rel.resourceType(), key, relations, g);
        }
        return g;
    }

    // ---- 内部工具 ----

    private static Graph<String, ModelEdge> emptyGraph() {
        return GraphTypeBuilder.<String, ModelEdge>directed().allowingMultipleEdges(true).allowingSelfLoops(true)
                .edgeClass(ModelEdge.class).buildGraph();
    }

    private static void addEdge(Graph<String, ModelEdge> g, String from, String to, GraphEdgeType type) {
        g.addVertex(from);
        g.addVertex(to);
        g.addEdge(from, to, new ModelEdge(from, to, type));
    }

    private static String relationKey(String type, String relation) {
        return type + "#" + relation;
    }

    // ---- AST 遍历 ----

    private static void walkNode(RewriteNode node, Set<String> restrictions,
                                 String resourceType, String key,
                                 Map<String, CompiledRelation> relations,
                                 Graph<String, ModelEdge> g) {
        if (node instanceof SelfNode) {
            restrictions.forEach(r -> addEdge(g, r, key, GraphEdgeType.DIRECT));
            return;
        }
        if (node instanceof DirectRelationReferenceNode n) {
            addEdge(g, relationKey(resourceType, n.relationName()), key, GraphEdgeType.COMPUTED_USERSET);
            return;
        }
        if (node instanceof ComputedUsersetNode n) {
            String type = n.resourceType() != null && !n.resourceType().isEmpty() ? n.resourceType() : resourceType;
            addEdge(g, relationKey(type, n.relationName()), key, GraphEdgeType.COMPUTED_USERSET);
            return;
        }
        if (node instanceof TupleToUsersetNode n) {
            Set<String> linkTypes = linkTypes(resourceType, n.tupleRelation(), relations);
            linkTypes.forEach(lt -> addEdge(g, relationKey(lt, n.computedRelation()), key, GraphEdgeType.TUPLE_TO_USERSET));
            return;
        }
        if (node instanceof UnionNode n) {
            n.children().forEach(child -> walkNode(child, restrictions, resourceType, key, relations, g));
            return;
        }
        if (node instanceof IntersectionNode n) {
            n.children().forEach(child -> walkNode(child, restrictions, resourceType, key, relations, g));
            return;
        }
        if (node instanceof ExclusionNode n) {
            walkNode(n.left(), restrictions, resourceType, key, relations, g);
        }
    }

    private static Set<String> linkTypes(String resourceType, String tupleRelation,
                                         Map<String, CompiledRelation> relations) {
        CompiledRelation tupleRel = relations.get(relationKey(resourceType, tupleRelation));
        if (tupleRel == null || tupleRel.restrictions().isEmpty()) {
            return Set.of();
        }
        Set<String> types = new LinkedHashSet<>();
        for (String r : tupleRel.restrictions()) {
            int idx = r.indexOf(Subject.RELATION_SEPARATOR);
            types.add(idx > 0 ? r.substring(0, idx) : r);
        }
        return types;
    }
}
