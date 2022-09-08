package com.sdps.module.system.service.logger;

import com.sdps.module.system.api.logger.dto.ApiErrorLogCreateReqDTO;

/**
 * API 错误日志 Service 接口
 *
 * @author 芋道源码
 */
public interface SysApiErrorLogService {

	/**
	 * 创建 API 错误日志
	 *
	 * @param createReqDTO
	 *            API 错误日志
	 */
	void createApiErrorLog(ApiErrorLogCreateReqDTO createReqDTO);

}
