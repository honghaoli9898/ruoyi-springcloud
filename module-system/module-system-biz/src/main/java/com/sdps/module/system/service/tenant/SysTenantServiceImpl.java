package com.sdps.module.system.service.tenant;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.sdps.common.enums.CommonStatusEnum;
import com.sdps.common.exception.util.ServiceExceptionUtil;
import com.sdps.common.util.collection.CollectionUtils;
import com.sdps.common.util.date.DateUtils;
import com.sdps.module.system.dal.dataobject.tenant.TenantDO;
import com.sdps.module.system.dal.mapper.tenant.SysTenantMapper;
import com.sdps.module.system.enums.ErrorCodeConstants;

/**
 * 租户 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class SysTenantServiceImpl implements SysTenantService {

	@Autowired
	private SysTenantMapper tenantMapper;

	@Override
	public TenantDO getTenant(Long id) {
		return tenantMapper.selectById(id);
	}

	@Override
	public List<Long> getTenantIds() {
		List<TenantDO> tenants = tenantMapper.selectList();
		return CollectionUtils.convertList(tenants, TenantDO::getId);
	}

	@Override
	public void validTenant(Long id) {
		TenantDO tenant = getTenant(id);
		if (tenant == null) {
			throw ServiceExceptionUtil
					.exception(ErrorCodeConstants.TENANT_NOT_EXISTS);
		}
		if (tenant.getStatus().equals(CommonStatusEnum.DISABLE.getStatus())) {
			throw ServiceExceptionUtil.exception(
					ErrorCodeConstants.TENANT_DISABLE, tenant.getName());
		}
		if (DateUtils.isExpired(tenant.getExpireTime())) {
			throw ServiceExceptionUtil.exception(
					ErrorCodeConstants.TENANT_EXPIRE, tenant.getName());
		}
	}

}
