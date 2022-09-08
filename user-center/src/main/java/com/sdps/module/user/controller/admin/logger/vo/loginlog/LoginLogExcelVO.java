package com.sdps.module.user.controller.admin.logger.vo.loginlog;

import java.util.Date;

import lombok.Data;

import com.alibaba.excel.annotation.ExcelProperty;

/**
 * 登录日志 Excel 导出响应 VO
 */
@Data
public class LoginLogExcelVO {

    @ExcelProperty("日志主键")
    private Long id;

    @ExcelProperty("用户账号")
    private String username;

    @ExcelProperty(value = "日志类型")
    private Integer logType;

    @ExcelProperty(value = "登录结果")
    private Integer result;

    @ExcelProperty("登录 IP")
    private String userIp;

    @ExcelProperty("浏览器 UA")
    private String userAgent;

    @ExcelProperty("登录时间")
    private Date createTime;

}
