package org.kitona.zus.api.converter;

import org.kitona.zus.api.request.authorization.FgaBatchCheckRequest;
import org.kitona.zus.api.request.authorization.FgaCheckRequest;
import org.kitona.zus.api.request.common.FgaConsistencyOptions;
import org.kitona.zus.api.request.common.FgaReferenceRequest;
import org.kitona.zus.api.request.common.FgaTupleKeyRequest;
import org.kitona.zus.api.response.FgaCheckResultVO;
import org.kitona.zus.service.dto.command.CheckCommand;
import org.kitona.zus.service.dto.response.PermissionCheckResultDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

/**
 * Check / BatchCheck 的 API 与 Service 转换器。
 *
 * <p>该转换器采用 MapStruct mapper 形式托管到 Spring 容器中。请求侧包含
 * tupleKey、consistency 等嵌套结构，因此保留 default 方法进行显式组装；响应侧字段同名，
 * 交给 MapStruct 生成实现。
 *
 * @author kitona
 * @since 2026-04-18
 */
@Mapper(componentModel = "spring")
public interface FgaCheckConverter {

    FgaCheckConverter INSTANCE = Mappers.getMapper(FgaCheckConverter.class);

    /**
     * 将单次 Check 请求转换为应用层命令。
     *
     * @param storeId Store 标识
     * @param request API 请求
     * @return 应用层命令
     */
    @Mapping(target = "storeId", source = "storeId")
    @Mapping(target = "objectType", source = "request.tupleKey.object.type")
    @Mapping(target = "objectId", source = "request.tupleKey.object.id")
    @Mapping(target = "relation", source = "request.tupleKey.relation")
    @Mapping(target = "subjectType", source = "request.tupleKey.subject.type")
    @Mapping(target = "subjectId", source = "request.tupleKey.subject.id")
    @Mapping(target = "subjectRelation", source = "request.tupleKey.subject.relation")
    @Mapping(target = "consistencyToken", source = "request.consistency", qualifiedByName = "resolveConsistencyToken")
    @Mapping(target = "context", source = "request.context")
    CheckCommand toCheckCommand(String storeId, FgaCheckRequest request);

    /**
     * 将批量 Check 中的单个检查项转换为应用层命令。
     *
     * @param storeId Store 标识
     * @param item    批量检查项
     * @param batch   批量请求，用于读取共享一致性参数
     * @return 应用层命令
     */
    @Mapping(target = "storeId", source = "storeId")
    @Mapping(target = "objectType", source = "item.tupleKey.object.type")
    @Mapping(target = "objectId", source = "item.tupleKey.object.id")
    @Mapping(target = "relation", source = "item.tupleKey.relation")
    @Mapping(target = "subjectType", source = "item.tupleKey.subject.type")
    @Mapping(target = "subjectId", source = "item.tupleKey.subject.id")
    @Mapping(target = "subjectRelation", source = "item.tupleKey.subject.relation")
    @Mapping(target = "consistencyToken", source = "batch.consistency", qualifiedByName = "resolveConsistencyToken")
    @Mapping(target = "context", source = "item.context")
    CheckCommand toCheckCommand(String storeId,
                                FgaBatchCheckRequest.CheckItem item,
                                FgaBatchCheckRequest batch);

    /**
     * 将应用层 Check 结果转换为 API 响应。
     *
     * @param dto 应用层结果
     * @return API 响应
     */
    @Mapping(target = "allowed", source = "allowed")
    @Mapping(target = "durationMs", source = "durationMs")
    @Mapping(target = "zookieToken", source = "zookieToken")
    @Mapping(target = "decision", source = "decision")
    @Mapping(target = "errorMessage", source = "errorMessage")
    FgaCheckResultVO toVO(PermissionCheckResultDTO dto);

    /**
     * 将 API 一致性选项解析为领域层识别的 zookie token。
     */
    @Named("resolveConsistencyToken")
    default String resolveConsistencyToken(FgaConsistencyOptions options) {
        if (options == null || options.getPreference() != FgaConsistencyOptions.Preference.AT_LEAST_AS_FRESH) {
            return null;
        }
        return options.getAtRevision();
    }
}
