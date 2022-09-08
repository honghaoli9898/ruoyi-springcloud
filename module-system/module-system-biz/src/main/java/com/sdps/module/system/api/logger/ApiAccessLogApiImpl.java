package com.sdps.module.system.api.logger;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.sdps.module.system.api.logger.ApiAccessLogApi;
import com.sdps.module.system.api.logger.dto.ApiAccessLogCreateReqDTO;
import com.sdps.module.system.service.logger.SysApiAccessLogService;

/**
 * API 访问日志的 API 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class ApiAccessLogApiImpl implements ApiAccessLogApi {

    @Resource
    private SysApiAccessLogService apiAccessLogService;

    @Override
    public void createApiAccessLog(ApiAccessLogCreateReqDTO createDTO) {
        apiAccessLogService.createApiAccessLog(createDTO);
    }

}
