package org.kitona.zus.domain.port;

import org.kitona.zus.domain.authorization.model.AuthorizationModelAggregate;

/**
 * 模型快照渲染端口。
 */
public interface IModelSnapshotRenderer {

    String render(AuthorizationModelAggregate aggregate);
}
