package com.sdps.module.user.convert.logger;

import java.util.List;

import com.sdps.module.system.dal.dataobject.logger.LoginLogDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.sdps.common.pojo.PageResult;
import com.sdps.module.user.controller.admin.logger.vo.loginlog.LoginLogExcelVO;
import com.sdps.module.user.controller.admin.logger.vo.loginlog.LoginLogRespVO;

@Mapper
public interface LoginLogConvert {

    LoginLogConvert INSTANCE = Mappers.getMapper(LoginLogConvert.class);

    PageResult<LoginLogRespVO> convertPage(PageResult<LoginLogDO> page);

    List<LoginLogExcelVO> convertList(List<LoginLogDO> list);

}
