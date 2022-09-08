package com.sdps.module.system.api.logger;

import javax.validation.Valid;

import com.sdps.module.system.api.logger.dto.ApiAccessLogCreateReqDTO;

/**
 * API 访问日志的 API 接口
 *
 * @author 芋道源码
 */
public interface ApiAccessLogApi {

	/**
	 * 创建 API 访问日志
	 *
	 * @param createDTO
	 *            创建信息
	 */
	void createApiAccessLog(@Valid ApiAccessLogCreateReqDTO createDTO);

}
