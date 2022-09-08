package com.sdps.module.user.controller.admin.user.vo.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;

import com.sdps.common.pojo.PageParam;
import com.sdps.common.util.date.DateUtils;

@ApiModel("管理后台 - 用户分页 Request VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserPageReqVO extends PageParam {

	private static final long serialVersionUID = 3102651103058128842L;

	@ApiModelProperty(value = "用户账号", example = "yudao", notes = "模糊匹配")
	private String username;

	@ApiModelProperty(value = "手机号码", example = "yudao", notes = "模糊匹配")
	private String mobile;

	@ApiModelProperty(value = "展示状态", example = "1", notes = "参见 CommonStatusEnum 枚举类")
	private Integer status;

	@ApiModelProperty(value = "创建时间", example = "[2022-07-01 00:00:00,2022-07-01 23:59:59]")
	@DateTimeFormat(pattern = DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
	private Date[] createTime;

	@ApiModelProperty(value = "部门编号", example = "1024", notes = "同时筛选子部门")
	private Long deptId;

}
