package com.sdps.module.system.dal.mapper.permission;

import java.util.Date;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.sdps.common.model.dataobject.permission.RoleDO;
import com.sdps.common.mybatis.core.mapper.BaseMapperX;

@Mapper
public interface SysRoleMapper extends BaseMapperX<RoleDO> {


    @Select("SELECT COUNT(*) FROM system_role WHERE update_time > #{maxUpdateTime}")
    Long selectCountByUpdateTimeGt(Date maxUpdateTime);

}
