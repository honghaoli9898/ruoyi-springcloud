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
 * ?????? Service ?????????
 *
 * @author ????????????
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
	// ?????????????????????????????????
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
		// ?????????????????????
		TenantPackageDO tenantPackage = tenantPackageService
				.validTenantPackage(createReqVO.getPackageId());

		// ????????????
		TenantDO tenant = TenantConvert.INSTANCE.convert(createReqVO);
		tenantMapper.insert(tenant);

		TenantUtils.execute(tenant.getId(), () -> {
			// ????????????
				Long roleId = createRole(tenantPackage);
				// ??????????????????????????????
				Long userId = createUser(roleId, createReqVO);
				// ????????????????????????
				TenantDO tenantDO = new TenantDO();
				tenantDO.setId(tenant.getId());
				tenantDO.setContactUserId(userId);
				tenantMapper.updateById(tenantDO);
			});
		return tenant.getId();
	}

	private Long createUser(Long roleId, TenantCreateReqVO createReqVO) {
		// ????????????
		Long userId = userService.createUser(TenantConvert.INSTANCE
				.convert02(createReqVO));
		// ????????????
		permissionService.assignUserRole(userId, singleton(roleId));
		return userId;
	}

	private Long createRole(TenantPackageDO tenantPackage) {
		// ????????????
		RoleCreateReqVO reqVO = new RoleCreateReqVO();
		reqVO.setName(RoleCodeEnum.TENANT_ADMIN.getName());
		reqVO.setCode(RoleCodeEnum.TENANT_ADMIN.getCode());
		reqVO.setSort(0);
		reqVO.setRemark("??????????????????");
		Long roleId = roleService.createRole(reqVO,
				RoleTypeEnum.SYSTEM.getType());
		// ????????????
		permissionService.assignRoleMenu(roleId, tenantPackage.getMenuIds());
		return roleId;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateTenant(TenantUpdateReqVO updateReqVO) {
		// ????????????
		TenantDO tenant = checkUpdateTenant(updateReqVO.getId());
		// ?????????????????????
		TenantPackageDO tenantPackage = tenantPackageService
				.validTenantPackage(updateReqVO.getPackageId());

		// ????????????
		TenantDO updateObj = TenantConvert.INSTANCE.convert(updateReqVO);
		tenantMapper.updateById(updateObj);
		// ??????????????????????????????????????????????????????
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
					// ??????????????????
						List<RoleDO> roles = roleService.getRoles(null);
						roles.forEach(role -> Assert.isTrue(
								tenantId.equals(role.getTenantId()),
								"??????({}/{}) ???????????????", role.getId(),
								role.getTenantId(), tenantId)); // ????????????
						// ?????????????????????????????????
						roles.forEach(role -> {
							// ????????????????????????????????????????????????????????????????????????
							if (Objects.equals(role.getCode(),
									RoleCodeEnum.TENANT_ADMIN.getCode())) {
								permissionService.assignRoleMenu(role.getId(),
										menuIds);
								log.info(
										"[updateTenantRoleMenu][???????????????({}/{}) ??????????????????({})]",
										role.getId(), role.getTenantId(),
										menuIds);
								return;
							}
							// ??????????????????????????????????????????????????????
							Set<Long> roleMenuIds = permissionService
									.getRoleMenuIds(role.getId());
							roleMenuIds = CollUtil.intersectionDistinct(
									roleMenuIds, menuIds);
							permissionService.assignRoleMenu(role.getId(),
									roleMenuIds);
							log.info(
									"[updateTenantRoleMenu][??????({}/{}) ??????????????????({})]",
									role.getId(), role.getTenantId(),
									roleMenuIds);
						});
					});
	}

	@Override
	public void deleteTenant(Long id) {
		// ????????????
		checkUpdateTenant(id);
		// ??????
		tenantMapper.deleteById(id);
	}

	private TenantDO checkUpdateTenant(Long id) {
		TenantDO tenant = tenantMapper.selectById(id);
		if (tenant == null) {
			throw ServiceExceptionUtil
					.exception(ErrorCodeConstants.TENANT_NOT_EXISTS);
		}
		// ??????????????????????????????
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
		// ?????????????????????????????????
		if (isTenantDisable()) {
			return;
		}
		// ????????????
		TenantDO tenant = sysTenantService.getTenant(TenantContextHolder
				.getRequiredTenantId());
		// ???????????????
		handler.handle(tenant);
	}

	@Override
	public void handleTenantMenu(TenantMenuHandler handler) {
		// ?????????????????????????????????
		if (isTenantDisable()) {
			return;
		}
		// ?????????????????????????????????
		TenantDO tenant = sysTenantService.getTenant(TenantContextHolder
				.getRequiredTenantId());
		Set<Long> menuIds;
		if (isSystemTenant(tenant)) { // ?????????????????????????????????
			menuIds = CollectionUtils.convertSet(menuService.getMenus(),
					MenuDO::getId);
		} else {
			menuIds = tenantPackageService.getTenantPackage(
					tenant.getPackageId()).getMenuIds();
		}
		// ???????????????
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
