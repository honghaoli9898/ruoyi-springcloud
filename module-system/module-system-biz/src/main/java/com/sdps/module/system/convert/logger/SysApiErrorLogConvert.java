package com.sdps.module.system.convert.logger;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.sdps.module.system.api.logger.dto.ApiErrorLogCreateReqDTO;
import com.sdps.module.system.dal.dataobject.logger.ApiErrorLogDO;

/**
 * API 错误日志 Convert
 *
 * @author 芋道源码
 */
@Mapper
public interface SysApiErrorLogConvert {

    SysApiErrorLogConvert INSTANCE = Mappers.getMapper(SysApiErrorLogConvert.class);

    ApiErrorLogDO convert(ApiErrorLogCreateReqDTO bean);

}
