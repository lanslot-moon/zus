package org.kitona.zus.domain.port;

import org.kitona.zus.domain.authorization.model.AuthorizationModelAggregate;
import org.kitona.zus.domain.authorization.evaluation.compiled.CompiledAuthorizationModel;

/**
 * 编译授权模型端口。
 */
public interface ICompiledModelCompiler {

    CompiledAuthorizationModel compile(AuthorizationModelAggregate aggregate);
}
