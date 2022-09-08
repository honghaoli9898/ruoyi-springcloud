package com.sdps.module.user.service.logger;

import java.util.List;

import com.sdps.common.pojo.PageResult;
import com.sdps.module.system.dal.dataobject.logger.ApiAccessLogDO;
import com.sdps.module.user.controller.admin.logger.vo.apiaccesslog.ApiAccessLogExportReqVO;
import com.sdps.module.user.controller.admin.logger.vo.apiaccesslog.ApiAccessLogPageReqVO;

/**
 * API 访问日志 Service 接口
 *
 * @author 芋道源码
 */
public interface ApiAccessLogService {

    /**
     * 获得 API 访问日志分页
     *
     * @param pageReqVO 分页查询
     * @return API 访问日志分页
     */
    PageResult<ApiAccessLogDO> getApiAccessLogPage(ApiAccessLogPageReqVO pageReqVO);

    /**
     * 获得 API 访问日志列表, 用于 Excel 导出
     *
     * @param exportReqVO 查询条件
     * @return API 访问日志分页
     */
    List<ApiAccessLogDO> getApiAccessLogList(ApiAccessLogExportReqVO exportReqVO);

}
