package com.sdps.module.system.dal.mapper.user;

import org.apache.ibatis.annotations.Mapper;

import com.sdps.common.model.dataobject.user.AdminUserDO;
import com.sdps.common.mybatis.core.mapper.BaseMapperX;

@Mapper
public interface SysAdminUserMapper extends BaseMapperX<AdminUserDO> {

}
