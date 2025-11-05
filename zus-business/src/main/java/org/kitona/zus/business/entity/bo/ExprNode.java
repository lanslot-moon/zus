package org.kitona.zus.business.entity.bo;

import java.util.*;

public sealed interface ExprNode permits ExprNode.OrNode, ExprNode.AndNode, ExprNode.FromNode,
                ExprNode.TupleToUsersetNode, ExprNode.ListNode, ExprNode.RelationNode, ExprNode.SelfNode {

    record RelationNode(String type, String relation) implements ExprNode {}

    record OrNode(List<ExprNode> children) implements ExprNode {}

    record AndNode(List<ExprNode> children) implements ExprNode {}

    record FromNode(ExprNode left, String fromType) implements ExprNode {}

    record TupleToUsersetNode(String from, String to) implements ExprNode {}

    record ListNode(List<ExprNode> elements) implements ExprNode {}

    record SelfNode() implements ExprNode {}
}
