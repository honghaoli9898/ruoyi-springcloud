package com.sdps.module.user.dal.mapper.tenant;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.sdps.common.mybatis.core.mapper.BaseMapperX;
import com.sdps.common.mybatis.core.query.LambdaQueryWrapperX;
import com.sdps.common.pojo.PageResult;
import com.sdps.module.system.dal.dataobject.tenant.TenantDO;
import com.sdps.module.user.controller.admin.tenant.vo.tenant.TenantExportReqVO;
import com.sdps.module.user.controller.admin.tenant.vo.tenant.TenantPageReqVO;

/**
 * 租户 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface TenantMapper extends BaseMapperX<TenantDO> {

	default PageResult<TenantDO> selectPage(TenantPageReqVO reqVO) {
		return selectPage(
				reqVO,
				new LambdaQueryWrapperX<TenantDO>()
						.likeIfPresent(TenantDO::getName, reqVO.getName())
						.likeIfPresent(TenantDO::getContactName,
								reqVO.getContactName())
						.likeIfPresent(TenantDO::getContactMobile,
								reqVO.getContactMobile())
						.eqIfPresent(TenantDO::getStatus, reqVO.getStatus())
						.betweenIfPresent(TenantDO::getCreateTime,
								reqVO.getCreateTime())
						.orderByDesc(TenantDO::getId));
	}

	default List<TenantDO> selectList(TenantExportReqVO reqVO) {
		return selectList(new LambdaQueryWrapperX<TenantDO>()
				.likeIfPresent(TenantDO::getName, reqVO.getName())
				.likeIfPresent(TenantDO::getContactName, reqVO.getContactName())
				.likeIfPresent(TenantDO::getContactMobile,
						reqVO.getContactMobile())
				.eqIfPresent(TenantDO::getStatus, reqVO.getStatus())
				.betweenIfPresent(TenantDO::getCreateTime,
						reqVO.getCreateTime()).orderByDesc(TenantDO::getId));
	}

	default TenantDO selectByName(String name) {
		return selectOne(TenantDO::getName, name);
	}

	default Long selectCountByPackageId(Long packageId) {
		return selectCount(TenantDO::getPackageId, packageId);
	}

	default List<TenantDO> selectListByPackageId(Long packageId) {
		return selectList(TenantDO::getPackageId, packageId);
	}

	@Select("SELECT COUNT(*) FROM system_tenant WHERE update_time > #{maxUpdateTime}")
	Long selectCountByUpdateTimeGt(Date maxUpdateTime);

}
