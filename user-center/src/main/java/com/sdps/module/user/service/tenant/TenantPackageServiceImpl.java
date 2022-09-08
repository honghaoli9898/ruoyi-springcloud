package com.sdps.module.user.service.tenant;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import cn.hutool.core.collection.CollUtil;

import com.sdps.common.enums.CommonStatusEnum;
import com.sdps.common.exception.util.ServiceExceptionUtil;
import com.sdps.common.pojo.PageResult;
import com.sdps.module.system.dal.dataobject.tenant.TenantDO;
import com.sdps.module.system.enums.ErrorCodeConstants;
import com.sdps.module.user.controller.admin.tenant.vo.packages.TenantPackageCreateReqVO;
import com.sdps.module.user.controller.admin.tenant.vo.packages.TenantPackagePageReqVO;
import com.sdps.module.user.controller.admin.tenant.vo.packages.TenantPackageUpdateReqVO;
import com.sdps.module.user.convert.tenant.TenantPackageConvert;
import com.sdps.module.user.dal.dataobject.tenant.TenantPackageDO;
import com.sdps.module.user.dal.mapper.tenant.TenantPackageMapper;

/**
 * 租户套餐 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class TenantPackageServiceImpl implements TenantPackageService {

	@Resource
	private TenantPackageMapper tenantPackageMapper;

	@Autowired
	@Lazy
	// 避免循环依赖的报错
	private TenantService tenantService;

	@Override
	public Long createTenantPackage(TenantPackageCreateReqVO createReqVO) {
		// 插入
		TenantPackageDO tenantPackage = TenantPackageConvert.INSTANCE
				.convert(createReqVO);
		tenantPackageMapper.insert(tenantPackage);
		// 返回
		return tenantPackage.getId();
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateTenantPackage(TenantPackageUpdateReqVO updateReqVO) {
		// 校验存在
		TenantPackageDO tenantPackage = validateTenantPackageExists(updateReqVO
				.getId());
		// 更新
		TenantPackageDO updateObj = TenantPackageConvert.INSTANCE
				.convert(updateReqVO);
		tenantPackageMapper.updateById(updateObj);
		// 如果菜单发生变化，则修改每个租户的菜单
		if (!CollUtil.isEqualList(tenantPackage.getMenuIds(),
				updateReqVO.getMenuIds())) {
			List<TenantDO> tenants = tenantService
					.getTenantListByPackageId(tenantPackage.getId());
			tenants.forEach(tenant -> tenantService.updateTenantRoleMenu(
					tenant.getId(), updateReqVO.getMenuIds()));
		}
	}

	@Override
	public void deleteTenantPackage(Long id) {
		// 校验存在
		this.validateTenantPackageExists(id);
		// 校验正在使用
		this.validateTenantUsed(id);
		// 删除
		tenantPackageMapper.deleteById(id);
	}

	private TenantPackageDO validateTenantPackageExists(Long id) {
		TenantPackageDO tenantPackage = tenantPackageMapper.selectById(id);
		if (tenantPackage == null) {
			throw ServiceExceptionUtil
					.exception(ErrorCodeConstants.TENANT_PACKAGE_NOT_EXISTS);
		}
		return tenantPackage;
	}

	private void validateTenantUsed(Long id) {
		if (tenantService.getTenantCountByPackageId(id) > 0) {
			throw ServiceExceptionUtil
					.exception(ErrorCodeConstants.TENANT_PACKAGE_USED);
		}
	}

	@Override
	public TenantPackageDO getTenantPackage(Long id) {
		return tenantPackageMapper.selectById(id);
	}

	@Override
	public PageResult<TenantPackageDO> getTenantPackagePage(
			TenantPackagePageReqVO pageReqVO) {
		return tenantPackageMapper.selectPage(pageReqVO);
	}

	@Override
	public TenantPackageDO validTenantPackage(Long id) {
		TenantPackageDO tenantPackage = tenantPackageMapper.selectById(id);
		if (tenantPackage == null) {
			throw ServiceExceptionUtil
					.exception(ErrorCodeConstants.TENANT_PACKAGE_NOT_EXISTS);
		}
		if (tenantPackage.getStatus().equals(
				CommonStatusEnum.DISABLE.getStatus())) {
			throw ServiceExceptionUtil.exception(
					ErrorCodeConstants.TENANT_PACKAGE_DISABLE,
					tenantPackage.getName());
		}
		return tenantPackage;
	}

	@Override
	public List<TenantPackageDO> getTenantPackageListByStatus(Integer status) {
		return tenantPackageMapper.selectListByStatus(status);
	}

}
