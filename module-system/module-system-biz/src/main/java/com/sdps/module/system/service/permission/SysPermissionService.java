package com.sdps.module.system.service.permission;

import java.util.Collection;
import java.util.Set;

import org.springframework.lang.Nullable;

import com.google.common.collect.Multimap;
import com.sdps.module.system.api.permission.dto.DeptDataPermissionRespDTO;

/**
 * 权限 Service 接口
 *
 * 提供用户-角色、角色-菜单、角色-部门的关联权限处理
 *
 * @author 芋道源码
 */
public interface SysPermissionService {

	/**
	 * 初始化权限的本地缓存
	 */
	void initLocalCache();

	/**
	 * 获得用户拥有的角色编号集合，从缓存中获取
	 *
	 * @param userId
	 *            用户编号
	 * @param roleStatuses
	 *            角色状态集合. 允许为空，为空时不过滤
	 * @return 角色编号集合
	 */
	Set<Long> getUserRoleIdsFromCache(Long userId,
			@Nullable Collection<Integer> roleStatuses);

	/**
	 * 获得拥有多个角色的用户编号集合
	 *
	 * @param roleIds
	 *            角色编号集合
	 * @return 用户编号集合
	 */
	Set<Long> getUserRoleIdListByRoleIds(Collection<Long> roleIds);

	/**
	 * 判断是否有权限，任一一个即可
	 *
	 * @param userId
	 *            用户编号
	 * @param permissions
	 *            权限
	 * @return 是否
	 */
	boolean hasAnyPermissions(Long userId, String... permissions);

	/**
	 * 判断是否有角色，任一一个即可
	 *
	 * @param roles
	 *            角色数组
	 * @return 是否
	 */
	boolean hasAnyRoles(Long userId, String... roles);

	/**
	 * 获得登陆用户的部门数据权限
	 *
	 * @param userId
	 *            用户编号
	 * @return 部门数据权限
	 */
	DeptDataPermissionRespDTO getDeptDataPermission(Long userId);

	Multimap<Long, Long> getRoleMenuCache();

	Multimap<Long, Long> getMenuRoleCache();

}
