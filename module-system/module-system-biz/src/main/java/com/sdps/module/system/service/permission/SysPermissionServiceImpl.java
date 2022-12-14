package com.sdps.module.system.service.permission;

import static java.util.Collections.singleton;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import javax.annotation.PostConstruct;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.sdps.common.datapermission.core.annotation.DataPermission;
import com.sdps.common.enums.CommonStatusEnum;
import com.sdps.common.model.dataobject.permission.MenuDO;
import com.sdps.common.model.dataobject.permission.RoleDO;
import com.sdps.common.tenant.core.aop.TenantIgnore;
import com.sdps.common.util.collection.CollectionUtils;
import com.sdps.common.util.json.JsonUtils;
import com.sdps.module.system.api.permission.dto.DeptDataPermissionRespDTO;
import com.sdps.module.system.dal.dataobject.dept.DeptDO;
import com.sdps.module.system.dal.dataobject.permission.RoleMenuDO;
import com.sdps.module.system.dal.dataobject.permission.UserRoleDO;
import com.sdps.module.system.dal.mapper.permission.SysRoleMenuMapper;
import com.sdps.module.system.dal.mapper.permission.SysUserRoleMapper;
import com.sdps.module.system.enums.permission.DataScopeEnum;
import com.sdps.module.system.service.dept.SysDeptService;
import com.sdps.module.system.service.user.SysAdminUserService;

/**
 * ?????? Service ?????????
 *
 * @author ????????????
 */
@Service
@Slf4j
public class SysPermissionServiceImpl implements SysPermissionService {
	/**
	 * ???????????? {@link #schedulePeriodicRefresh()} ????????? ?????????????????? Redis Pub/Sub
	 * ?????????????????????????????????
	 */
	private static final long SCHEDULER_PERIOD = 5 * 60 * 1000L;

	/**
	 * ?????????????????????????????????????????? key??????????????? value????????????????????????
	 *
	 * ???????????? volatile ?????????????????????????????????????????????????????????
	 */
	@Getter
	@Setter
	// ??????????????????
	private volatile Multimap<Long, Long> roleMenuCache;
	/**
	 * ?????????????????????????????????????????? key??????????????? value????????????????????????
	 *
	 * ???????????? volatile ?????????????????????????????????????????????????????????
	 */
	@Getter
	@Setter
	// ??????????????????
	private volatile Multimap<Long, Long> menuRoleCache;
	/**
	 * ?????? RoleMenu ???????????????????????????????????????????????????????????????????????????
	 */
	@Getter
	private volatile Date roleMenuMaxUpdateTime;

	/**
	 * ?????????????????????????????????????????? key??????????????? value????????????????????????
	 *
	 * ???????????? volatile ?????????????????????????????????????????????????????????
	 */
	@Getter
	@Setter
	// ??????????????????
	private volatile Map<Long, Set<Long>> userRoleCache;
	/**
	 * ?????? UserRole ???????????????????????????????????????????????????????????????????????????
	 */
	@Getter
	private volatile Date userRoleMaxUpdateTime;

	@Autowired
	private SysRoleMenuMapper roleMenuMapper;
	@Autowired
	private SysUserRoleMapper userRoleMapper;
	@Autowired
	private SysRoleService roleService;
	@Autowired
	private SysMenuService menuService;
	@Autowired
	private SysDeptService deptService;
	@Autowired
	private SysAdminUserService userService;

	@Autowired
	@Lazy
	// ?????????????????????????????????
	private SysPermissionService self;

	@Scheduled(fixedDelay = SCHEDULER_PERIOD, initialDelay = SCHEDULER_PERIOD)
	public void schedulePeriodicRefresh() {
		self.initLocalCache();
	}

	@Override
	@PostConstruct
	@TenantIgnore
	// ????????????????????????????????????
	public void initLocalCache() {
		initUserRoleLocalCache();
		initRoleMenuLocalCache();
	}

	/**
	 * ????????? {@link #userRoleCache} ??????
	 */
	@VisibleForTesting
	void initUserRoleLocalCache() {
		// ??????????????????????????????????????????????????????
		List<UserRoleDO> userRoleList = loadUserRoleIfUpdate(userRoleMaxUpdateTime);
		if (CollUtil.isEmpty(userRoleList)) {
			return;
		}

		// ????????? userRoleCache ??????
		ImmutableMultimap.Builder<Long, Long> userRoleCacheBuilder = ImmutableMultimap
				.builder();
		userRoleList.forEach(userRoleDO -> userRoleCacheBuilder.put(
				userRoleDO.getUserId(), userRoleDO.getRoleId()));
		userRoleCache = CollectionUtils.convertMultiMap2(userRoleList,
				UserRoleDO::getUserId, UserRoleDO::getRoleId);
		userRoleMaxUpdateTime = CollectionUtils.getMaxValue(userRoleList,
				UserRoleDO::getUpdateTime);
		log.info("[initUserRoleLocalCache][?????????????????????????????????????????? {}]",
				userRoleList.size());
	}

	/**
	 * ???????????????????????????????????????????????????????????????????????????????????????????????????????????? ????????????????????????????????????
	 *
	 * @param maxUpdateTime
	 *            ???????????????????????????????????????????????????
	 * @return ??????????????????????????????
	 */
	protected List<UserRoleDO> loadUserRoleIfUpdate(Date maxUpdateTime) {
		// ????????????????????????????????????
		if (maxUpdateTime == null) { // ????????????????????????????????? DB ??????????????????
			log.info("[loadUserRoleIfUpdate][??????????????????????????????????????????]");
		} else { // ????????????????????????????????????????????????????????????
			if (userRoleMapper.selectCountByUpdateTimeGt(maxUpdateTime) == 0) {
				return null;
			}
			log.info("[loadUserRoleIfUpdate][??????????????????????????????????????????]");
		}
		// ?????????????????????????????????????????????????????????????????????????????????
		return userRoleMapper.selectList();
	}

	void initRoleMenuLocalCache() {
		// ??????????????????????????????????????????????????????
		List<RoleMenuDO> roleMenuList = loadRoleMenuIfUpdate(roleMenuMaxUpdateTime);
		if (CollUtil.isEmpty(roleMenuList)) {
			return;
		}

		// ????????? roleMenuCache ??? menuRoleCache ??????
		ImmutableMultimap.Builder<Long, Long> roleMenuCacheBuilder = ImmutableMultimap
				.builder();
		ImmutableMultimap.Builder<Long, Long> menuRoleCacheBuilder = ImmutableMultimap
				.builder();
		roleMenuList.forEach(roleMenuDO -> {
			roleMenuCacheBuilder.put(roleMenuDO.getRoleId(),
					roleMenuDO.getMenuId());
			menuRoleCacheBuilder.put(roleMenuDO.getMenuId(),
					roleMenuDO.getRoleId());
		});
		roleMenuCache = roleMenuCacheBuilder.build();
		menuRoleCache = menuRoleCacheBuilder.build();
		roleMenuMaxUpdateTime = CollectionUtils.getMaxValue(roleMenuList,
				RoleMenuDO::getUpdateTime);
		log.info("[initRoleMenuLocalCache][?????????????????????????????????????????? {}]",
				roleMenuList.size());
	}

	/**
	 * ???????????????????????????????????????????????????????????????????????????????????????????????????????????? ????????????????????????????????????
	 *
	 * @param maxUpdateTime
	 *            ???????????????????????????????????????????????????
	 * @return ??????????????????????????????
	 */
	protected List<RoleMenuDO> loadRoleMenuIfUpdate(Date maxUpdateTime) {
		// ????????????????????????????????????
		if (maxUpdateTime == null) { // ????????????????????????????????? DB ??????????????????
			log.info("[loadRoleMenuIfUpdate][??????????????????????????????????????????]");
		} else { // ????????????????????????????????????????????????????????????
			if (roleMenuMapper.selectCountByUpdateTimeGt(maxUpdateTime) == 0) {
				return null;
			}
			log.info("[loadRoleMenuIfUpdate][??????????????????????????????????????????]");
		}
		// ?????????????????????????????????????????????????????????????????????????????????
		return roleMenuMapper.selectList();
	}

	@Override
	public Set<Long> getUserRoleIdListByRoleIds(Collection<Long> roleIds) {
		return CollectionUtils.convertSet(
				userRoleMapper.selectListByRoleIds(roleIds),
				UserRoleDO::getUserId);
	}

	@Override
	public boolean hasAnyPermissions(Long userId, String... permissions) {
		// ????????????????????????????????????
		if (ArrayUtil.isEmpty(permissions)) {
			return true;
		}

		// ???????????????????????????????????????????????????????????????
		Set<Long> roleIds = getUserRoleIdsFromCache(userId,
				singleton(CommonStatusEnum.ENABLE.getStatus()));
		if (CollUtil.isEmpty(roleIds)) {
			return false;
		}
		// ??????????????????????????????????????????????????????
		if (roleService.hasAnySuperAdmin(roleIds)) {
			return true;
		}

		// ??????????????????????????????????????????
		return Arrays.stream(permissions).anyMatch(
				permission -> {
					List<MenuDO> menuList = menuService
							.getMenuListByPermissionFromCache(permission);
					// ??????????????????????????????????????????????????? Menu ???????????????
					if (CollUtil.isEmpty(menuList)) {
						return false;
					}
					// ??????????????????????????????????????????
					return menuList.stream().anyMatch(
							menu -> CollUtil.containsAny(roleIds,
									menuRoleCache.get(menu.getId())));
				});
	}

	@Override
	public boolean hasAnyRoles(Long userId, String... roles) {
		// ????????????????????????????????????
		if (ArrayUtil.isEmpty(roles)) {
			return true;
		}

		// ???????????????????????????????????????????????????????????????
		Set<Long> roleIds = getUserRoleIdsFromCache(userId,
				singleton(CommonStatusEnum.ENABLE.getStatus()));
		if (CollUtil.isEmpty(roleIds)) {
			return false;
		}
		// ??????????????????????????????????????????????????????
		if (roleService.hasAnySuperAdmin(roleIds)) {
			return true;
		}
		Set<String> userRoles = CollectionUtils.convertSet(
				roleService.getRolesFromCache(roleIds), RoleDO::getCode);
		return CollUtil.containsAny(userRoles, Sets.newHashSet(roles));
	}

	@Override
	@DataPermission(enable = false)
	// ????????????????????????????????????????????????????????????????????????
	@TenantIgnore
	// ????????????????????????????????????????????????????????????????????????????????????????????????????????????????????? User????????????????????????????????????????????????????????????????????????????????????
	public DeptDataPermissionRespDTO getDeptDataPermission(Long userId) {
		// ?????????????????????
		Set<Long> roleIds = getUserRoleIdsFromCache(userId,
				singleton(CommonStatusEnum.ENABLE.getStatus()));
		// ??????????????????????????????????????????
		DeptDataPermissionRespDTO result = new DeptDataPermissionRespDTO();
		if (CollUtil.isEmpty(roleIds)) {
			result.setSelf(true);
			return result;
		}
		List<RoleDO> roles = roleService.getRolesFromCache(roleIds);

		// ????????????????????????????????????????????? Guava ??? Suppliers ????????????????????????????????????????????? DB ?????????
		Supplier<Long> userDeptIdCache = Suppliers.memoize(() -> userService
				.getUser(userId).getDeptId());
		// ???????????????????????????
		for (RoleDO role : roles) {
			// ??????????????????
			if (role.getDataScope() == null) {
				continue;
			}
			// ????????????ALL
			if (Objects.equals(role.getDataScope(),
					DataScopeEnum.ALL.getScope())) {
				result.setAll(true);
				continue;
			}
			// ????????????DEPT_CUSTOM
			if (Objects.equals(role.getDataScope(),
					DataScopeEnum.DEPT_CUSTOM.getScope())) {
				CollUtil.addAll(result.getDeptIds(), role.getDataScopeDeptIds());
				// ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????
				// ?????????????????????????????? t_user ??? username ?????????????????? dept_id ?????????
				CollUtil.addAll(result.getDeptIds(), userDeptIdCache.get());
				continue;
			}
			// ????????????DEPT_ONLY
			if (Objects.equals(role.getDataScope(),
					DataScopeEnum.DEPT_ONLY.getScope())) {
				CollectionUtils.addIfNotNull(result.getDeptIds(),
						userDeptIdCache.get());
				continue;
			}
			// ????????????DEPT_DEPT_AND_CHILD
			if (Objects.equals(role.getDataScope(),
					DataScopeEnum.DEPT_AND_CHILD.getScope())) {
				List<DeptDO> depts = deptService.getDeptsByParentIdFromCache(
						userDeptIdCache.get(), true);
				CollUtil.addAll(result.getDeptIds(),
						CollectionUtils.convertList(depts, DeptDO::getId));
				// ????????????????????????
				CollUtil.addAll(result.getDeptIds(), userDeptIdCache.get());
				continue;
			}
			// ????????????SELF
			if (Objects.equals(role.getDataScope(),
					DataScopeEnum.SELF.getScope())) {
				result.setSelf(true);
				continue;
			}
			// ???????????????error log ??????
			log.error("[getDeptDataPermission][LoginUser({}) role({}) ????????????]",
					userId, JsonUtils.toJsonString(result));
		}
		return result;
	}

	@Override
	public Set<Long> getUserRoleIdsFromCache(Long userId,
			Collection<Integer> roleStatuses) {
		Set<Long> cacheRoleIds = userRoleCache.get(userId);
		// ??????????????????????????????????????????????????????????????????
		if (CollUtil.isEmpty(cacheRoleIds)) {
			return Collections.emptySet();
		}
		Set<Long> roleIds = new HashSet<Long>(cacheRoleIds);
		// ??????????????????
		if (CollectionUtil.isNotEmpty(roleStatuses)) {
			roleIds.removeIf(roleId -> {
				RoleDO role = roleService.getRoleFromCache(roleId);
				return role == null || !roleStatuses.contains(role.getStatus());
			});
		}
		return roleIds;
	}
}
