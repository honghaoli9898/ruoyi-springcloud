package com.sdps.module.system.api.tenant;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.sdps.module.system.api.tenant.TenantApi;
import com.sdps.module.system.service.tenant.SysTenantService;

@Service
public class TenantApiImpl implements TenantApi {

    @Resource
    private SysTenantService tenantService;

    @Override
    public List<Long> getTenantIds() {
        return tenantService.getTenantIds();
    }

    @Override
    public void validTenant(Long id) {
        tenantService.validTenant(id);
    }

}
