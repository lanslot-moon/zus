package org.kitona.zus.domain.port;

import org.kitona.zus.domain.authorization.model.AuthorizationModelAggregate;

/**
 * 模型快照渲染端口。
 * 该接口定义了将授权模型聚合转换为字符串表示的渲染方法。
 *
 * @author CodeGeeX
 * @version 1.0
 */
public interface IModelSnapshotRenderer {

    /**
     * 将授权模型聚合渲染为字符串格式。
     *
     * @param aggregate 授权模型聚合对象，包含需要渲染的模型数据
     * @return String 渲染后的模型快照字符串表示
     */
    String render(AuthorizationModelAggregate aggregate);
}
