package com.sdps.module.system.dal.mapper.tenant;

import java.util.Date;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.sdps.common.mybatis.core.mapper.BaseMapperX;
import com.sdps.module.system.dal.dataobject.tenant.TenantDO;

/**
 * 租户 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface SysTenantMapper extends BaseMapperX<TenantDO> {

	@Select("SELECT COUNT(*) FROM system_tenant WHERE update_time > #{maxUpdateTime}")
	Long selectCountByUpdateTimeGt(Date maxUpdateTime);

}
