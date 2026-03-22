package org.kitona.zus.api.controller.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.kitona.zus.api.controller.IFgaWriteApiService;
import org.kitona.zus.api.request.FgaTupleRequest;
import org.kitona.zus.api.response.RestResult;
import org.kitona.zus.common.utils.MapstructUtil;
import org.kitona.zus.service.application.IWriteApplicationService;
import org.kitona.zus.service.dto.command.WriteTupleCommand;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * FGA 元组写入 API 实现
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
@Slf4j
@Service
public class FgaWriteApiService implements IFgaWriteApiService {

    @Resource
    private IWriteApplicationService writeApplicationService;

    @Override
    public RestResult<Void> write(String storeId, List<FgaTupleRequest> writes) {
        List<WriteTupleCommand> writeTuple = MapstructUtil.convert(writes, WriteTupleCommand.class);
        writeApplicationService.write(storeId, writeTuple);
        return RestResult.success(null);
    }

    @Override
    public RestResult<Void> delete(String storeId, List<FgaTupleRequest> deletes) {
        List<WriteTupleCommand> writeTuple = MapstructUtil.convert(deletes, WriteTupleCommand.class);
        writeApplicationService.delete(storeId, writeTuple);
        return RestResult.success(null);
    }
}
