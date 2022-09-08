package com.sdps.module.user.controller.admin.permission.vo.menu;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;

@ApiModel("管理后台 - 菜单更新 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class MenuUpdateReqVO extends MenuBaseVO {

    @ApiModelProperty(value = "菜单编号", required = true, example = "1024")
    @NotNull(message = "菜单编号不能为空")
    private Long id;

}
