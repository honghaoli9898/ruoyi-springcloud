package com.sdps.module.system.dal.mapper.permission;

import java.util.Date;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.sdps.common.model.dataobject.permission.MenuDO;
import com.sdps.common.mybatis.core.mapper.BaseMapperX;

@Mapper
public interface SysMenuMapper extends BaseMapperX<MenuDO> {

    @Select("SELECT COUNT(*) FROM system_menu WHERE update_time > #{maxUpdateTime}")
    Long selectCountByUpdateTimeGt(Date maxUpdateTime);

}
