package com.sdps.module.user.dal.mapper.tenant;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.sdps.common.mybatis.core.mapper.BaseMapperX;
import com.sdps.common.mybatis.core.query.LambdaQueryWrapperX;
import com.sdps.common.pojo.PageResult;
import com.sdps.module.user.controller.admin.tenant.vo.packages.TenantPackagePageReqVO;
import com.sdps.module.user.dal.dataobject.tenant.TenantPackageDO;

/**
 * 租户套餐 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface TenantPackageMapper extends BaseMapperX<TenantPackageDO> {

	default PageResult<TenantPackageDO> selectPage(TenantPackagePageReqVO reqVO) {
		return selectPage(
				reqVO,
				new LambdaQueryWrapperX<TenantPackageDO>()
						.likeIfPresent(TenantPackageDO::getName,
								reqVO.getName())
						.eqIfPresent(TenantPackageDO::getStatus,
								reqVO.getStatus())
						.likeIfPresent(TenantPackageDO::getRemark,
								reqVO.getRemark())
						.betweenIfPresent(TenantPackageDO::getCreateTime,
								reqVO.getCreateTime())
						.orderByDesc(TenantPackageDO::getId));
	}

	default List<TenantPackageDO> selectListByStatus(Integer status) {
		return selectList(TenantPackageDO::getStatus, status);
	}
}
