package com.sdps.module.system.service.tenant;

import java.util.List;

import com.sdps.module.system.dal.dataobject.tenant.TenantDO;

/**
 * 租户 Service 接口
 *
 * @author 芋道源码
 */
public interface SysTenantService {
	
    /**
     * 获得租户
     *
     * @param id 编号
     * @return 租户
     */
    TenantDO getTenant(Long id);

    /**
     * 获得所有租户
     *
     * @return 租户编号数组
     */
    List<Long> getTenantIds();

    /**
     * 校验租户是否合法
     *
     * @param id 租户编号
     */
    void validTenant(Long id);
}
