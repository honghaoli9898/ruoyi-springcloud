package com.sdps.module.user.service.tenant;

import static java.util.Collections.singleton;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;

import com.sdps.common.context.TenantContextHolder;
import com.sdps.common.exception.util.ServiceExceptionUtil;
import com.sdps.common.model.dataobject.permission.MenuDO;
import com.sdps.common.model.dataobject.permission.RoleDO;
import com.sdps.common.pojo.PageResult;
import com.sdps.common.tenant.config.TenantProperties;
import com.sdps.common.tenant.core.util.TenantUtils;
import com.sdps.common.util.collection.CollectionUtils;
import com.sdps.module.system.dal.dataobject.tenant.TenantDO;
import com.sdps.module.system.enums.ErrorCodeConstants;
import com.sdps.module.system.enums.permission.RoleCodeEnum;
import com.sdps.module.system.enums.permission.RoleTypeEnum;
import com.sdps.module.system.service.tenant.SysTenantService;
import com.sdps.module.user.controller.admin.permission.vo.role.RoleCreateReqVO;
import com.sdps.module.user.controller.admin.tenant.vo.tenant.TenantCreateReqVO;
import com.sdps.module.user.controller.admin.tenant.vo.tenant.TenantExportReqVO;
import com.sdps.module.user.controller.admin.tenant.vo.tenant.TenantPageReqVO;
import com.sdps.module.user.controller.admin.tenant.vo.tenant.TenantUpdateReqVO;
import com.sdps.module.user.convert.tenant.TenantConvert;
import com.sdps.module.user.dal.dataobject.tenant.TenantPackageDO;
import com.sdps.module.user.dal.mapper.tenant.TenantMapper;
import com.sdps.module.user.service.handler.TenantInfoHandler;
import com.sdps.module.user.service.handler.TenantMenuHandler;
import com.sdps.module.user.service.permission.MenuService;
import com.sdps.module.user.service.permission.PermissionService;
import com.sdps.module.user.service.permission.RoleService;
import com.sdps.module.user.service.user.AdminUserService;

/**
 * 租户 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
@Slf4j
public class TenantServiceImpl implements TenantService {

	@Autowired(required = false)
	private TenantProperties tenantProperties;

	@Autowired
	private TenantMapper tenantMapper;

	@Autowired
	private TenantPackageService tenantPackageService;
	@Autowired
	@Lazy
	// 延迟，避免循环依赖报错
	private AdminUserService userService;
	@Autowired
	private RoleService roleService;
	@Autowired
	private MenuService menuService;
	@Autowired
	private PermissionService permissionService;
	@Autowired
	@Lazy
	private SysTenantService sysTenantService;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Long createTenant(TenantCreateReqVO createReqVO) {
		// 校验套餐被禁用
		TenantPackageDO tenantPackage = tenantPackageService
				.validTenantPackage(createReqVO.getPackageId());

		// 创建租户
		TenantDO tenant = TenantConvert.INSTANCE.convert(createReqVO);
		tenantMapper.insert(tenant);

		TenantUtils.execute(tenant.getId(), () -> {
			// 创建角色
				Long roleId = createRole(tenantPackage);
				// 创建用户，并分配角色
				Long userId = createUser(roleId, createReqVO);
				// 修改租户的管理员
				TenantDO tenantDO = new TenantDO();
				tenantDO.setId(tenant.getId());
				tenantDO.setContactUserId(userId);
				tenantMapper.updateById(tenantDO);
			});
		return tenant.getId();
	}

	private Long createUser(Long roleId, TenantCreateReqVO createReqVO) {
		// 创建用户
		Long userId = userService.createUser(TenantConvert.INSTANCE
				.convert02(createReqVO));
		// 分配角色
		permissionService.assignUserRole(userId, singleton(roleId));
		return userId;
	}

	private Long createRole(TenantPackageDO tenantPackage) {
		// 创建角色
		RoleCreateReqVO reqVO = new RoleCreateReqVO();
		reqVO.setName(RoleCodeEnum.TENANT_ADMIN.getName());
		reqVO.setCode(RoleCodeEnum.TENANT_ADMIN.getCode());
		reqVO.setSort(0);
		reqVO.setRemark("系统自动生成");
		Long roleId = roleService.createRole(reqVO,
				RoleTypeEnum.SYSTEM.getType());
		// 分配权限
		permissionService.assignRoleMenu(roleId, tenantPackage.getMenuIds());
		return roleId;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateTenant(TenantUpdateReqVO updateReqVO) {
		// 校验存在
		TenantDO tenant = checkUpdateTenant(updateReqVO.getId());
		// 校验套餐被禁用
		TenantPackageDO tenantPackage = tenantPackageService
				.validTenantPackage(updateReqVO.getPackageId());

		// 更新租户
		TenantDO updateObj = TenantConvert.INSTANCE.convert(updateReqVO);
		tenantMapper.updateById(updateObj);
		// 如果套餐发生变化，则修改其角色的权限
		if (ObjectUtil.notEqual(tenant.getPackageId(),
				updateReqVO.getPackageId())) {
			updateTenantRoleMenu(tenant.getId(), tenantPackage.getMenuIds());
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateTenantRoleMenu(Long tenantId, Set<Long> menuIds) {
		TenantUtils
				.execute(tenantId, () -> {
					// 获得所有角色
						List<RoleDO> roles = roleService.getRoles(null);
						roles.forEach(role -> Assert.isTrue(
								tenantId.equals(role.getTenantId()),
								"角色({}/{}) 租户不匹配", role.getId(),
								role.getTenantId(), tenantId)); // 兜底校验
						// 重新分配每个角色的权限
						roles.forEach(role -> {
							// 如果是租户管理员，重新分配其权限为租户套餐的权限
							if (Objects.equals(role.getCode(),
									RoleCodeEnum.TENANT_ADMIN.getCode())) {
								permissionService.assignRoleMenu(role.getId(),
										menuIds);
								log.info(
										"[updateTenantRoleMenu][租户管理员({}/{}) 的权限修改为({})]",
										role.getId(), role.getTenantId(),
										menuIds);
								return;
							}
							// 如果是其他角色，则去掉超过套餐的权限
							Set<Long> roleMenuIds = permissionService
									.getRoleMenuIds(role.getId());
							roleMenuIds = CollUtil.intersectionDistinct(
									roleMenuIds, menuIds);
							permissionService.assignRoleMenu(role.getId(),
									roleMenuIds);
							log.info(
									"[updateTenantRoleMenu][角色({}/{}) 的权限修改为({})]",
									role.getId(), role.getTenantId(),
									roleMenuIds);
						});
					});
	}

	@Override
	public void deleteTenant(Long id) {
		// 校验存在
		checkUpdateTenant(id);
		// 删除
		tenantMapper.deleteById(id);
	}

	private TenantDO checkUpdateTenant(Long id) {
		TenantDO tenant = tenantMapper.selectById(id);
		if (tenant == null) {
			throw ServiceExceptionUtil
					.exception(ErrorCodeConstants.TENANT_NOT_EXISTS);
		}
		// 内置租户，不允许删除
		if (isSystemTenant(tenant)) {
			throw ServiceExceptionUtil
					.exception(ErrorCodeConstants.TENANT_CAN_NOT_UPDATE_SYSTEM);
		}
		return tenant;
	}

	@Override
	public PageResult<TenantDO> getTenantPage(TenantPageReqVO pageReqVO) {
		return tenantMapper.selectPage(pageReqVO);
	}

	@Override
	public List<TenantDO> getTenantList(TenantExportReqVO exportReqVO) {
		return tenantMapper.selectList(exportReqVO);
	}

	@Override
	public TenantDO getTenantByName(String name) {
		return tenantMapper.selectByName(name);
	}

	@Override
	public Long getTenantCountByPackageId(Long packageId) {
		return tenantMapper.selectCountByPackageId(packageId);
	}

	@Override
	public List<TenantDO> getTenantListByPackageId(Long packageId) {
		return tenantMapper.selectListByPackageId(packageId);
	}

	@Override
	public void handleTenantInfo(TenantInfoHandler handler) {
		// 如果禁用，则不执行逻辑
		if (isTenantDisable()) {
			return;
		}
		// 获得租户
		TenantDO tenant = sysTenantService.getTenant(TenantContextHolder
				.getRequiredTenantId());
		// 执行处理器
		handler.handle(tenant);
	}

	@Override
	public void handleTenantMenu(TenantMenuHandler handler) {
		// 如果禁用，则不执行逻辑
		if (isTenantDisable()) {
			return;
		}
		// 获得租户，然后获得菜单
		TenantDO tenant = sysTenantService.getTenant(TenantContextHolder
				.getRequiredTenantId());
		Set<Long> menuIds;
		if (isSystemTenant(tenant)) { // 系统租户，菜单是全量的
			menuIds = CollectionUtils.convertSet(menuService.getMenus(),
					MenuDO::getId);
		} else {
			menuIds = tenantPackageService.getTenantPackage(
					tenant.getPackageId()).getMenuIds();
		}
		// 执行处理器
		handler.handle(menuIds);
	}

	private static boolean isSystemTenant(TenantDO tenant) {
		return Objects
				.equals(tenant.getPackageId(), TenantDO.PACKAGE_ID_SYSTEM);
	}

	private boolean isTenantDisable() {
		return tenantProperties == null
				|| Boolean.FALSE.equals(tenantProperties.getEnable());
	}

}
