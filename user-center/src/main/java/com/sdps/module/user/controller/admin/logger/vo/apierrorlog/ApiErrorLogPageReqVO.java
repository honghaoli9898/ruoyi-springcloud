package com.sdps.module.user.controller.admin.logger.vo.apierrorlog;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import org.springframework.format.annotation.DateTimeFormat;

import com.sdps.common.pojo.PageParam;
import com.sdps.common.util.date.DateUtils;

@ApiModel("管理后台 - API 错误日志分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ApiErrorLogPageReqVO extends PageParam {

	private static final long serialVersionUID = -8492566343925613967L;

	@ApiModelProperty(value = "用户编号", example = "666")
	private Long userId;

	@ApiModelProperty(value = "用户类型", example = "1")
	private Integer userType;

	@ApiModelProperty(value = "应用名", example = "dashboard")
	private String applicationName;

	@ApiModelProperty(value = "请求地址", example = "/xx/yy")
	private String requestUrl;

	@DateTimeFormat(pattern = DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
	@ApiModelProperty(value = "异常发生时间")
	private Date[] exceptionTime;

	@ApiModelProperty(value = "处理状态", example = "0")
	private Integer processStatus;

}
