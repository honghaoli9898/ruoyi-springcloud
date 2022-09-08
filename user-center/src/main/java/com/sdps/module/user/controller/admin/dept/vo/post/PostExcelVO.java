package com.sdps.module.user.controller.admin.dept.vo.post;

import com.sdps.common.core.annotations.DictFormat;
import com.sdps.common.dict.core.convert.DictConvert;
import com.sdps.module.system.enums.DictTypeConstants;
import lombok.Data;

import com.alibaba.excel.annotation.ExcelProperty;

/**
 * 岗位 Excel 导出响应 VO
 */
@Data
public class PostExcelVO {

	@ExcelProperty("岗位序号")
	private Long id;

	@ExcelProperty("岗位编码")
	private String code;

	@ExcelProperty("岗位名称")
	private String name;

	@ExcelProperty("岗位排序")
	private Integer sort;

	@ExcelProperty(value = "状态", converter = DictConvert.class)
	@DictFormat(DictTypeConstants.COMMON_STATUS)
	private String status;

}
