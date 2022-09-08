package com.sdps.module.user.controller.admin.tenant.vo.tenant;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import org.springframework.format.annotation.DateTimeFormat;

import com.sdps.common.pojo.PageParam;
import com.sdps.common.util.date.DateUtils;

@ApiModel("管理后台 - 租户分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class TenantPageReqVO extends PageParam {

	private static final long serialVersionUID = -1206108706052101414L;

	@ApiModelProperty(value = "租户名", example = "芋道")
	private String name;

	@ApiModelProperty(value = "联系人", example = "芋艿")
	private String contactName;

	@ApiModelProperty(value = "联系手机", example = "15601691300")
	private String contactMobile;

	@ApiModelProperty(value = "租户状态（0正常 1停用）", example = "1")
	private Integer status;

	@DateTimeFormat(pattern = DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
	@ApiModelProperty(value = "创建时间")
	private Date[] createTime;

}
