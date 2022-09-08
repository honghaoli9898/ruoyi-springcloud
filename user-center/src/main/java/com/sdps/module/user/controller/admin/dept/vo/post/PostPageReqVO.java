package com.sdps.module.user.controller.admin.dept.vo.post;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.sdps.common.pojo.PageParam;

@ApiModel("管理后台 - 岗位分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class PostPageReqVO extends PageParam {

	private static final long serialVersionUID = -3339971765053207317L;

	@ApiModelProperty(value = "岗位编码", example = "yudao", notes = "模糊匹配")
	private String code;

	@ApiModelProperty(value = "岗位名称", example = "芋道", notes = "模糊匹配")
	private String name;

	@ApiModelProperty(value = "展示状态", example = "1", notes = "参见 CommonStatusEnum 枚举类")
	private Integer status;

}
