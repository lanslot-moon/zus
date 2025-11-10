package org.kitona.zus.business.entity.bo;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collector;

/**
 * FgaModelParser 类用于解析FGA (Fine-Grained Access) 格式的模型文本。
 * 该类提供了将模型文本解析为TypeDefinition列表的功能。
 */
public class FgaModelParser {

    /**
     * 私有构造函数，防止实例化工具类。
     *
     * @throws IllegalStateException 如果尝试实例化此工具类
     */
    private FgaModelParser() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 用于匹配类型定义的正则表达式模式。
     */
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
     * 解析FGA模型文本，返回类型定义列表。
     *
     * @param modelText 要解析的模型文本，非空且非空白
     * @return 包含所有解析出的类型定义的列表，如果输入为空则返回空列表
     */
    public static List<TypeDefinition> parseModelText(String modelText) {
        if (modelText == null || modelText.trim().isEmpty()) {
            return Collections.emptyList();
        }

        // 将文本按行分割，处理每行并收集结果
        return Arrays.stream(modelText.split("\\r?\\n"))
                .map(String::strip)
                .filter(FgaModelParser::isRelevantLine)
                .collect(ParserStateCollector.collect());
    }

    /**
     * 判断一行文本是否与类型或关系定义相关。
     *
     * @param line 要检查的文本行
     * @return 如果行不为空、不以#开头、不以schema或model开头，则返回true
     */
    private static boolean isRelevantLine(String line) {
        return !(line.isEmpty() || line.startsWith("#") || line.startsWith("schema") || line.startsWith("model"));
    }

    /**
     * ParserStateCollector 是一个静态内部类，用于收集和解析类型定义和关系定义。
     * 它使用 Collector 模式来处理输入的字符串行，并最终生成类型定义列表。
     */
    private static class ParserStateCollector {

        /**
         * State 类表示解析过程中的状态，包含当前类型、关系映射和类型列表。
         */
        static class State {
            /**
             * 当前正在处理的类型名称
             */
            String currentType;
            /**
             *
             */
            boolean inRelationsBlock = false;
            /**
             * 存储关系定义的映射表
             */
            Map<String, RelationDefinition> relations = new LinkedHashMap<>();
            /**
             * 存储所有已完成的类型定义
             */
            List<TypeDefinition> types = new ArrayList<>();
        }

        /**
         * 创建一个新的收集器，用于将字符串行收集为类型定义列表。
         *
         * @return 一个 Collector，用于将字符串行转换为类型定义列表
         */
        public static Collector<String, State, List<TypeDefinition>> collect() {
            return Collector.of(State::new, ParserStateCollector::accumulate, ParserStateCollector::combine, ParserStateCollector::finish);
        }

        /**
         * 累积方法，处理每一行输入字符串，更新解析状态。
         *
         * @param state 当前的解析状态
         * @param line  输入的字符串行
         */
        private static void accumulate(State state, String line) {
            Matcher typeMatcher = TYPE_PATTERN.matcher(line);
            Matcher relationMatcher = RELATION_PATTERN.matcher(line);

            // 如果找到类型定义，完成当前类型的处理并开始新类型
            if (typeMatcher.find()) {
                completeCurrentType(state);
                state.currentType = typeMatcher.group(1);
                state.inRelationsBlock = false;
                return;
            }

            // 遇到 relations 块
            if (line.trim().equals(RELATION_NODE_NAME)) {
                state.inRelationsBlock = true;
                return;
            }

            // 如果找到关系定义且当前有活动类型，则添加关系到当前类型
            if (state.inRelationsBlock && relationMatcher.find() && state.currentType != null) {
                String relName = relationMatcher.group(1);
                String expr = relationMatcher.group(2).trim();
                state.relations.put(relName, new RelationDefinition(relName, expr));
            }
        }

        /**
         * 组合方法，用于合并两个状态。
         * 此实现不支持并行处理，因此抛出 UnsupportedOperationException。
         *
         * @param state1 第一个状态
         * @param state2 第二个状态
         * @return 组合后的状态（此方法不会返回）
         * @throws UnsupportedOperationException 始终抛出，表示不支持并行处理
         */
        private static State combine(State state1, State state2) {
            throw new UnsupportedOperationException("Parallel processing not supported");
        }

        /**
         * 完成方法，在收集所有输入后生成最终结果。
         *
         * @param state 最终的解析状态
         * @return 包含所有类型定义的列表
         */
        private static List<TypeDefinition> finish(State state) {
            completeCurrentType(state);
            return state.types;
        }

        /**
         * 完成当前类型的处理，将其添加到类型列表中。
         * 如果当前类型存在且有相关关系定义，则创建一个新的类型定义。
         *
         * @param state 当前的解析状态
         */
        private static void completeCurrentType(State state) {
            if (state.currentType != null) {
                state.types.add(new TypeDefinition(state.currentType, new LinkedHashMap<>(state.relations), Map.of()));
                state.relations.clear();
                state.inRelationsBlock = false;
            }
        }
    }
}