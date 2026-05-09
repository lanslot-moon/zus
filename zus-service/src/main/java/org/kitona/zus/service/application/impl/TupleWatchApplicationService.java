package org.kitona.zus.service.application.impl;

import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.kitona.zus.domain.authorization.audit.Changelog;
import org.kitona.zus.domain.repository.IChangelogQueryRepository;
import org.kitona.zus.service.application.ITupleWatchApplicationService;
import org.kitona.zus.service.conv.assembler.ChangelogAssembler;
import org.kitona.zus.service.dto.response.PageResultDTO;
import org.kitona.zus.service.dto.response.TupleChangeResultDTO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Watch 应用服务：通过 IChangelogQueryRepository 查询变更日志供 API 层 SSE 推送。
 *
 * @author kitona
 * @version 1.1.0
 * @since 2025-01-15
 */
@Service
public class TupleWatchApplicationService implements ITupleWatchApplicationService {

    private static final int DEFAULT_CHANGE_LIMIT = 100;

    @Resource
    private IChangelogQueryRepository changelogQueryRepository;

    @Override
    public List<TupleChangeResultDTO> getChanges(String storeId, Long startAt, int limit) {
        if (StringUtils.isBlank(storeId)) {
            return List.of();
        }
        int effectiveLimit = limit > 0 ? limit : DEFAULT_CHANGE_LIMIT;
        Long after = startAt != null ? startAt : 0L;
        List<Changelog> list = changelogQueryRepository.findAfterZookie(storeId, after, effectiveLimit);
        return ChangelogAssembler.toDTOList(list);
    }

    @Override
    public long getCurrentZookie(String storeId) {
        if (StringUtils.isBlank(storeId)) {
            return 0L;
        }
        Long max = changelogQueryRepository.getMaxZookie(storeId);
        return max != null ? max : 0L;
    }

    @Override
    public PageResultDTO<TupleChangeResultDTO> listChanges(String storeId, String continuationToken,
                                                           int pageSize, String objectTypeFilter) {
        if (StringUtils.isBlank(storeId)) {
            return PageResultDTO.empty();
        }
        int effectiveSize = pageSize > 0 ? pageSize : DEFAULT_CHANGE_LIMIT;
        Long after = parseZookieToken(continuationToken);

        // 为了支撑在应用层做 type 过滤，适当放大底层查询窗口，避免过滤后页严重不足。
        int fetchSize = StringUtils.isBlank(objectTypeFilter) ? effectiveSize : effectiveSize * 2;
        List<Changelog> raw = changelogQueryRepository.findAfterZookie(storeId, after, fetchSize);

        List<Changelog> filtered = StringUtils.isBlank(objectTypeFilter) ? raw : raw.stream()
                .filter(change -> objectTypeFilter.equals(change.getObjectType()))
                .toList();

        if (filtered.size() > effectiveSize) {
            filtered = filtered.subList(0, effectiveSize);
        }

        List<TupleChangeResultDTO> list = ChangelogAssembler.toDTOList(filtered);
        if (list.isEmpty()) {
            return PageResultDTO.empty();
        }
        String nextToken = String.valueOf(filtered.get(filtered.size() - 1).getZookie());
        boolean hasMore = list.size() >= effectiveSize;
        return PageResultDTO.of(list, hasMore ? nextToken : null);
    }

    private Long parseZookieToken(String token) {
        if (StringUtils.isBlank(token)) {
            return 0L;
        }
        try {
            return Long.parseLong(token);
        } catch (NumberFormatException ex) {
            return 0L;
        }
    }
}
