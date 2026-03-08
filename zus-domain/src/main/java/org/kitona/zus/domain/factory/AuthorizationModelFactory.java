package org.kitona.zus.domain.factory;

import org.kitona.zus.domain.port.IModelCompiler;
import org.kitona.zus.domain.service.AuthorizationModelGraph;
import org.kitona.zus.domain.service.internal.ModelTextParser;
import org.kitona.zus.domain.valueobject.AuthorizationModel;
import org.kitona.zus.domain.valueobject.TypeDefinition;

import java.util.List;

/**
 * 授权模型工厂
 * <p>
 * 负责创建复杂的授权模型对象。
 * 工厂模式用于封装复杂对象的创建逻辑，将创建过程与使用过程解耦。
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
public class AuthorizationModelFactory {

    private AuthorizationModelFactory() {
        throw new IllegalStateException("Factory class should not be instantiated");
    }

    /**
     * 从 DSL 文本创建授权模型图
     *
     * @param dslText  DSL 格式的模型定义文本
     * @param compiler 模型编译器
     * @return 授权模型图
     */
    public static AuthorizationModelGraph createGraphFromDsl(String dslText, IModelCompiler compiler) {
        List<TypeDefinition> typeDefinitions = ModelTextParser.parse(dslText);
        return AuthorizationModelGraph.fromModel(new AuthorizationModel(typeDefinitions), compiler);
    }

    /**
     * 从类型定义列表创建授权模型图
     *
     * @param compiler        模型编译器
     * @param typeDefinitions 类型定义列表
     * @return 授权模型图
     */
    public static AuthorizationModelGraph createGraph(IModelCompiler compiler, List<TypeDefinition> typeDefinitions) {
        AuthorizationModel model = new AuthorizationModel(typeDefinitions);
        return AuthorizationModelGraph.fromModel(model, compiler);
    }
}
