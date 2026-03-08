package org.kitona.zus.domain.service.internal;

import org.kitona.zus.domain.valueobject.RelationDefinition;
import org.kitona.zus.domain.valueobject.TypeDefinition;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collector;

/**
 * 模型文本解析器
 * <p>
 * 用于解析 FGA (Fine-Grained Access) 格式的模型文本。
 * 该类提供了将模型文本解析为 TypeDefinition 列表的功能。
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
public final class ModelTextParser {

    private ModelTextParser() {
        throw new IllegalStateException("Utility class");
    }

    private static final String RELATION_NODE_NAME = "relations";

    /**
     * 用于匹配类型定义的正则表达式模式。
     * 匹配格式为 "type <word>" 的行，其中<word>是类型名称。
     */
    private static final Pattern TYPE_PATTERN = Pattern.compile("^type\\s+(\\w+)");

    /**
     * 用于匹配关系定义的正则表达式模式。
     * 匹配格式为 "define <relation> : <expression>" 的行。
     */
    private static final Pattern RELATION_PATTERN = Pattern.compile("^\\s*define\\s+(\\w+)\\s*:\\s*(.+)$");

    /**
     * 解析 FGA 模型文本，返回类型定义列表
     *
     * @param modelText 要解析的模型文本，非空且非空白
     * @return 包含所有解析出的类型定义的列表，如果输入为空则返回空列表
     */
    public static List<TypeDefinition> parse(String modelText) {
        if (modelText == null || modelText.trim().isEmpty()) {
            return Collections.emptyList();
        }

        return Arrays.stream(modelText.split("\\r?\\n"))
                .map(String::strip)
                .filter(ModelTextParser::isRelevantLine)
                .collect(ParserStateCollector.collect());
    }

    private static boolean isRelevantLine(String line) {
        return !(line.isEmpty() || line.startsWith("#") || line.startsWith("schema") || line.startsWith("model"));
    }

    private static class ParserStateCollector {

        static class State {
            String currentType;
            boolean inRelationsBlock = false;
            Map<String, RelationDefinition> relations = new LinkedHashMap<>();
            List<TypeDefinition> types = new ArrayList<>();
        }

        public static Collector<String, State, List<TypeDefinition>> collect() {
            return Collector.of(State::new, ParserStateCollector::accumulate, ParserStateCollector::combine, ParserStateCollector::finish);
        }

        private static void accumulate(State state, String line) {
            Matcher typeMatcher = TYPE_PATTERN.matcher(line);
            Matcher relationMatcher = RELATION_PATTERN.matcher(line);

            if (typeMatcher.find()) {
                completeCurrentType(state);
                state.currentType = typeMatcher.group(1);
                state.inRelationsBlock = false;
                return;
            }

            if (line.trim().equals(RELATION_NODE_NAME)) {
                state.inRelationsBlock = true;
                return;
            }

            if (state.inRelationsBlock && relationMatcher.find() && state.currentType != null) {
                String relName = relationMatcher.group(1);
                String expr = relationMatcher.group(2).trim();
                state.relations.put(relName, new RelationDefinition(relName, expr));
            }
        }

        private static State combine(State state1, State state2) {
            throw new UnsupportedOperationException("Parallel processing not supported");
        }

        private static List<TypeDefinition> finish(State state) {
            completeCurrentType(state);
            return state.types;
        }

        private static void completeCurrentType(State state) {
            if (state.currentType != null) {
                state.types.add(new TypeDefinition(state.currentType, new LinkedHashMap<>(state.relations), Map.of()));
                state.relations.clear();
                state.inRelationsBlock = false;
            }
        }
    }
}
