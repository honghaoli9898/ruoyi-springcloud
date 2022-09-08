package com.sdps.module.system.convert.logger;

import com.sdps.module.system.api.logger.dto.LoginLogCreateReqDTO;
import com.sdps.module.system.dal.dataobject.logger.LoginLogDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SysLoginLogConvert {

    SysLoginLogConvert INSTANCE = Mappers.getMapper(SysLoginLogConvert.class);

    LoginLogDO convert(LoginLogCreateReqDTO bean);

}
