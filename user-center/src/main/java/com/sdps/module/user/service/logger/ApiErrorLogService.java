package com.sdps.module.user.service.logger;

import java.util.List;

import com.sdps.common.pojo.PageResult;
import com.sdps.module.system.dal.dataobject.logger.ApiErrorLogDO;
import com.sdps.module.user.controller.admin.logger.vo.apierrorlog.ApiErrorLogExportReqVO;
import com.sdps.module.user.controller.admin.logger.vo.apierrorlog.ApiErrorLogPageReqVO;

/**
 * API 错误日志 Service 接口
 *
 * @author 芋道源码
 */
public interface ApiErrorLogService {

    /**
     * 获得 API 错误日志分页
     *
     * @param pageReqVO 分页查询
     * @return API 错误日志分页
     */
    PageResult<ApiErrorLogDO> getApiErrorLogPage(ApiErrorLogPageReqVO pageReqVO);

    /**
     * 获得 API 错误日志列表, 用于 Excel 导出
     *
     * @param exportReqVO 查询条件
     * @return API 错误日志分页
     */
    List<ApiErrorLogDO> getApiErrorLogList(ApiErrorLogExportReqVO exportReqVO);

    /**
     * 更新 API 错误日志已处理
     *
     * @param id API 日志编号
     * @param processStatus 处理结果
     * @param processUserId 处理人
     */
    void updateApiErrorLogProcess(Long id, Integer processStatus, Long processUserId);

}
