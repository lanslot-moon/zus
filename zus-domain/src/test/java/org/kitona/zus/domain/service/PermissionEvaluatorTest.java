package org.kitona.zus.domain.service;

import org.junit.jupiter.api.Test;
import org.kitona.zus.domain.authorization.model.ConditionDefinition;
import org.kitona.zus.domain.authorization.tuple.RelationTuple;
import org.kitona.zus.domain.authorization.tuple.TupleCondition;
import org.kitona.zus.domain.authorization.tuple.TupleKey;
import org.kitona.zus.domain.authorization.evaluation.compiled.CompiledAuthorizationModel;
import org.kitona.zus.domain.authorization.evaluation.compiled.CompiledRelation;
import org.kitona.zus.domain.authorization.evaluation.nodes.ComputedUsersetNode;
import org.kitona.zus.domain.authorization.evaluation.nodes.DirectRelationReferenceNode;
import org.kitona.zus.domain.authorization.evaluation.runtime.EvaluationRequest;
import org.kitona.zus.domain.authorization.evaluation.nodes.ExclusionNode;
import org.kitona.zus.domain.authorization.evaluation.nodes.IntersectionNode;
import org.kitona.zus.domain.authorization.evaluation.nodes.RewriteNode;
import org.kitona.zus.domain.authorization.evaluation.nodes.SelfNode;
import org.kitona.zus.domain.authorization.evaluation.runtime.TupleMatchContext;
import org.kitona.zus.domain.authorization.evaluation.nodes.TupleToUsersetNode;
import org.kitona.zus.domain.authorization.evaluation.nodes.UnionNode;
import org.kitona.zus.domain.port.IConditionEvaluator;
import org.kitona.zus.domain.port.IDirectTupleReader;
import org.kitona.zus.domain.port.IObjectSubjectCandidateReader;
import org.kitona.zus.domain.port.ISubjectObjectCandidateReader;
import org.kitona.zus.domain.port.ITupleLinkReader;
import org.kitona.zus.domain.read.criteria.TupleQueryCriteria;
import org.kitona.zus.domain.repository.ITupleQueryRepository;
import org.kitona.zus.domain.valueobject.ObjectRef;
import org.kitona.zus.domain.valueobject.Subject;
import org.kitona.zus.domain.valueobject.Zookie;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PermissionEvaluatorTest {

    private static final long OFFICE_HOURS_CONDITION_ID = 1001L;

    @Test
    void shouldEvaluateUnionIntersectionAndExclusion() {
        InMemoryTupleQueryRepository tupleRepository = new InMemoryTupleQueryRepository(List.of(
                tuple("store", "document", "doc-1", "viewer", Subject.user("user", "alice")),
                tuple("store", "document", "doc-1", "editor", Subject.user("user", "alice")),
                tuple("store", "document", "doc-1", "banned", Subject.user("user", "bob")),
                tuple("store", "document", "doc-1", "viewer", Subject.user("user", "bob"))
        ));
        PermissionEvaluator evaluator = evaluator(tupleRepository, new AllowAllConditionEvaluator());

        CompiledAuthorizationModel model = model(Map.of(
                relationKey("document", "viewer"), relation("document", "viewer", new SelfNode()),
                relationKey("document", "editor"), relation("document", "editor", new SelfNode()),
                relationKey("document", "banned"), relation("document", "banned", new SelfNode()),
                relationKey("document", "visible"), relation("document", "visible",
                        new UnionNode(List.of(new DirectRelationReferenceNode("viewer"), new DirectRelationReferenceNode("editor")))),
                relationKey("document", "collaborator"), relation("document", "collaborator",
                        new IntersectionNode(List.of(new DirectRelationReferenceNode("viewer"), new DirectRelationReferenceNode("editor")))),
                relationKey("document", "allowed"), relation("document", "allowed",
                        new ExclusionNode(new DirectRelationReferenceNode("visible"), new DirectRelationReferenceNode("banned")))
        ), Map.of());

        assertTrue(evaluator.check(model, request("store", Subject.user("user", "alice"), ObjectRef.of("document", "doc-1"), "visible")));
        assertTrue(evaluator.check(model, request("store", Subject.user("user", "alice"), ObjectRef.of("document", "doc-1"), "collaborator")));
        assertTrue(evaluator.check(model, request("store", Subject.user("user", "alice"), ObjectRef.of("document", "doc-1"), "allowed")));
        assertTrue(evaluator.check(model, request("store", Subject.user("user", "bob"), ObjectRef.of("document", "doc-1"), "visible")));
        assertFalse(evaluator.check(model, request("store", Subject.user("user", "bob"), ObjectRef.of("document", "doc-1"), "collaborator")));
        assertFalse(evaluator.check(model, request("store", Subject.user("user", "bob"), ObjectRef.of("document", "doc-1"), "allowed")));
    }

    @Test
    void shouldEvaluateComputedUsersetAndTupleToUserset() {
        InMemoryTupleQueryRepository tupleRepository = new InMemoryTupleQueryRepository(List.of(
                tuple("store", "document", "doc-1", "editor", Subject.user("user", "alice")),
                tuple("store", "document", "doc-1", "parent", Subject.user("folder", "folder-1")),
                tuple("store", "folder", "folder-1", "viewer", Subject.user("user", "alice"))
        ));
        PermissionEvaluator evaluator = evaluator(tupleRepository, new AllowAllConditionEvaluator());

        CompiledAuthorizationModel model = model(Map.of(
                relationKey("document", "editor"), relation("document", "editor", new SelfNode()),
                relationKey("document", "can_edit"), relation("document", "can_edit",
                        new ComputedUsersetNode("document", "editor")),
                relationKey("document", "parent"), relation("document", "parent", new SelfNode()),
                relationKey("document", "viewer"), relation("document", "viewer",
                        new TupleToUsersetNode("parent", "viewer")),
                relationKey("folder", "viewer"), relation("folder", "viewer", new SelfNode())
        ), Map.of());

        EvaluationRequest editRequest = request("store", Subject.user("user", "alice"),
                ObjectRef.of("document", "doc-1"), "can_edit");
        EvaluationRequest viewRequest = request("store", Subject.user("user", "alice"),
                ObjectRef.of("document", "doc-1"), "viewer");

        assertTrue(evaluator.check(model, editRequest));
        assertTrue(evaluator.check(model, viewRequest));
    }

    @Test
    void shouldRespectSubjectRelationWildcardExpirationAndCondition() {
        long now = System.currentTimeMillis();
        InMemoryTupleQueryRepository tupleRepository = new InMemoryTupleQueryRepository(List.of(
                tuple("store", "document", "doc-1", "viewer", Subject.userset("group", "eng", "member")),
                tuple("store", "document", "doc-1", "viewer", Subject.wildcard("user")),
                tuple("store", "document", "doc-2", "viewer", Subject.user("user", "alice"), TupleCondition.EMPTY, now - 1000L),
                tuple("store", "document", "doc-3", "viewer", Subject.user("user", "alice"),
                        TupleCondition.of(OFFICE_HOURS_CONDITION_ID, "office_hours", "{\"required\":true}"), null)
        ));
        PermissionEvaluator evaluator = evaluator(tupleRepository, new RequestFlagConditionEvaluator());

        CompiledAuthorizationModel model = model(Map.of(
                relationKey("document", "viewer"), relation("document", "viewer", new SelfNode())
        ), Map.of(
                OFFICE_HOURS_CONDITION_ID,
                ConditionDefinition.reconstitute(OFFICE_HOURS_CONDITION_ID, "office_hours", "context.allow == true", "{\"required\":true}", "test")
        ));

        assertTrue(evaluator.check(model, request("store", Subject.userset("group", "eng", "member"),
                ObjectRef.of("document", "doc-1"), "viewer")));
        assertTrue(evaluator.check(model, request("store", Subject.user("user", "bob"),
                ObjectRef.of("document", "doc-1"), "viewer")));
        assertFalse(evaluator.check(model, request("store", Subject.user("user", "alice"),
                ObjectRef.of("document", "doc-2"), "viewer")));
        assertFalse(evaluator.check(model, request("store", Subject.user("user", "alice"),
                ObjectRef.of("document", "doc-3"), "viewer")));
        assertTrue(evaluator.check(model, request("store", Subject.user("user", "alice"),
                ObjectRef.of("document", "doc-3"), "viewer", Map.of("allow", true))));
    }

    @Test
    void shouldStopOnCyclesAndDepthLimit() {
        InMemoryTupleQueryRepository tupleRepository = new InMemoryTupleQueryRepository(List.of(
                tuple("store", "document", "doc-1", "owner", Subject.user("user", "alice"))
        ));
        PermissionEvaluator evaluator = evaluator(tupleRepository, new AllowAllConditionEvaluator(), 1);

        CompiledAuthorizationModel cyclicModel = model(Map.of(
                relationKey("document", "viewer"), relation("document", "viewer", new DirectRelationReferenceNode("editor")),
                relationKey("document", "editor"), relation("document", "editor", new DirectRelationReferenceNode("viewer"))
        ), Map.of());
        CompiledAuthorizationModel deepModel = model(Map.of(
                relationKey("document", "viewer"), relation("document", "viewer", new DirectRelationReferenceNode("editor")),
                relationKey("document", "editor"), relation("document", "editor", new DirectRelationReferenceNode("owner")),
                relationKey("document", "owner"), relation("document", "owner", new SelfNode())
        ), Map.of());

        assertFalse(evaluator.check(cyclicModel, request("store", Subject.user("user", "alice"),
                ObjectRef.of("document", "doc-1"), "viewer")));
        assertFalse(evaluator.check(deepModel, request("store", Subject.user("user", "alice"),
                ObjectRef.of("document", "doc-1"), "viewer")));
    }

    @Test
    void shouldListObjectsAndUsersUsingUnifiedEvaluator() {
        InMemoryTupleQueryRepository tupleRepository = new InMemoryTupleQueryRepository(List.of(
                tuple("store", "document", "doc-1", "viewer", Subject.user("user", "alice")),
                tuple("store", "document", "doc-2", "viewer", Subject.user("user", "bob")),
                tuple("store", "document", "doc-3", "viewer", Subject.user("user", "alice"))
        ));
        PermissionEvaluator evaluator = evaluator(tupleRepository, new AllowAllConditionEvaluator());
        CompiledAuthorizationModel model = model(Map.of(
                relationKey("document", "viewer"), relation("document", "viewer", new SelfNode())
        ), Map.of());

        List<String> objects = evaluator.listObjects(model, request("store", Subject.user("user", "alice"),
                ObjectRef.of("document", "*"), "viewer"), "document");
        List<Subject> users = evaluator.listUsers(model, request("store", Subject.wildcard("user"),
                ObjectRef.of("document", "doc-1"), "viewer"));

        assertIterableEquals(List.of("document:doc-1", "document:doc-3"), objects);
        assertEquals(List.of(Subject.user("user", "alice")), users);
    }

    @Test
    void shouldRespectZookieWhenEnumeratingCandidates() {
        InMemoryTupleQueryRepository tupleRepository = new InMemoryTupleQueryRepository(List.of(
                tuple("store", "document", "doc-1", "viewer", Subject.user("user", "alice"), TupleCondition.EMPTY, null, 1L),
                tuple("store", "document", "doc-2", "viewer", Subject.user("user", "alice"), TupleCondition.EMPTY, null, 2L)
        ));
        PermissionEvaluator evaluator = evaluator(tupleRepository, new AllowAllConditionEvaluator());
        CompiledAuthorizationModel model = model(Map.of(
                relationKey("document", "viewer"), relation("document", "viewer", new SelfNode())
        ), Map.of());

        List<String> objects = evaluator.listObjects(
                model,
                EvaluationRequest.of("store", Subject.user("user", "alice"), ObjectRef.of("document", "*"),
                        "viewer", Zookie.of(1L), Map.of()),
                "document");

        assertIterableEquals(List.of("document:doc-1"), objects);
    }

    @Test
    void shouldRespectRelationRestrictions() {
        InMemoryTupleQueryRepository tupleRepository = new InMemoryTupleQueryRepository(List.of(
                tuple("store", "document", "doc-1", "viewer", Subject.user("user", "alice")),
                tuple("store", "document", "doc-1", "viewer", Subject.userset("group", "eng", "member")),
                tuple("store", "document", "doc-1", "public_viewer", Subject.wildcard("user"))
        ));
        PermissionEvaluator evaluator = evaluator(tupleRepository, new AllowAllConditionEvaluator());
        CompiledAuthorizationModel model = model(Map.of(
                relationKey("document", "viewer"), new CompiledRelation("document", "viewer", new SelfNode(), Set.of("group#member")),
                relationKey("document", "public_viewer"), new CompiledRelation("document", "public_viewer", new SelfNode(), Set.of("user:*"))
        ), Map.of());

        assertFalse(evaluator.check(model, request("store", Subject.user("user", "alice"),
                ObjectRef.of("document", "doc-1"), "viewer")));
        assertTrue(evaluator.check(model, request("store", Subject.userset("group", "eng", "member"),
                ObjectRef.of("document", "doc-1"), "viewer")));
        assertTrue(evaluator.check(model, request("store", Subject.user("user", "bob"),
                ObjectRef.of("document", "doc-1"), "public_viewer")));
    }

    private static EvaluationRequest request(String storeId, Subject subject, ObjectRef object, String relation) {
        return request(storeId, subject, object, relation, Map.of());
    }

    private static PermissionEvaluator evaluator(InMemoryTupleQueryRepository repository,
                                                 IConditionEvaluator conditionEvaluator) {
        return evaluator(repository, conditionEvaluator, 32);
    }

    private static PermissionEvaluator evaluator(InMemoryTupleQueryRepository repository,
                                                 IConditionEvaluator conditionEvaluator,
                                                 int maxDepth) {
        return new PermissionEvaluator(repository, repository, repository, repository, conditionEvaluator, maxDepth);
    }

    private static EvaluationRequest request(String storeId, Subject subject, ObjectRef object,
                                             String relation, Map<String, Object> context) {
        return EvaluationRequest.of(storeId, subject, object, relation, Zookie.EMPTY, context);
    }

    private static CompiledAuthorizationModel model(Map<String, CompiledRelation> relations,
                                                    Map<Long, ConditionDefinition> conditions) {
        return new CompiledAuthorizationModel(new LinkedHashMap<>(relations), new LinkedHashMap<>(conditions));
    }

    private static CompiledRelation relation(String resourceType, String relationName, RewriteNode node) {
        return new CompiledRelation(resourceType, relationName, node, Set.of());
    }

    private static String relationKey(String resourceType, String relationName) {
        return resourceType + "#" + relationName;
    }

    private static RelationTuple tuple(String storeId, String objectType, String objectId,
                                             String relation, Subject subject) {
        return tuple(storeId, objectType, objectId, relation, subject, TupleCondition.EMPTY, null);
    }

    private static RelationTuple tuple(String storeId, String objectType, String objectId,
                                             String relation, Subject subject, TupleCondition condition,
                                             Long expiresAt) {
        return tuple(storeId, objectType, objectId, relation, subject, condition, expiresAt, 1L);
    }

    private static RelationTuple tuple(String storeId, String objectType, String objectId,
                                             String relation, Subject subject, TupleCondition condition,
                                             Long expiresAt, Long zookieVersion) {
        return RelationTuple.reconstitute(
                null,
                storeId,
                TupleKey.of(ObjectRef.of(objectType, objectId), relation, subject),
                Zookie.of(zookieVersion),
                condition,
                expiresAt,
                subject.isWildcard(),
                1L
        );
    }

    private static final class InMemoryTupleQueryRepository implements ITupleQueryRepository, IDirectTupleReader,
            ITupleLinkReader, ISubjectObjectCandidateReader, IObjectSubjectCandidateReader {

        private final List<RelationTuple> tuples;

        private InMemoryTupleQueryRepository(List<RelationTuple> tuples) {
            this.tuples = new ArrayList<>(tuples);
        }

        @Override
        public List<RelationTuple> list(TupleQueryCriteria criteria) {
            return filter(criteria.storeId(), criteria.objectType(), criteria.objectId(), criteria.relation(),
                    criteria.subjectType(), criteria.subjectId(), criteria.subjectRelation(), criteria.maxZookie());
        }

        @Override
        public List<RelationTuple> findBySubject(TupleQueryCriteria criteria) {
            return filter(criteria.storeId(), criteria.objectType(), null, criteria.relation(),
                    criteria.subjectType(), criteria.subjectId(), criteria.subjectRelation(), criteria.maxZookie());
        }

        @Override
        public List<RelationTuple> findByObject(TupleQueryCriteria criteria) {
            return filter(criteria.storeId(), criteria.objectType(), criteria.objectId(), criteria.relation(),
                    null, null, null, criteria.maxZookie());
        }

        @Override
        public List<RelationTuple> findDirectTuples(String storeId, ObjectRef object, String relation, Long maxZookie) {
            return filter(storeId, object.getType(), object.getId(), relation, null, null, null, maxZookie);
        }

        @Override
        public List<RelationTuple> findTupleLinks(String storeId, ObjectRef object, String relation, Long maxZookie) {
            return filter(storeId, object.getType(), object.getId(), relation, null, null, null, maxZookie);
        }

        @Override
        public List<RelationTuple> listObjectCandidates(String storeId, String objectType, Long maxZookie) {
            return filter(storeId, objectType, null, null, null, null, null, maxZookie);
        }

        @Override
        public List<RelationTuple> listSubjectCandidates(String storeId, Long maxZookie) {
            return filter(storeId, null, null, null, null, null, null, maxZookie);
        }

        private List<RelationTuple> filter(String storeId, String objectType, String objectId,
                                                 String relation, String subjectType, String subjectId,
                                                 String subjectRelation, Long maxZookie) {
            return tuples.stream()
                    .filter(tuple -> matches(tuple.getStoreId(), storeId))
                    .filter(tuple -> matches(tuple.getObjectType(), objectType))
                    .filter(tuple -> matches(tuple.getObjectId(), objectId))
                    .filter(tuple -> matches(tuple.getRelation(), relation))
                    .filter(tuple -> matches(tuple.getSubjectType(), subjectType))
                    .filter(tuple -> matches(tuple.getSubjectId(), subjectId))
                    .filter(tuple -> matchesNormalized(tuple.getSubjectRelation(), subjectRelation))
                    .filter(tuple -> maxZookie == null || tuple.getZookie().getVersion() == null
                            || tuple.getZookie().getVersion() <= maxZookie)
                    .toList();
        }

        private boolean matches(String actual, String expected) {
            return expected == null || expected.equals(actual);
        }

        private boolean matchesNormalized(String actual, String expected) {
            return expected == null || normalize(actual).equals(normalize(expected));
        }

        private String normalize(String value) {
            return value == null ? "" : value;
        }
    }

    private static final class AllowAllConditionEvaluator implements IConditionEvaluator {

        @Override
        public boolean evaluate(ConditionDefinition definition, TupleMatchContext context) {
            return true;
        }
    }

    private static final class RequestFlagConditionEvaluator implements IConditionEvaluator {

        @Override
        public boolean evaluate(ConditionDefinition definition, TupleMatchContext context) {
            Object value = context.request().context().get("allow");
            return Boolean.TRUE.equals(value);
        }
    }
}
