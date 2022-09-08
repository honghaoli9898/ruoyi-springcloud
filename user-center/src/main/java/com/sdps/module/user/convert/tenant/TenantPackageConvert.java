package com.sdps.module.user.convert.tenant;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.sdps.common.pojo.PageResult;
import com.sdps.module.user.controller.admin.tenant.vo.packages.TenantPackageCreateReqVO;
import com.sdps.module.user.controller.admin.tenant.vo.packages.TenantPackageRespVO;
import com.sdps.module.user.controller.admin.tenant.vo.packages.TenantPackageSimpleRespVO;
import com.sdps.module.user.controller.admin.tenant.vo.packages.TenantPackageUpdateReqVO;
import com.sdps.module.user.dal.dataobject.tenant.TenantPackageDO;

/**
 * 租户套餐 Convert
 *
 * @author 芋道源码
 */
@Mapper
public interface TenantPackageConvert {

    TenantPackageConvert INSTANCE = Mappers.getMapper(TenantPackageConvert.class);

    TenantPackageDO convert(TenantPackageCreateReqVO bean);

    TenantPackageDO convert(TenantPackageUpdateReqVO bean);

    TenantPackageRespVO convert(TenantPackageDO bean);

    List<TenantPackageRespVO> convertList(List<TenantPackageDO> list);

    PageResult<TenantPackageRespVO> convertPage(PageResult<TenantPackageDO> page);

    List<TenantPackageSimpleRespVO> convertList02(List<TenantPackageDO> list);

}
