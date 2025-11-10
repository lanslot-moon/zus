//package org.kitona.zus.business.entity.bo;
//
//import java.util.*;
//import java.util.concurrent.ConcurrentHashMap;
//
///**
// * 基于 AuthorizationModelGraph + RelationTuple 列表的图计算引擎（Check）
// * <p>
// * 设计理念：
// * - modelGraph.graph().getEdges() 提供模型级别的依赖（节点如 "document#viewer" -> children { "self", "writer", "tupleToUserset:..." }）
// * - tupleIndex 存储实例级别绑定：key = object + "#" + relation -> subjects (user:alice 或 folder:1 等)
// * - resolve(...) 做 DFS 解析，遇到 tupleToUserset 时做跨对象跳转
// */
//public class ModelGraphEvaluator {
//
//    private final AuthorizationModelGraph modelGraph;
//
//    // tupleIndex: object#relation -> set of subjects (subject 可为 user:xxx 或 objectType:id)
//    private final Map<String, Set<String>> tupleIndex = new ConcurrentHashMap<>();
//
//    // memoization: object#relation#user -> result
//    private final Map<String, Boolean> memo = new ConcurrentHashMap<>();
//
//    // 最大递归深度保护
//    private static final int MAX_DEPTH = 256;
//
//    public ModelGraphEvaluator(AuthorizationModelGraph modelGraph, List<RelationTuple> tuples) {
//        this.modelGraph = Objects.requireNonNull(modelGraph);
//        indexTuples(tuples);
//    }
//
//    /* ---------- tuple 索引维护 ---------- */
//
//    private void indexTuples(List<RelationTuple> tuples) {
//        tupleIndex.clear();
//        if (tuples == null) return;
//        for (RelationTuple t : tuples) {
//            addTupleToIndex(t);
//        }
//    }
//
//    public void addTupleToIndex(RelationTuple t) {
//        if (t == null) return;
//        String key = makeObjRelKey(t.resourceType() + ":" + t.resourceId(), t.relationName());
//        tupleIndex.computeIfAbsent(key, k -> Collections.newSetFromMap(new ConcurrentHashMap<>())).add(t.user());
//    }
//
//    public void removeTupleFromIndex(RelationTuple t) {
//        if (t == null) return;
//        String key = makeObjRelKey(t.resourceType() + ":" + t.resourceId(), t.relationName());
//        Set<String> s = tupleIndex.get(key);
//        if (s != null && s.isEmpty()) {
//            tupleIndex.remove(key);
//        }
//        if (s != null) {
//            s.remove(t.user());
//        }
//    }
//
//    public void reloadTuples(List<RelationTuple> tuples) {
//        indexTuples(tuples);
//        memo.clear();
//    }
//
//    /* ---------- 公共 Check API ---------- */
//
//    /**
//     * 检查 user 是否对 object 的 relation 有权限。
//     *
//     * @param object   如 "document:2"
//     * @param relation 如 "viewer"
//     * @param user     如 "user:alice"
//     * @return true 表示授权成立
//     */
//    public boolean check(String user, String object, String relation) {
//        memo.clear(); // 每次 check 清空 memo（根据需求可改为全局 cache 并在写入时失效）
//        return resolve(object, relation, user, new HashSet<>(), 0);
//    }
//
//    /* ---------- 核心解析算法 ---------- */
//
//    /**
//     *
//     * 支持 rewriteExpression 中的几种情形（简化语法）：
//     * - "self" 或 ""                -> 直接匹配 tuple
//     * - "self or <rel>"             -> computedUserset（组合，或语义）
//     * - "tupleToUserset: from=type#rel; to=rel" -> tupleToUserset 跨对象跳转
//     * - 也支持直接 "otherType#otherRel" 形式的引用
//     */
//    private boolean resolve(String object, String relation, String user, Set<String> visited, int depth) {
//        if (depth > MAX_DEPTH) return false;
//
//        // 标准化 key
//        String memoKey = object + "#" + relation + "#" + user;
//        if (memo.containsKey(memoKey)) {
//            return memo.get(memoKey);
//        }
//
//        // 防环：每个 (object#relation#user) 仅访问一次
//        if (visited.contains(memoKey)) {
//            memo.put(memoKey, false);
//            return false;
//        }
//        visited.add(memoKey);
//
//        // 1) this/self: 直接在 tupleIndex 中查找 user
//        String objRel = makeObjRelKey(object, relation);
//        Set<String> subjects = tupleIndex.getOrDefault(objRel, Collections.emptySet());
//        if (subjects.contains(user)) {
//            memo.put(memoKey, true);
//            return true;
//        }
//
//        // 2) 从模型图中查找该类型 relation 的子表达（模型图节点 key = type#relation）
//        String[] objParts = object.split(":", 2);
//        if (objParts.length != 2) {
//            memo.put(memoKey, false);
//            return false;
//        }
//        String resourceType = objParts[0];
//        String modelNode = resourceType + "#" + relation;
//
//        Map<String, Set<String>> edges = modelGraph.graph().getEdges();
//        Set<String> children = edges.getOrDefault(modelNode, Collections.emptySet());
//
//        // 若模型中没有定义该 relation，则没有进一步路径
//        if (children.isEmpty()) {
//            memo.put(memoKey, false);
//            return false;
//        }
//
//        // 遍历模型图上每个子 term（或/合并语义）
//        for (String child : children) {
//            if (child == null || child.isBlank()) continue;
//            String term = child.trim();
//
//            // 2.1 如果是 self/this，已经在第1步检查了，跳过
//            if (term.equalsIgnoreCase("self") || term.equalsIgnoreCase("this")) {
//                continue;
//            }
//
//            // 2.2 tupleToUserset：做跨对象映射
//            if (term.startsWith("tupleToUserset:")) {
//                Map<String, String> kv = parseKvList(term.substring("tupleToUserset:".length()));
//                String from = kv.get("from"); // e.g. document#parent
//                String to = kv.get("to");     // e.g. folder#owner or owner (relative)
//
//                if (from == null || to == null) continue;
//
//                // fromParts: e.g. document#parent -> relationOnThisObject = "parent"
//                String[] fromParts = from.split("#", 2);
//                if (fromParts.length != 2) continue;
//                String fromRelationOnObject = fromParts[1];
//
//                // current object's fromRelation => 会得到 targets（这些 subjects 通常是其他对象，如 "folder:1"）
//                String curFromKey = makeObjRelKey(object, fromRelationOnObject);
//                Set<String> targets = tupleIndex.getOrDefault(curFromKey, Collections.emptySet());
//
//                // 对每个目标对象，计算 user 是否在目标对象的 toRelation 上
//                for (String targetObj : targets) {
//                    // 解析 to 字段，可以是 "folder#owner" 或 "owner"（相对）
//                    String targetRel;
//                    if (to.contains("#")) {
//                        String[] t = to.split("#", 2);
//                        // 若 to 给出 type，则忽略 type 校验（使用 targetObj 直接）
//                        targetRel = t[1];
//                    } else {
//                        // 相对关系，使用 targetObj 的类型
//                        targetRel = to;
//                    }
//                    if (resolve(targetObj, targetRel, user, visited, depth + 1)) {
//                        memo.put(memoKey, true);
//                        return true;
//                    }
//                }
//                continue;
//            }
//
//            // 2.3 如果 child 是跨关系引用（如 parentFolder#editor）
//            if (term.contains("#")) {
//                String[] t = term.split("#", 2);
//                String refType = t[0];
//                String refRel = t[1];
//
//                // 如果引用类型与当前对象类型一致，直接在当前对象上递归
//                if (refType.equals(resourceType)) {
//                    if (resolve(object, refRel, user, visited, depth + 1)) {
//                        memo.put(memoKey, true);
//                        return true;
//                    }
//                } else {
//                    // 否则，尝试查找当前 object 上是否存在 relation=refRel 指向 refType:ID 的 tuple，
//                    // 然后对每个目标对象再去检查 targetObj#refRel 或 targetObj#<to>（这是保守处理）
//                    String curKey = makeObjRelKey(object, refType);
//                    Set<String> targets = tupleIndex.getOrDefault(curKey, Collections.emptySet());
//                    for (String targetObj : targets) {
//                        // 只有当目标对象类型与 refType 匹配时才尝试（更严格）
//                        String[] targetParts = targetObj.split(":", 2);
//                        if (targetParts.length == 2 && targetParts[0].equals(refType)) {
//                            if (resolve(targetObj, refRel, user, visited, depth + 1)) {
//                                memo.put(memoKey, true);
//                                return true;
//                            }
//                        }
//                    }
//                }
//                continue;
//            }
//
//            // 2.4 普通相对 relation（例如 rewriteExpression 中的 "writer"）
//            if (resolve(object, term, user, visited, depth + 1)) {
//                memo.put(memoKey, true);
//                return true;
//            }
//        }
//
//        memo.put(memoKey, false);
//        return false;
//    }
//
//    /* ---------- 工具方法 ---------- */
//
//    private static String makeObjRelKey(String object, String relation) {
//        return object + "#" + relation;
//    }
//
//    private static Map<String, String> parseKvList(String s) {
//        Map<String, String> map = new HashMap<>();
//        if (s == null || s.isBlank()) return map;
//        String[] parts = s.split(";");
//        for (String p : parts) {
//            String[] kv = p.split("=", 2);
//            if (kv.length == 2) {
//                map.put(kv[0].trim(), kv[1].trim());
//            }
//        }
//        return map;
//    }
//}
