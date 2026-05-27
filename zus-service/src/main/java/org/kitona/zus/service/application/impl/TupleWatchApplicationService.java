package org.kitona.zus.service.application.impl;

import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.kitona.zus.domain.read.port.IChangelogQueryPort;
import org.kitona.zus.domain.read.view.ChangelogView;
import org.kitona.zus.service.application.ITupleWatchApplicationService;
import org.kitona.zus.service.conv.assembler.ChangelogAssembler;
import org.kitona.zus.service.dto.response.PageResultDTO;
import org.kitona.zus.service.dto.response.TupleChangeResultDTO;
import org.kitona.zus.service.port.IConsistencyTokenReader;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Watch 应用服务：通过 IChangelogQueryPort 查询变更日志供 API 层 SSE 推送。
 *
 * @author kitona
 * @version 1.1.0
 * @since 2025-01-15
 */
@Service
public class TupleWatchApplicationService implements ITupleWatchApplicationService {

    private static final int DEFAULT_CHANGE_LIMIT = 100;

    @Resource
    private IChangelogQueryPort changelogQueryRepository;

    @Resource
    private IConsistencyTokenReader consistencyTokenReader;

    /**
     * 查询 Watch 增量变更。
     *
     * @param storeId Store 标识
     * @param startAt startAt 参数
     * @param limit limit 参数
     * @return 查询结果
     */
    @Override
    public List<TupleChangeResultDTO> getChanges(String storeId, Long startAt, int limit) {
        if (StringUtils.isBlank(storeId)) {
            return List.of();
        }
        int effectiveLimit = limit > 0 ? limit : DEFAULT_CHANGE_LIMIT;
        Long after = startAt != null ? startAt : 0L;
        List<ChangelogView> list = changelogQueryRepository.findAfterZookie(storeId, after, effectiveLimit);
        return ChangelogAssembler.toDTOList(list);
    }

    /**
     * 读取 Store 当前 zookie。
     *
     * @param storeId Store 标识
     * @return 查询结果
     */
    @Override
    public long getCurrentZookie(String storeId) {
        if (StringUtils.isBlank(storeId)) {
            return 0L;
        }
        Long max = consistencyTokenReader.currentMaxZookie(storeId);
        return max != null ? max : 0L;
    }

    /**
     * 按游标分页查询 Watch 变更列表。
     *
     * @param storeId           Store 标识
     * @param continuationToken 上次读取返回的延续游标
     * @param pageSize          分页大小
     * @param objectTypeFilter  可选对象类型过滤条件
     * @return 变更列表分页结果
     */
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
        List<ChangelogView> raw = changelogQueryRepository.findAfterZookie(storeId, after, fetchSize);

        List<ChangelogView> filtered = StringUtils.isBlank(objectTypeFilter) ? raw : raw.stream()
                .filter(change -> objectTypeFilter.equals(change.objectType()))
                .toList();

        if (filtered.size() > effectiveSize) {
            filtered = filtered.subList(0, effectiveSize);
        }

        List<TupleChangeResultDTO> list = ChangelogAssembler.toDTOList(filtered);
        if (list.isEmpty()) {
            return PageResultDTO.empty();
        }
        String nextToken = String.valueOf(filtered.get(filtered.size() - 1).zookie());
        boolean hasMore = list.size() >= effectiveSize;
        return PageResultDTO.of(list, hasMore ? nextToken : null);
    }

    /**
     * 解析 zookie 游标 token。
     *
     * @param token token 参数
     * @return 解析后的 zookie；无法解析时返回 0
     */
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
