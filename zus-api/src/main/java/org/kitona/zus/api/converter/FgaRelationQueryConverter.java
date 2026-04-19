package org.kitona.zus.api.converter;

import org.apache.commons.collections4.CollectionUtils;
import org.kitona.zus.api.request.authorization.FgaListObjectsRequest;
import org.kitona.zus.api.request.authorization.FgaListUsersRequest;
import org.kitona.zus.api.request.common.FgaConsistencyOptions;
import org.kitona.zus.api.request.common.FgaReferenceRequest;
import org.kitona.zus.api.response.FgaListObjectsResponseVO;
import org.kitona.zus.api.response.FgaListUsersResponseVO;
import org.kitona.zus.api.response.FgaUserVO;
import org.kitona.zus.common.utils.MapstructUtil;
import org.kitona.zus.service.dto.query.ListObjectsQuery;
import org.kitona.zus.service.dto.query.ListUsersQuery;
import org.kitona.zus.service.dto.response.ListObjectsResultDTO;
import org.kitona.zus.service.dto.response.ListUsersResultDTO;

import java.util.Collections;
import java.util.List;

/**
 * 关系查询（ListObjects / ListUsers）的 API ↔ Service 转换器。
 *
 * <p>说明：当前 {@link FgaListUsersRequest} 支持多个 {@code userFilters}，
 * 但领域层 {@link ListUsersQuery} 只承载单一过滤条件；这里选取第一个过滤器作为主过滤项，
 * 避免 API 层再做多次查询合并（待领域层补齐 multi-filter 后可调整）。
 *
 * @author kitona
 * @since 2026-04-18
 */
public final class FgaRelationQueryConverter {

    private FgaRelationQueryConverter() {
    }

    // =========================== API → Service ===========================

    public static ListObjectsQuery toListObjectsQuery(String storeId, FgaListObjectsRequest request) {
        if (request == null) {
            return null;
        }
        FgaReferenceRequest subject = request.getSubject();
        return ListObjectsQuery.builder()
                .storeId(storeId)
                .subjectType(subject != null ? subject.getType() : null)
                .subjectId(subject != null ? subject.getId() : null)
                .subjectRelation(subject != null ? subject.getRelation() : null)
                .relation(request.getRelation())
                .objectType(request.getObjectType())
                .consistencyToken(resolveConsistencyToken(request.getConsistency()))
                .context(request.getContext())
                .build();
    }

    public static ListUsersQuery toListUsersQuery(String storeId, FgaListUsersRequest request) {
        if (request == null) {
            return null;
        }
        FgaReferenceRequest object = request.getObject();
        FgaListUsersRequest.UserFilter first = firstFilter(request.getUserFilters());
        return ListUsersQuery.builder()
                .storeId(storeId)
                .objectType(object != null ? object.getType() : null)
                .objectId(object != null ? object.getId() : null)
                .relation(request.getRelation())
                .subjectType(first != null ? first.getType() : null)
                .subjectRelation(first != null ? first.getRelation() : null)
                .consistencyToken(resolveConsistencyToken(request.getConsistency()))
                .context(request.getContext())
                .build();
    }

    private static FgaListUsersRequest.UserFilter firstFilter(List<FgaListUsersRequest.UserFilter> filters) {
        if (CollectionUtils.isEmpty(filters)) {
            return null;
        }
        return filters.get(0);
    }

    private static String resolveConsistencyToken(FgaConsistencyOptions options) {
        if (options == null || options.getPreference() != FgaConsistencyOptions.Preference.AT_LEAST_AS_FRESH) {
            return null;
        }
        return options.getAtRevision();
    }

    // =========================== Service → API ===========================

    public static FgaListObjectsResponseVO toListObjectsVO(ListObjectsResultDTO dto) {
        FgaListObjectsResponseVO vo = new FgaListObjectsResponseVO();
        vo.setObjects(dto == null || dto.getObjects() == null ? Collections.emptyList() : dto.getObjects());
        return vo;
    }

    public static FgaListUsersResponseVO toListUsersVO(ListUsersResultDTO dto) {
        FgaListUsersResponseVO vo = new FgaListUsersResponseVO();
        if (dto == null || CollectionUtils.isEmpty(dto.getUsers())) {
            vo.setUsers(Collections.emptyList());
            return vo;
        }
        vo.setUsers(MapstructUtil.convert(dto.getUsers(), FgaUserVO.class));
        return vo;
    }
}
