package com.sdps.module.user.controller.admin.tenant.vo.packages;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import org.springframework.format.annotation.DateTimeFormat;

import com.sdps.common.pojo.PageParam;
import com.sdps.common.util.date.DateUtils;

@ApiModel("管理后台 - 租户套餐分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class TenantPackagePageReqVO extends PageParam {

	private static final long serialVersionUID = 8890200434604381190L;

	@ApiModelProperty(value = "套餐名", example = "VIP")
	private String name;

	@ApiModelProperty(value = "状态", example = "1")
	private Integer status;

	@ApiModelProperty(value = "备注", example = "好")
	private String remark;

	@DateTimeFormat(pattern = DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
	@ApiModelProperty(value = "创建时间")
	private Date[] createTime;
}
