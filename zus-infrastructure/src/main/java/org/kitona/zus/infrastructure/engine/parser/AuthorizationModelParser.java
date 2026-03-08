package org.kitona.zus.infrastructure.engine.parser;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.kitona.zus.domain.valueobject.RelationDefinition;
import org.kitona.zus.domain.valueobject.TypeDefinition;
import org.kitona.zus.domain.valueobject.AuthorizationModel;
import org.kitona.zus.common.utils.JacksonUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * 将 typeDefinitions JSON（与基础设施层组装的格式一致）解析为 AuthorizationModel。
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
@Slf4j
public final class AuthorizationModelParser {

    private AuthorizationModelParser() {
    }

    /**
     * 解析 typeDefinitions JSON 为 AuthorizationModel。格式为 [ { "type": "document", "relations": { "viewer": { "rewrite": "self or ...", "allowedTypes": ["user"] } } } ]。
     *
     * @param typeDefinitionsJson 与基础设施层组装的 typeDefinitions JSON 一致
     * @return 解析后的授权模型，解析失败或为空时返回空模型
     */
    public static AuthorizationModel parseTypeDefinitionsJson(String typeDefinitionsJson) {
        if (typeDefinitionsJson == null || typeDefinitionsJson.isBlank()) {
            return new AuthorizationModel(Collections.emptyList());
        }
        JsonNode root = JacksonUtil.parseJSONObject(typeDefinitionsJson);
        if (root == null || !root.isArray()) {
            log.warn("typeDefinitions 不是 JSON 数组: {}", typeDefinitionsJson);
            return new AuthorizationModel(Collections.emptyList());
        }
        List<TypeDefinition> list = new ArrayList<>();
        for (JsonNode node : root) {
            TypeDefinition td = parseOneType(node);
            if (td != null) {
                list.add(td);
            }
        }
        return new AuthorizationModel(list);
    }

    private static TypeDefinition parseOneType(JsonNode node) {
        JsonNode typeNode = node.get("type");
        JsonNode relationsNode = node.get("relations");
        if (typeNode == null || !typeNode.isTextual() || relationsNode == null || !relationsNode.isObject()) {
            return null;
        }
        String resourceType = typeNode.asText();
        Map<String, RelationDefinition> relations = new LinkedHashMap<>();
        Map<String, Set<String>> relationRestrictions = new LinkedHashMap<>();
        relationsNode.fields().forEachRemaining(entry -> {
            String relationName = entry.getKey();
            JsonNode relNode = entry.getValue();
            if (!relNode.isObject()) {
                return;
            }
            String rewrite = "self";
            JsonNode rewriteNode = relNode.get("rewrite");
            if (rewriteNode != null && rewriteNode.isTextual()) {
                rewrite = rewriteNode.asText();
            }
            relations.put(relationName, new RelationDefinition(relationName, rewrite));
            JsonNode allowedNode = relNode.get("allowedTypes");
            if (allowedNode != null && allowedNode.isArray()) {
                Set<String> allowed = StreamSupport.stream(allowedNode.spliterator(), false)
                        .filter(JsonNode::isTextual)
                        .map(JsonNode::asText)
                        .collect(Collectors.toSet());
                if (!allowed.isEmpty()) {
                    relationRestrictions.put(relationName, allowed);
                }
            }
        });
        return new TypeDefinition(resourceType, relations, relationRestrictions);
    }
}
