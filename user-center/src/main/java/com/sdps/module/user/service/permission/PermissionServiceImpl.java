package com.sdps.module.user.service.permission;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;

import com.sdps.common.model.dataobject.permission.MenuDO;
import com.sdps.common.model.dataobject.permission.RoleDO;
import com.sdps.common.util.collection.CollectionUtils;
import com.sdps.common.util.collection.MapUtils;
import com.sdps.module.system.dal.dataobject.permission.RoleMenuDO;
import com.sdps.module.system.dal.dataobject.permission.UserRoleDO;
import com.sdps.module.system.service.dept.SysDeptService;
import com.sdps.module.system.service.permission.SysPermissionService;
import com.sdps.module.system.service.permission.SysRoleService;
import com.sdps.module.system.service.user.SysAdminUserService;
import com.sdps.module.user.dal.mapper.permission.RoleMenuBatchInsertMapper;
import com.sdps.module.user.dal.mapper.permission.RoleMenuMapper;
import com.sdps.module.user.dal.mapper.permission.UserRoleBatchInsertMapper;
import com.sdps.module.user.dal.mapper.permission.UserRoleMapper;
import com.sdps.module.user.mq.producer.permission.PermissionProducer;

/**
 * 权限 Service 实现类
 *
 * @author 芋道源码
 */
@Service
public class PermissionServiceImpl implements PermissionService {
	@Autowired
	private RoleMenuMapper roleMenuMapper;
	@Autowired
	private RoleMenuBatchInsertMapper roleMenuBatchInsertMapper;
	@Autowired
	private UserRoleMapper userRoleMapper;
	@Autowired
	private UserRoleBatchInsertMapper userRoleBatchInsertMapper;

	@Autowired
	private SysRoleService sysRoleService;

	@Autowired
	private RoleService roleService;
	@Autowired
	private MenuService menuService;
	@Autowired
	private SysDeptService deptService;
	@Autowired
	private SysAdminUserService userService;

	@Autowired
	private PermissionProducer permissionProducer;

	@Autowired
	@Lazy
	private SysPermissionService self;

	@Override
	public List<MenuDO> getRoleMenuListFromCache(Collection<Long> roleIds,
			Collection<Integer> menuTypes, Collection<Integer> menusStatuses) {
		// 任一一个参数为空时，不返回任何菜单
		if (CollectionUtils.isAnyEmpty(roleIds, menuTypes, menusStatuses)) {
			return Collections.emptyList();
		}

		// 判断角色是否包含超级管理员。如果是超级管理员，获取到全部
		List<RoleDO> roleList = sysRoleService.getRolesFromCache(roleIds);
		if (sysRoleService.hasAnySuperAdmin(roleList)) {
			return menuService.getMenuListFromCache(menuTypes, menusStatuses);
		}

		// 获得角色拥有的菜单关联
		List<Long> menuIds = MapUtils.getList(self.getRoleMenuCache(), roleIds);
		return menuService.getMenuListFromCache(menuIds, menuTypes,
				menusStatuses);
	}

	@Override
	public Set<Long> getRoleMenuIds(Long roleId) {
		// 如果是管理员的情况下，获取全部菜单编号
		if (sysRoleService.hasAnySuperAdmin(Collections.singleton(roleId))) {
			return CollectionUtils.convertSet(menuService.getMenus(),
					MenuDO::getId);
		}
		// 如果是非管理员的情况下，获得拥有的菜单编号
		return CollectionUtils.convertSet(
				roleMenuMapper.selectListByRoleId(roleId),
				RoleMenuDO::getMenuId);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void assignRoleMenu(Long roleId, Set<Long> menuIds) {
		// 获得角色拥有菜单编号
		Set<Long> dbMenuIds = CollectionUtils.convertSet(
				roleMenuMapper.selectListByRoleId(roleId),
				RoleMenuDO::getMenuId);
		// 计算新增和删除的菜单编号
		Collection<Long> createMenuIds = CollUtil.subtract(menuIds, dbMenuIds);
		Collection<Long> deleteMenuIds = CollUtil.subtract(dbMenuIds, menuIds);
		// 执行新增和删除。对于已经授权的菜单，不用做任何处理
		if (!CollectionUtil.isEmpty(createMenuIds)) {
			roleMenuBatchInsertMapper.saveBatch(CollectionUtils.convertList(
					createMenuIds, menuId -> {
						RoleMenuDO entity = new RoleMenuDO();
						entity.setRoleId(roleId);
						entity.setMenuId(menuId);
						return entity;
					}));
		}
		if (!CollectionUtil.isEmpty(deleteMenuIds)) {
			roleMenuMapper.deleteListByRoleIdAndMenuIds(roleId, deleteMenuIds);
		}
		// 发送刷新消息. 注意，需要事务提交后，在进行发送刷新消息。不然 db 还未提交，结果缓存先刷新了
		TransactionSynchronizationManager
				.registerSynchronization(new TransactionSynchronization() {

					@Override
					public void afterCommit() {
						permissionProducer.sendRoleMenuRefreshMessage();
					}

				});
	}

	@Override
	public Set<Long> getUserRoleIdListByUserId(Long userId) {
		return CollectionUtils.convertSet(
				userRoleMapper.selectListByUserId(userId),
				UserRoleDO::getRoleId);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void assignUserRole(Long userId, Set<Long> roleIds) {
		// 获得角色拥有角色编号
		Set<Long> dbRoleIds = CollectionUtils.convertSet(
				userRoleMapper.selectListByUserId(userId),
				UserRoleDO::getRoleId);
		// 计算新增和删除的角色编号
		Collection<Long> createRoleIds = CollUtil.subtract(roleIds, dbRoleIds);
		Collection<Long> deleteMenuIds = CollUtil.subtract(dbRoleIds, roleIds);
		// 执行新增和删除。对于已经授权的角色，不用做任何处理
		if (!CollectionUtil.isEmpty(createRoleIds)) {
			userRoleBatchInsertMapper.saveBatch(CollectionUtils.convertList(
					createRoleIds, roleId -> {
						UserRoleDO entity = new UserRoleDO();
						entity.setUserId(userId);
						entity.setRoleId(roleId);
						return entity;
					}));
		}
		if (!CollectionUtil.isEmpty(deleteMenuIds)) {
			userRoleMapper
					.deleteListByUserIdAndRoleIdIds(userId, deleteMenuIds);
		}
		// 发送刷新消息. 注意，需要事务提交后，在进行发送刷新消息。不然 db 还未提交，结果缓存先刷新了
		TransactionSynchronizationManager
				.registerSynchronization(new TransactionSynchronization() {

					@Override
					public void afterCommit() {
						permissionProducer.sendUserRoleRefreshMessage();
					}

				});
	}

	@Override
	public void assignRoleDataScope(Long roleId, Integer dataScope,
			Set<Long> dataScopeDeptIds) {
		roleService.updateRoleDataScope(roleId, dataScope, dataScopeDeptIds);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void processRoleDeleted(Long roleId) {
		// 标记删除 UserRole
		userRoleMapper.deleteListByRoleId(roleId);
		// 标记删除 RoleMenu
		roleMenuMapper.deleteListByRoleId(roleId);
		// 发送刷新消息. 注意，需要事务提交后，在进行发送刷新消息。不然 db 还未提交，结果缓存先刷新了
		TransactionSynchronizationManager
				.registerSynchronization(new TransactionSynchronization() {

					@Override
					public void afterCommit() {
						permissionProducer.sendRoleMenuRefreshMessage();
						permissionProducer.sendUserRoleRefreshMessage();
					}

				});
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void processMenuDeleted(Long menuId) {
		roleMenuMapper.deleteListByMenuId(menuId);
		// 发送刷新消息. 注意，需要事务提交后，在进行发送刷新消息。不然 db 还未提交，结果缓存先刷新了
		TransactionSynchronizationManager
				.registerSynchronization(new TransactionSynchronization() {

					@Override
					public void afterCommit() {
						permissionProducer.sendRoleMenuRefreshMessage();
					}

				});
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void processUserDeleted(Long userId) {
		userRoleMapper.deleteListByUserId(userId);
		TransactionSynchronizationManager
				.registerSynchronization(new TransactionSynchronization() {
					@Override
					public void afterCommit() {
						permissionProducer.sendUserRoleRefreshMessage();
					}
				});
	}

}
