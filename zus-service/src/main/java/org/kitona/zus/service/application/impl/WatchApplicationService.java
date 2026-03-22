package org.kitona.zus.service.application.impl;

import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.kitona.zus.domain.entity.ChangelogEntity;
import org.kitona.zus.domain.repository.IChangelogQueryRepository;
import org.kitona.zus.service.application.IWatchApplicationService;
import org.kitona.zus.service.assembler.ChangelogAssembler;
import org.kitona.zus.service.dto.response.WatchChangeResultDTO;
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
public class WatchApplicationService implements IWatchApplicationService {

    private static final int DEFAULT_CHANGE_LIMIT = 100;

    @Resource
    private IChangelogQueryRepository changelogQueryRepository;

    @Override
    public List<WatchChangeResultDTO> getChanges(String storeId, Long startAt, int limit) {
        if (StringUtils.isBlank(storeId)) {
            return List.of();
        }
        int effectiveLimit = limit > 0 ? limit : DEFAULT_CHANGE_LIMIT;
        Long after = startAt != null ? startAt : 0L;
        List<ChangelogEntity> list = changelogQueryRepository.findAfterZookie(storeId, after, effectiveLimit);
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
}
