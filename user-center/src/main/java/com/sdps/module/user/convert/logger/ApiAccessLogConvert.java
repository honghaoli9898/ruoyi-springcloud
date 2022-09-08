package com.sdps.module.user.convert.logger;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.sdps.common.pojo.PageResult;
import com.sdps.module.system.dal.dataobject.logger.ApiAccessLogDO;
import com.sdps.module.user.controller.admin.logger.vo.apiaccesslog.ApiAccessLogExcelVO;
import com.sdps.module.user.controller.admin.logger.vo.apiaccesslog.ApiAccessLogRespVO;

/**
 * API 访问日志 Convert
 *
 * @author 芋道源码
 */
@Mapper
public interface ApiAccessLogConvert {

    ApiAccessLogConvert INSTANCE = Mappers.getMapper(ApiAccessLogConvert.class);

    ApiAccessLogRespVO convert(ApiAccessLogDO bean);

    List<ApiAccessLogRespVO> convertList(List<ApiAccessLogDO> list);

    PageResult<ApiAccessLogRespVO> convertPage(PageResult<ApiAccessLogDO> page);

    List<ApiAccessLogExcelVO> convertList02(List<ApiAccessLogDO> list);

}
