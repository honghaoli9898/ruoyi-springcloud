package com.sdps.module.bpm.controller.admin.definition.vo.form;

import com.sdps.common.pojo.PageParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ApiModel("管理后台 - 动态表单分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class BpmFormPageReqVO extends PageParam {

	private static final long serialVersionUID = -7242534925512852066L;
	@ApiModelProperty(value = "表单名称", example = "芋道")
    private String name;

}
