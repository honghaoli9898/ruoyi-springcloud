package com.sdps.module.user.service.logger;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.sdps.common.exception.util.ServiceExceptionUtil;
import com.sdps.common.pojo.PageResult;
import com.sdps.module.system.dal.dataobject.logger.ApiErrorLogDO;
import com.sdps.module.system.enums.ErrorCodeConstants;
import com.sdps.module.system.enums.logger.ApiErrorLogProcessStatusEnum;
import com.sdps.module.user.controller.admin.logger.vo.apierrorlog.ApiErrorLogExportReqVO;
import com.sdps.module.user.controller.admin.logger.vo.apierrorlog.ApiErrorLogPageReqVO;
import com.sdps.module.user.dal.mapper.logger.ApiErrorLogMapper;

/**
 * API 错误日志 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class ApiErrorLogServiceImpl implements ApiErrorLogService {

	@Autowired
	private ApiErrorLogMapper apiErrorLogMapper;


	@Override
	public PageResult<ApiErrorLogDO> getApiErrorLogPage(
			ApiErrorLogPageReqVO pageReqVO) {
		return apiErrorLogMapper.selectPage(pageReqVO);
	}

	@Override
	public List<ApiErrorLogDO> getApiErrorLogList(
			ApiErrorLogExportReqVO exportReqVO) {
		return apiErrorLogMapper.selectList(exportReqVO);
	}

	@Override
	public void updateApiErrorLogProcess(Long id, Integer processStatus,
			Long processUserId) {
		ApiErrorLogDO errorLog = apiErrorLogMapper.selectById(id);
		if (errorLog == null) {
			throw ServiceExceptionUtil
					.exception(ErrorCodeConstants.API_ERROR_LOG_NOT_FOUND);
		}
		if (!ApiErrorLogProcessStatusEnum.INIT.getStatus().equals(
				errorLog.getProcessStatus())) {
			throw ServiceExceptionUtil
					.exception(ErrorCodeConstants.API_ERROR_LOG_PROCESSED);
		}
		// 标记处理
		apiErrorLogMapper.updateById(ApiErrorLogDO.builder().id(id)
				.processStatus(processStatus).processUserId(processUserId)
				.processTime(new Date()).build());
	}

}
