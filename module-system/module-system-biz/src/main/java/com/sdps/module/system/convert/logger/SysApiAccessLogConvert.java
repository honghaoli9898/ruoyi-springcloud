package com.sdps.module.system.convert.logger;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.sdps.module.system.api.logger.dto.ApiAccessLogCreateReqDTO;
import com.sdps.module.system.dal.dataobject.logger.ApiAccessLogDO;

/**
 * API 访问日志 Convert
 *
 * @author 芋道源码
 */
@Mapper
public interface SysApiAccessLogConvert {

    SysApiAccessLogConvert INSTANCE = Mappers.getMapper(SysApiAccessLogConvert.class);

    ApiAccessLogDO convert(ApiAccessLogCreateReqDTO bean);

}
