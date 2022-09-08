package com.sdps.module.user.controller.admin.errorcode.vo;

import java.util.Date;

import com.sdps.common.core.annotations.DictFormat;
import com.sdps.common.dict.core.convert.DictConvert;
import lombok.Data;

import com.alibaba.excel.annotation.ExcelProperty;

/**
 * 错误码 Excel VO
 *
 * @author 芋道源码
 */
@Data
public class ErrorCodeExcelVO {

	@ExcelProperty("错误码编号")
	private Long id;

	@ExcelProperty(value = "错误码类型", converter = DictConvert.class)
	@DictFormat("inf_error_code_type")
	private Integer type;

	@ExcelProperty("应用名")
	private String applicationName;

	@ExcelProperty("错误码编码")
	private Integer code;

	@ExcelProperty("错误码错误提示")
	private String message;

	@ExcelProperty("备注")
	private String memo;

	@ExcelProperty("创建时间")
	private Date createTime;

}
