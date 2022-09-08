package com.sdps.module.user.controller.admin.tenant.vo.packages;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ApiModel("管理后台 - 租户套餐更新 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class TenantPackageUpdateReqVO extends TenantPackageBaseVO {

	@ApiModelProperty(value = "套餐编号", required = true, example = "1024")
	@NotNull(message = "套餐编号不能为空")
	private Long id;

}
