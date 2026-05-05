package org.kitona.zus.api.controller;

import jakarta.validation.Valid;
import org.kitona.zus.api.request.authorization.FgaExplainRequest;
import org.kitona.zus.api.response.FgaExplainResultVO;
import org.kitona.zus.api.response.RestResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * FGA Explain API —— 授权证明路径调试入口。
 */
@RestController
@RequestMapping("/fga/stores/{storeId}")
public interface IFgaExplainApiService {

    /**
     * 解释一次权限检查的证明过程。
     */
    @PostMapping("/check/explain")
    RestResult<FgaExplainResultVO> explain(@PathVariable String storeId, @Valid @RequestBody FgaExplainRequest request);
}
