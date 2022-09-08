package com.sdps.module.system.service.permission;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.sdps.common.model.dataobject.permission.RoleDO;

/**
 * 角色 Service 接口
 *
 * @author 芋道源码
 */
public interface SysRoleService {

	/**
	 * 初始化角色的本地缓存
	 */
	void initLocalCache();

	/**
	 * 获得角色数组，从缓存中
	 *
	 * @param ids
	 *            角色编号数组
	 * @return 角色数组
	 */
	List<RoleDO> getRolesFromCache(Collection<Long> ids);

	/**
	 * 判断角色编号数组中，是否有管理员
	 *
	 * @param ids
	 *            角色编号数组
	 * @return 是否有管理员
	 */
	default boolean hasAnySuperAdmin(Set<Long> ids) {
		return hasAnySuperAdmin(getRolesFromCache(ids));
	}

	/**
	 * 判断角色数组中，是否有超级管理员
	 *
	 * @param roleList
	 *            角色数组
	 * @return 是否有管理员
	 */
	boolean hasAnySuperAdmin(Collection<RoleDO> roleList);
	
    /**
     * 获得角色，从缓存中
     *
     * @param id 角色编号
     * @return 角色
     */
    RoleDO getRoleFromCache(Long id);
}
