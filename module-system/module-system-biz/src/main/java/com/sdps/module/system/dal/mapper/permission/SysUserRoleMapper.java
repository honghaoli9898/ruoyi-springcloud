package com.sdps.module.system.dal.mapper.permission;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.sdps.common.mybatis.core.mapper.BaseMapperX;
import com.sdps.module.system.dal.dataobject.permission.UserRoleDO;

@Mapper
public interface SysUserRoleMapper extends BaseMapperX<UserRoleDO> {

	default List<UserRoleDO> selectListByRoleIds(Collection<Long> roleIds) {
		return selectList(UserRoleDO::getRoleId, roleIds);
	}

	@Select("SELECT COUNT(*) FROM system_user_role WHERE update_time > #{maxUpdateTime}")
	Long selectCountByUpdateTimeGt(Date maxUpdateTime);

}
