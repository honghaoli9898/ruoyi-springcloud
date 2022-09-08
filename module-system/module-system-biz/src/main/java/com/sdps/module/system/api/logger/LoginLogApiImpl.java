package com.sdps.module.system.api.logger;

import com.sdps.module.system.api.logger.dto.LoginLogCreateReqDTO;
import com.sdps.module.system.service.logger.SysLoginLogService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;

/**
 * 登录日志的 API 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class LoginLogApiImpl implements LoginLogApi {

    @Resource
    private SysLoginLogService sysLoginLogService;

    @Override
    public void createLoginLog(LoginLogCreateReqDTO reqDTO) {
        sysLoginLogService.createLoginLog(reqDTO);
    }

}
