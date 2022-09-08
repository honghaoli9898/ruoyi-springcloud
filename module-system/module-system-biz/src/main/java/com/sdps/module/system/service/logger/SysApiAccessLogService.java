package com.sdps.module.system.service.logger;

import com.sdps.module.system.api.logger.dto.ApiAccessLogCreateReqDTO;

/**
 * API 访问日志 Service 接口
 *
 * @author 芋道源码
 */
public interface SysApiAccessLogService {

	/**
	 * 创建 API 访问日志
	 *
	 * @param createReqDTO
	 *            API 访问日志
	 */
	void createApiAccessLog(ApiAccessLogCreateReqDTO createReqDTO);

}
