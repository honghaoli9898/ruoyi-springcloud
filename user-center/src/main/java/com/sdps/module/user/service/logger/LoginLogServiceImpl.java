package com.sdps.module.user.service.logger;

import java.util.List;

import com.sdps.module.system.dal.dataobject.logger.LoginLogDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.sdps.common.pojo.PageResult;
import com.sdps.module.user.controller.admin.logger.vo.loginlog.LoginLogExportReqVO;
import com.sdps.module.user.controller.admin.logger.vo.loginlog.LoginLogPageReqVO;
import com.sdps.module.user.dal.mapper.logger.LoginLogMapper;

/**
 * 登录日志 Service 实现
 */
@Service
@Validated
public class LoginLogServiceImpl implements LoginLogService {

    @Autowired
    private LoginLogMapper loginLogMapper;

    @Override
    public PageResult<LoginLogDO> getLoginLogPage(LoginLogPageReqVO reqVO) {
        return loginLogMapper.selectPage(reqVO);
    }

    @Override
    public List<LoginLogDO> getLoginLogList(LoginLogExportReqVO reqVO) {
        return loginLogMapper.selectList(reqVO);
    }

}
