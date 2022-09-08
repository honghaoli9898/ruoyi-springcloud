package com.sdps.module.user.convert.tenant;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.sdps.common.pojo.PageResult;
import com.sdps.module.system.dal.dataobject.tenant.TenantDO;
import com.sdps.module.user.controller.admin.tenant.vo.tenant.TenantCreateReqVO;
import com.sdps.module.user.controller.admin.tenant.vo.tenant.TenantExcelVO;
import com.sdps.module.user.controller.admin.tenant.vo.tenant.TenantRespVO;
import com.sdps.module.user.controller.admin.tenant.vo.tenant.TenantUpdateReqVO;
import com.sdps.module.user.controller.admin.user.vo.user.UserCreateReqVO;

/**
 * 租户 Convert
 *
 * @author 芋道源码
 */
@Mapper
public interface TenantConvert {

	TenantConvert INSTANCE = Mappers.getMapper(TenantConvert.class);

	TenantDO convert(TenantCreateReqVO bean);

	TenantDO convert(TenantUpdateReqVO bean);

	TenantRespVO convert(TenantDO bean);

	List<TenantRespVO> convertList(List<TenantDO> list);

	PageResult<TenantRespVO> convertPage(PageResult<TenantDO> page);

	List<TenantExcelVO> convertList02(List<TenantDO> list);

	default UserCreateReqVO convert02(TenantCreateReqVO bean) {
		UserCreateReqVO reqVO = new UserCreateReqVO();
		reqVO.setUsername(bean.getUsername());
		reqVO.setPassword(bean.getPassword());
		reqVO.setNickname(bean.getContactName());
		reqVO.setMobile(bean.getContactMobile());
		return reqVO;
	}

}
