package org.kitona.zus.domain.authorization.evaluation.explain;

import org.junit.jupiter.api.Test;
import org.kitona.zus.domain.enums.EvaluationNodeType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.kitona.zus.domain.authorization.evaluation.explain.EvaluationExplainReason.BusinessEvidenceReason.CONDITION_FAILED;
import static org.kitona.zus.domain.authorization.evaluation.explain.EvaluationExplainReason.BusinessEvidenceReason.CONDITION_PASSED;
import static org.kitona.zus.domain.authorization.evaluation.explain.EvaluationExplainReason.BusinessEvidenceReason.DEPTH_LIMIT_EXCEEDED;
import static org.kitona.zus.domain.authorization.evaluation.explain.EvaluationExplainReason.BusinessEvidenceReason.DIRECT_TUPLE_MATCHED;
import static org.kitona.zus.domain.authorization.evaluation.explain.EvaluationExplainReason.BusinessEvidenceReason.NO_TUPLE_MATCHED;
import static org.kitona.zus.domain.authorization.evaluation.explain.EvaluationExplainReason.BusinessEvidenceReason.TRACE_TRUNCATED;
import static org.kitona.zus.domain.authorization.evaluation.explain.EvaluationExplainReason.NodeCompletionReason.EXCLUSION_LEFT_ALLOWED_RIGHT_DENIED;
import static org.kitona.zus.domain.authorization.evaluation.explain.EvaluationExplainReason.NodeCompletionReason.NODE_ALLOWED;
import static org.kitona.zus.domain.authorization.evaluation.explain.EvaluationExplainReason.NodeCompletionReason.NODE_DENIED;
import static org.kitona.zus.domain.authorization.evaluation.explain.EvaluationExplainReason.NodeCompletionReason.RELATION_ALLOWED;
import static org.kitona.zus.domain.authorization.evaluation.explain.EvaluationExplainReason.NodeCompletionReason.RELATION_DENIED;
import static org.kitona.zus.domain.authorization.evaluation.explain.EvaluationExplainReason.NodeCompletionReason.TUPLE_TO_USERSET_ALLOWED;
import static org.kitona.zus.domain.authorization.evaluation.explain.EvaluationExplainReason.NodeCompletionReason.TUPLE_TO_USERSET_DENIED;
import static org.kitona.zus.domain.authorization.evaluation.explain.EvaluationExplainReason.NodeCompletionReason.UNION_BRANCH_ALLOWED;
import static org.kitona.zus.domain.authorization.evaluation.explain.EvaluationExplainReason.NodeCompletionReason.UNION_NO_BRANCH_ALLOWED;

class EvaluationExplainReasonTest {

    @Test
    void shouldExposeWhetherReasonRepresentsSuccessfulProof() {
        assertTrue(RELATION_ALLOWED.isSuccess());
        assertTrue(DIRECT_TUPLE_MATCHED.isSuccess());
        assertTrue(UNION_BRANCH_ALLOWED.isSuccess());
        assertTrue(EXCLUSION_LEFT_ALLOWED_RIGHT_DENIED.isSuccess());
        assertTrue(CONDITION_PASSED.isSuccess());

        assertFalse(RELATION_DENIED.isSuccess());
        assertFalse(NO_TUPLE_MATCHED.isSuccess());
        assertFalse(CONDITION_FAILED.isSuccess());
        assertFalse(DEPTH_LIMIT_EXCEEDED.isSuccess());
        assertFalse(TRACE_TRUNCATED.isSuccess());
    }

    @Test
    void shouldResolveDefaultReasonByNodeTypeAndSuccess() {
        assertEquals(EvaluationNodeType.RELATION, RELATION_ALLOWED.getNodeType());
        assertEquals(EvaluationNodeType.UNION, UNION_BRANCH_ALLOWED.getNodeType());
        assertEquals(EvaluationNodeType.TUPLE, DIRECT_TUPLE_MATCHED.getNodeType());

        assertEquals(RELATION_ALLOWED,
                EvaluationExplainReason.defaultFor(EvaluationNodeType.RELATION, true));
        assertEquals(RELATION_DENIED,
                EvaluationExplainReason.defaultFor(EvaluationNodeType.RELATION, false));
        assertEquals(UNION_BRANCH_ALLOWED,
                EvaluationExplainReason.defaultFor(EvaluationNodeType.UNION, true));
        assertEquals(UNION_NO_BRANCH_ALLOWED,
                EvaluationExplainReason.defaultFor(EvaluationNodeType.UNION, false));
        assertEquals(TUPLE_TO_USERSET_ALLOWED,
                EvaluationExplainReason.defaultFor(EvaluationNodeType.TUPLE_TO_USERSET, true));
        assertEquals(NODE_DENIED,
                EvaluationExplainReason.defaultFor(EvaluationNodeType.SELF, false));
        assertEquals(NODE_ALLOWED,
                EvaluationExplainReason.defaultFor(EvaluationNodeType.TUPLE, true));
        assertEquals(NODE_DENIED,
                EvaluationExplainReason.defaultFor(EvaluationNodeType.TUPLE, false));
    }

    @Test
    void shouldDistinguishNodeCompletionReasonFromBusinessEvidenceReason() {
        assertTrue(RELATION_ALLOWED.isNodeCompletionReason());
        assertTrue(UNION_BRANCH_ALLOWED.isNodeCompletionReason());
        assertTrue(TUPLE_TO_USERSET_DENIED.isNodeCompletionReason());

        assertFalse(DIRECT_TUPLE_MATCHED.isNodeCompletionReason());
        assertFalse(CONDITION_FAILED.isNodeCompletionReason());
        assertFalse(DEPTH_LIMIT_EXCEEDED.isNodeCompletionReason());
        assertFalse(TRACE_TRUNCATED.isNodeCompletionReason());
    }
}
