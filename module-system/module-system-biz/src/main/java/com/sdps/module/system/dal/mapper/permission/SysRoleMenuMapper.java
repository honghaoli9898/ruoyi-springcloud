package com.sdps.module.system.dal.mapper.permission;

import java.util.Date;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.sdps.common.mybatis.core.mapper.BaseMapperX;
import com.sdps.module.system.dal.dataobject.permission.RoleMenuDO;

@Mapper
public interface SysRoleMenuMapper extends BaseMapperX<RoleMenuDO> {

    @Select("SELECT COUNT(*) FROM system_role_menu WHERE update_time > #{maxUpdateTime}")
    Long selectCountByUpdateTimeGt(Date maxUpdateTime);

}
