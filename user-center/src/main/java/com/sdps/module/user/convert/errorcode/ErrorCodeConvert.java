package com.sdps.module.user.convert.errorcode;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.sdps.common.pojo.PageResult;
import com.sdps.module.system.dal.dataobject.errorcode.ErrorCodeDO;
import com.sdps.module.user.controller.admin.errorcode.vo.ErrorCodeCreateReqVO;
import com.sdps.module.user.controller.admin.errorcode.vo.ErrorCodeExcelVO;
import com.sdps.module.user.controller.admin.errorcode.vo.ErrorCodeRespVO;
import com.sdps.module.user.controller.admin.errorcode.vo.ErrorCodeUpdateReqVO;

/**
 * 错误码 Convert
 *
 * @author 芋道源码
 */
@Mapper
public interface ErrorCodeConvert {

	ErrorCodeConvert INSTANCE = Mappers.getMapper(ErrorCodeConvert.class);

	ErrorCodeDO convert(ErrorCodeCreateReqVO bean);

	ErrorCodeDO convert(ErrorCodeUpdateReqVO bean);

	ErrorCodeRespVO convert(ErrorCodeDO bean);

	List<ErrorCodeRespVO> convertList(List<ErrorCodeDO> list);

	PageResult<ErrorCodeRespVO> convertPage(PageResult<ErrorCodeDO> page);

	List<ErrorCodeExcelVO> convertList02(List<ErrorCodeDO> list);

}
