package com.sdps.module.user.service.logger;

import com.sdps.common.pojo.PageResult;
import com.sdps.module.system.dal.dataobject.logger.LoginLogDO;
import com.sdps.module.user.controller.admin.logger.vo.loginlog.LoginLogExportReqVO;
import com.sdps.module.user.controller.admin.logger.vo.loginlog.LoginLogPageReqVO;

import java.util.List;

/**
 * 登录日志 Service 接口
 */
public interface LoginLogService {

    /**
     * 获得登录日志分页
     *
     * @param reqVO 分页条件
     * @return 登录日志分页
     */
    PageResult<LoginLogDO> getLoginLogPage(LoginLogPageReqVO reqVO);

    /**
     * 获得登录日志列表
     *
     * @param reqVO 列表条件
     * @return 登录日志列表
     */
    List<LoginLogDO> getLoginLogList(LoginLogExportReqVO reqVO);


}
