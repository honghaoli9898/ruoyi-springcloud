package com.sdps.module.system.convert.errorcode;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.sdps.module.system.api.errorcode.dto.ErrorCodeAutoGenerateReqDTO;
import com.sdps.module.system.api.errorcode.dto.ErrorCodeRespDTO;
import com.sdps.module.system.dal.dataobject.errorcode.ErrorCodeDO;

/**
 * 错误码 Convert
 *
 * @author 芋道源码
 */
@Mapper
public interface SysErrorCodeConvert {

	SysErrorCodeConvert INSTANCE = Mappers.getMapper(SysErrorCodeConvert.class);

	ErrorCodeDO convert(ErrorCodeAutoGenerateReqDTO bean);

	List<ErrorCodeRespDTO> convertList03(List<ErrorCodeDO> list);

}
