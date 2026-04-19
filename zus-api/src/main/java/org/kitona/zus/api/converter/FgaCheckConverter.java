package org.kitona.zus.api.converter;

import org.kitona.zus.api.request.authorization.FgaBatchCheckRequest;
import org.kitona.zus.api.request.authorization.FgaCheckRequest;
import org.kitona.zus.api.request.common.FgaConsistencyOptions;
import org.kitona.zus.api.request.common.FgaReferenceRequest;
import org.kitona.zus.api.request.common.FgaTupleKeyRequest;
import org.kitona.zus.api.response.FgaCheckResultVO;
import org.kitona.zus.common.utils.MapstructUtil;
import org.kitona.zus.service.dto.command.CheckCommand;
import org.kitona.zus.service.dto.response.PermissionCheckResultDTO;

import java.util.Map;

/**
 * Check / BatchCheck 的 API ↔ Service 转换器。
 *
 * <p>{@link PermissionCheckResultDTO} → {@link FgaCheckResultVO} 是字段同名的
 * POJO 拷贝，通过 {@link MapstructUtil#convert(Object, Class)} 完成；
 * 请求侧涉及嵌套结构（tupleKey / consistency），手动组装更直观、性能更好。
 *
 * @author kitona
 * @since 2026-04-18
 */
public final class FgaCheckConverter {

    private FgaCheckConverter() {
    }

    // =========================== API → Service ===========================

    public static CheckCommand toCheckCommand(String storeId, FgaCheckRequest request) {
        if (request == null) {
            return null;
        }
        CheckCommand command = toCommand(storeId, request.getTupleKey(), request.getContext());
        if (command != null) {
            command.setConsistencyToken(resolveConsistencyToken(request.getConsistency()));
        }
        return command;
    }

    public static CheckCommand toCheckCommand(String storeId,
                                              FgaBatchCheckRequest.CheckItem item,
                                              FgaBatchCheckRequest batch) {
        if (item == null) {
            return null;
        }
        Map<String, Object> ctx = item.getContext();
        CheckCommand command = toCommand(storeId, item.getTupleKey(), ctx);
        if (command != null && batch != null) {
            command.setConsistencyToken(resolveConsistencyToken(batch.getConsistency()));
        }
        return command;
    }

    private static CheckCommand toCommand(String storeId, FgaTupleKeyRequest key, Map<String, Object> ctx) {
        if (key == null) {
            return null;
        }
        FgaReferenceRequest object = key.getObject();
        FgaReferenceRequest subject = key.getSubject();
        return CheckCommand.builder()
                .storeId(storeId)
                .objectType(object != null ? object.getType() : null)
                .objectId(object != null ? object.getId() : null)
                .relation(key.getRelation())
                .subjectType(subject != null ? subject.getType() : null)
                .subjectId(subject != null ? subject.getId() : null)
                .subjectRelation(subject != null ? subject.getRelation() : null)
                .context(ctx)
                .build();
    }

    /**
     * 解析一致性选项为领域层识别的 zookie token。
     * <p>当前仅在 {@link FgaConsistencyOptions.Preference#AT_LEAST_AS_FRESH} 下带 zookie，
     * 其他模式交由领域层自行处理（MINIMIZE_LATENCY 走缓存；HIGHER_CONSISTENCY 走强一致）。
     */
    private static String resolveConsistencyToken(FgaConsistencyOptions options) {
        if (options == null) {
            return null;
        }
        if (options.getPreference() == FgaConsistencyOptions.Preference.AT_LEAST_AS_FRESH) {
            return options.getAtRevision();
        }
        return null;
    }

    // =========================== Service → API ===========================

    public static FgaCheckResultVO toVO(PermissionCheckResultDTO dto) {
        if (dto == null) {
            return null;
        }
        return MapstructUtil.convert(dto, FgaCheckResultVO.class);
    }
}
