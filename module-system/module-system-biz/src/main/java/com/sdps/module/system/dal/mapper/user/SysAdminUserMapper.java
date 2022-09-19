package com.sdps.module.system.dal.mapper.user;

import java.util.Collection;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.sdps.common.model.dataobject.user.AdminUserDO;
import com.sdps.common.mybatis.core.mapper.BaseMapperX;

@Mapper
public interface SysAdminUserMapper extends BaseMapperX<AdminUserDO> {
	default List<AdminUserDO> selectListByDeptIds(Collection<Long> deptIds) {
		return selectList(AdminUserDO::getDeptId, deptIds);
	}
}
