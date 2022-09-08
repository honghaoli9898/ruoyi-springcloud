package com.sdps.module.dynamicroute.errorcode;

import com.sdps.common.exception.ErrorCode;

/**
 * System 错误码枚举类
 *
 * system 系统，使用 1-002-000-000 段
 */
public interface ErrorCodeConstants {

	ErrorCode RELEASE_ROUTE_ERROR = new ErrorCode(1009022000, "发布路由失败");
	ErrorCode GET_ROUTE_ERROR = new ErrorCode(1009022001, "获取路由失败");
	ErrorCode ADD_ROUTE_ERROR = new ErrorCode(1009022002, "添加路由失败");
	ErrorCode UPDATE_ROUTE_ERROR = new ErrorCode(1009022003, "更新路由失败");
	ErrorCode NO_INTERFACE_AUTH_ERROR = new ErrorCode(1009022004, "您没有该接口权限");
}
