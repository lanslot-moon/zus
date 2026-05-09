package org.kitona.zus.domain.service;

import org.kitona.zus.domain.authorization.evaluation.compiled.CompiledAuthorizationModel;
import org.kitona.zus.domain.authorization.evaluation.runtime.EvaluationRequest;
import org.kitona.zus.domain.authorization.tuple.RelationTuple;
import org.kitona.zus.domain.port.IObjectSubjectCandidateReader;
import org.kitona.zus.domain.port.ISubjectObjectCandidateReader;
import org.kitona.zus.domain.valueobject.ObjectRef;
import org.kitona.zus.domain.valueobject.Subject;
import org.kitona.zus.domain.valueobject.Zookie;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 权限关系搜索领域服务。
 *
 * <p>该服务负责 ListObjects / ListSubjects 这类关系枚举查询：
 * 先通过读侧候选端口缩小搜索空间，再对每个候选项复用 {@link PermissionCheckEvaluator}
 * 的 Check 内核做最终过滤。它不实现独立授权语义，避免搜索结果与单点 Check 发生漂移。
 */
public final class PermissionSearchEvaluator {

    /**
     * 单点权限证明器，负责判断一个明确的 subject-object-relation 命题是否成立。
     */
    private final PermissionCheckEvaluator permissionCheckEvaluator;

    /**
     * 从 subject 方向获取 object 候选的读端口。
     */
    private final ISubjectObjectCandidateReader subjectObjectCandidateReader;

    /**
     * 从 object 方向获取 subject 候选的读端口。
     */
    private final IObjectSubjectCandidateReader objectSubjectCandidateReader;

    /**
     * 创建权限关系搜索服务。
     *
     * @param permissionCheckEvaluator          单点权限证明器
     * @param subjectObjectCandidateReader object 候选读取端口
     * @param objectSubjectCandidateReader subject 候选读取端口
     */
    public PermissionSearchEvaluator(PermissionCheckEvaluator permissionCheckEvaluator,
                                     ISubjectObjectCandidateReader subjectObjectCandidateReader,
                                     IObjectSubjectCandidateReader objectSubjectCandidateReader) {
        this.permissionCheckEvaluator = permissionCheckEvaluator;
        this.subjectObjectCandidateReader = subjectObjectCandidateReader;
        this.objectSubjectCandidateReader = objectSubjectCandidateReader;
    }

    /**
     * 列出主体通过指定关系可访问的对象。
     *
     * <p>该方法不会构造 {@code object:*} 之类的占位对象。只有从候选端口取到真实 object 后，
     * 才会构造完整 {@link EvaluationRequest} 并交给 Check 内核证明。
     *
     * @param model      编译后的授权模型
     * @param storeId    store 标识
     * @param subject    查询主体
     * @param relation   查询关系
     * @param zookie     一致性令牌
     * @param context    条件求值上下文
     * @param objectType 可选 object 类型过滤
     * @return 主体可访问的 object type:id 列表
     */
    public List<String> listObjects(CompiledAuthorizationModel model, String storeId, Subject subject, String relation,
                                    Zookie zookie, Map<String, Object> context, String objectType) {
        Set<String> objects = new LinkedHashSet<>();
        Zookie effectiveZookie = effectiveZookie(zookie);
        for (ObjectRef candidate : collectObjectCandidates(storeId, objectType, effectiveZookie)) {
            EvaluationRequest request = EvaluationRequest.of(storeId, subject, candidate, relation, effectiveZookie, context);
            if (permissionCheckEvaluator.check(model, request)) {
                objects.add(candidate.toString());
            }
        }
        return List.copyOf(objects);
    }

    /**
     * 列出对指定对象关系具备权限的主体。
     *
     * <p>该方法不会构造 {@code subject:*} 之类的占位主体。只有从候选端口取到真实 subject 后，
     * 才会构造完整 {@link EvaluationRequest} 并交给 Check 内核证明。
     *
     * @param model    编译后的授权模型
     * @param storeId  store 标识
     * @param object   查询对象
     * @param relation 查询关系
     * @param zookie   一致性令牌
     * @param context  条件求值上下文
     * @return 对指定 object relation 具备权限的主体列表
     */
    public List<Subject> listSubjects(CompiledAuthorizationModel model, String storeId, ObjectRef object,
                                      String relation, Zookie zookie, Map<String, Object> context) {
        List<Subject> result = new ArrayList<>();
        Zookie effectiveZookie = effectiveZookie(zookie);
        for (Subject subject : collectSubjectCandidates(storeId, effectiveZookie)) {
            EvaluationRequest request = EvaluationRequest.of(storeId, subject, object, relation, effectiveZookie, context);
            if (permissionCheckEvaluator.check(model, request)) {
                result.add(subject);
            }
        }
        return List.copyOf(result);
    }

    /**
     * 从读侧端口获取对象候选集。
     */
    private List<ObjectRef> collectObjectCandidates(String storeId, String objectType, Zookie zookie) {
        return subjectObjectCandidateReader.listObjectCandidates(storeId, objectType, zookie.getVersion())
                .stream()
                .map(tuple -> ObjectRef.of(tuple.getObjectType(), tuple.getObjectId()))
                .distinct()
                .toList();
    }

    /**
     * 从读侧端口获取主体候选集。
     */
    private List<Subject> collectSubjectCandidates(String storeId, Zookie zookie) {
        return objectSubjectCandidateReader.listSubjectCandidates(storeId, zookie.getVersion())
                .stream()
                .map(RelationTuple::getSubject)
                .distinct()
                .toList();
    }

    /**
     * 归一化一致性令牌。
     */
    private Zookie effectiveZookie(Zookie zookie) {
        return zookie == null ? Zookie.EMPTY : zookie;
    }
}
