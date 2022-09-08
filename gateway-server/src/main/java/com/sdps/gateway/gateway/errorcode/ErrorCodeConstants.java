package com.sdps.gateway.gateway.errorcode;

import com.sdps.common.exception.ErrorCode;

/**
 * System 错误码枚举类
 *
 * system 系统，使用 1-002-000-000 段
 */
public interface ErrorCodeConstants {

    ErrorCode DECRYPT_PASSWORD_ERROR = new ErrorCode(1008022000, "解析密码失败");

}
