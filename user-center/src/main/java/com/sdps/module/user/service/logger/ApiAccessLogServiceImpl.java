package com.sdps.module.user.service.logger;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.sdps.common.pojo.PageResult;
import com.sdps.module.system.dal.dataobject.logger.ApiAccessLogDO;
import com.sdps.module.user.controller.admin.logger.vo.apiaccesslog.ApiAccessLogExportReqVO;
import com.sdps.module.user.controller.admin.logger.vo.apiaccesslog.ApiAccessLogPageReqVO;
import com.sdps.module.user.dal.mapper.logger.ApiAccessLogMapper;

/**
 * API 访问日志 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class ApiAccessLogServiceImpl implements ApiAccessLogService {

    @Autowired
    private ApiAccessLogMapper apiAccessLogMapper;

    @Override
    public PageResult<ApiAccessLogDO> getApiAccessLogPage(ApiAccessLogPageReqVO pageReqVO) {
        return apiAccessLogMapper.selectPage(pageReqVO);
    }

    @Override
    public List<ApiAccessLogDO> getApiAccessLogList(ApiAccessLogExportReqVO exportReqVO) {
        return apiAccessLogMapper.selectList(exportReqVO);
    }

}
