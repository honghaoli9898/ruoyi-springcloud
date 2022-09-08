package com.sdps.module.user.convert.logger;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.sdps.common.pojo.PageResult;
import com.sdps.module.system.dal.dataobject.logger.ApiErrorLogDO;
import com.sdps.module.user.controller.admin.logger.vo.apierrorlog.ApiErrorLogExcelVO;
import com.sdps.module.user.controller.admin.logger.vo.apierrorlog.ApiErrorLogRespVO;

/**
 * API 错误日志 Convert
 *
 * @author 芋道源码
 */
@Mapper
public interface ApiErrorLogConvert {

    ApiErrorLogConvert INSTANCE = Mappers.getMapper(ApiErrorLogConvert.class);

    ApiErrorLogRespVO convert(ApiErrorLogDO bean);

    PageResult<ApiErrorLogRespVO> convertPage(PageResult<ApiErrorLogDO> page);

    List<ApiErrorLogExcelVO> convertList02(List<ApiErrorLogDO> list);

}
