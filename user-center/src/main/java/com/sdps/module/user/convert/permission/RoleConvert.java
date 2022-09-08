package com.sdps.module.user.convert.permission;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.sdps.common.model.dataobject.permission.RoleDO;
import com.sdps.module.user.controller.admin.permission.vo.role.RoleCreateReqVO;
import com.sdps.module.user.controller.admin.permission.vo.role.RoleExcelVO;
import com.sdps.module.user.controller.admin.permission.vo.role.RoleRespVO;
import com.sdps.module.user.controller.admin.permission.vo.role.RoleSimpleRespVO;
import com.sdps.module.user.controller.admin.permission.vo.role.RoleUpdateReqVO;
import com.sdps.module.user.service.permission.bo.RoleCreateReqBO;

@Mapper
public interface RoleConvert {

    RoleConvert INSTANCE = Mappers.getMapper(RoleConvert.class);

    RoleDO convert(RoleUpdateReqVO bean);

    RoleRespVO convert(RoleDO bean);

    RoleDO convert(RoleCreateReqVO bean);

    List<RoleSimpleRespVO> convertList02(List<RoleDO> list);

    List<RoleExcelVO> convertList03(List<RoleDO> list);

    RoleDO convert(RoleCreateReqBO bean);

}
