package org.kitona.zus.domain.authorization.evaluation.graph;

import org.jgrapht.Graph;
import org.jgrapht.alg.cycle.HawickJamesSimpleCycles;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.kitona.zus.domain.valueobject.Subject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 授权模型静态有向图——持有 JGraphT 图并提供路径分析和环检测。
 */
public record AuthorizationModelGraph(Graph<String, ModelEdge> delegate) {

    /**
     * 判断从 {@code from} 到 {@code to} 是否存在有向路径。
     */
    public boolean pathExists(String from, String to) {
        if (Objects.equals(from, to)) {
            return true;
        }
        return delegate.containsVertex(from) && delegate.containsVertex(to)
                && new DijkstraShortestPath<>(delegate).getPath(from, to) != null;
    }

    /**
     * 判断 subject 在图中是否存在到达 objectType#relation 的路径。
     */
    public boolean pathExists(Subject subject, String relation, String objectType) {
        String to = relationKey(objectType, relation);
        String from = subjectLabel(subject);
        if (pathExists(from, to)) {
            return true;
        }
        if (subject.isUserset()) {
            return false;
        }
        return pathExists(subject.getType() + Subject.RELATION_SEPARATOR + Subject.WILDCARD, to);
    }

    /**
     * 检测所有简单环并分类：编译期环（全 COMPUTED_USERSET 边，必定无限递归）
     * vs 运行期环（含 DIRECT 或 TUPLE_TO_USERSET 边，取决于 tuple 数据）。
     */
    public CycleReport findCycles() {
        List<List<String>> compileTime = new ArrayList<>();
        List<List<String>> runtime = new ArrayList<>();
        for (List<String> cycle : new HawickJamesSimpleCycles<>(delegate).findSimpleCycles()) {
            (isCompileTimeCycle(cycle) ? compileTime : runtime).add(cycle);
        }
        return new CycleReport(compileTime, runtime);
    }

    private boolean isCompileTimeCycle(List<String> cycle) {
        int n = cycle.size();
        for (int i = 0; i < n; i++) {
            for (ModelEdge e : delegate.getAllEdges(cycle.get(i), cycle.get((i + 1) % n))) {
                if (e.type() != GraphEdgeType.COMPUTED_USERSET) {
                    return false;
                }
            }
        }
        return true;
    }

    public record CycleReport(List<List<String>> compileTimeCycles, List<List<String>> runtimeCycles) {

        public boolean hasCompileTimeCycles() {
            return !compileTimeCycles.isEmpty();
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            if (!compileTimeCycles.isEmpty()) {
                sb.append("Compile-time cycles (always infinite, must be fixed):\n");
                compileTimeCycles.forEach(c ->
                        sb.append("  ").append(String.join(" → ", c)).append(" → ").append(c.get(0)).append("\n"));
            }
            if (!runtimeCycles.isEmpty()) {
                sb.append("Runtime cycles (tuple-dependent):\n");
                runtimeCycles.forEach(c ->
                        sb.append("  ").append(String.join(" → ", c)).append(" → ").append(c.get(0)).append("\n"));
            }
            return sb.toString();
        }
    }

    static String relationKey(String type, String relation) {
        return type + "#" + relation;
    }

    static String subjectLabel(Subject subject) {
        if (subject.isUserset()) {
            return subject.getType() + Subject.RELATION_SEPARATOR + subject.getRelation();
        }
        return subject.getType();
    }
}
