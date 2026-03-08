package org.kitona.zus.domain.port;

import org.kitona.zus.domain.service.internal.GraphBuilder;
import org.kitona.zus.domain.valueobject.AuthorizationModel;

/**
 * 模型编译器端口
 * <p>
 * 定义将关系重写表达式编译为图边的契约。
 * 具体实现（如基于 ANTLR 的编译器）位于基础设施层。
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
public interface IModelCompiler {

    /**
     * 编译单条关系的重写表达式
     *
     * @param resourceType      资源类型，例如 document
     * @param relationName      关系名称，例如 viewer
     * @param rewriteExpression 重写表达式，例如 "editor or viewer"
     */
    void compileRelation(String resourceType, String relationName, String rewriteExpression);

    /**
     * 初始化图构建器
     *
     * @param graphBuilder 图构建器
     * @param model        授权模型
     */
    void initGraphBuilder(GraphBuilder graphBuilder, AuthorizationModel model);
}
