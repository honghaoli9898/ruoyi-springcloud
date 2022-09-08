package com.sdps.module.system.service.logger;

import com.sdps.module.system.api.logger.dto.LoginLogCreateReqDTO;
import com.sdps.module.system.convert.logger.SysLoginLogConvert;
import com.sdps.module.system.dal.dataobject.logger.LoginLogDO;
import com.sdps.module.system.dal.mapper.errorcode.SysLoginLogMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;

/**
 * 登录日志 Service 实现
 */
@Service
@Validated
public class SysLoginLogServiceImpl implements SysLoginLogService {

    @Resource
    private SysLoginLogMapper sysLoginLogMapper;

    @Override
    public void createLoginLog(LoginLogCreateReqDTO reqDTO) {
        LoginLogDO loginLog = SysLoginLogConvert.INSTANCE.convert(reqDTO);
        sysLoginLogMapper.insert(loginLog);
    }

}
