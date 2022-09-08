package com.sdps.module.system.service.logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.sdps.module.system.api.logger.dto.ApiErrorLogCreateReqDTO;
import com.sdps.module.system.convert.logger.SysApiErrorLogConvert;
import com.sdps.module.system.dal.dataobject.logger.ApiErrorLogDO;
import com.sdps.module.system.dal.mapper.logger.SysApiErrorLogMapper;
import com.sdps.module.system.enums.logger.ApiErrorLogProcessStatusEnum;

/**
 * API 错误日志 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class SysApiErrorLogServiceImpl implements SysApiErrorLogService {

	@Autowired
	private SysApiErrorLogMapper apiErrorLogMapper;

	@Override
	public void createApiErrorLog(ApiErrorLogCreateReqDTO createDTO) {
		ApiErrorLogDO apiErrorLog = SysApiErrorLogConvert.INSTANCE
				.convert(createDTO);
		apiErrorLog.setProcessStatus(ApiErrorLogProcessStatusEnum.INIT
				.getStatus());
		apiErrorLog.setCreator(createDTO.getCreator());
		apiErrorLog.setUpdater(createDTO.getCreator());
		apiErrorLogMapper.insert(apiErrorLog);
	}

}
