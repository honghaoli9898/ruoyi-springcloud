package com.sdps.module.system.service.logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.sdps.module.system.api.logger.dto.ApiAccessLogCreateReqDTO;
import com.sdps.module.system.convert.logger.SysApiAccessLogConvert;
import com.sdps.module.system.dal.dataobject.logger.ApiAccessLogDO;
import com.sdps.module.system.dal.mapper.logger.SysApiAccessLogMapper;

/**
 * API 访问日志 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class SysApiAccessLogServiceImpl implements SysApiAccessLogService {

	@Autowired
	private SysApiAccessLogMapper apiAccessLogMapper;

	@Override
	public void createApiAccessLog(ApiAccessLogCreateReqDTO createDTO) {
		ApiAccessLogDO apiAccessLog = SysApiAccessLogConvert.INSTANCE
				.convert(createDTO);
		apiAccessLog.setCreator(createDTO.getCreator());
		apiAccessLog.setUpdater(createDTO.getCreator());
		apiAccessLogMapper.insert(apiAccessLog);
	}

}
