package com.sdps.module.user.controller.admin.errorcode.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import org.springframework.format.annotation.DateTimeFormat;

import com.sdps.common.pojo.PageParam;
import com.sdps.common.util.date.DateUtils;

@ApiModel("管理后台 - 错误码分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ErrorCodePageReqVO extends PageParam {

	private static final long serialVersionUID = -328259251458281848L;

	@ApiModelProperty(value = "错误码类型", example = "1", notes = "参见 ErrorCodeTypeEnum 枚举类")
	private Integer type;

	@ApiModelProperty(value = "应用名", example = "dashboard")
	private String applicationName;

	@ApiModelProperty(value = "错误码编码", example = "1234")
	private Integer code;

	@ApiModelProperty(value = "错误码错误提示", example = "帅气")
	private String message;

	@DateTimeFormat(pattern = DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
	@ApiModelProperty(value = "创建时间")
	private Date[] createTime;

}
