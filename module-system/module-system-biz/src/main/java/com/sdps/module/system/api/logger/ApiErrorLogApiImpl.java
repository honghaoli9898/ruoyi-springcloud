package com.sdps.module.system.api.logger;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.sdps.module.system.api.logger.ApiErrorLogApi;
import com.sdps.module.system.api.logger.dto.ApiErrorLogCreateReqDTO;
import com.sdps.module.system.service.logger.SysApiErrorLogService;

/**
 * API 访问日志的 API 接口
 *
 * @author 芋道源码
 */
@Service
@Validated
public class ApiErrorLogApiImpl implements ApiErrorLogApi {

    @Resource
    private SysApiErrorLogService apiErrorLogService;

    @Override
    public void createApiErrorLog(ApiErrorLogCreateReqDTO createDTO) {
        apiErrorLogService.createApiErrorLog(createDTO);
    }

}
