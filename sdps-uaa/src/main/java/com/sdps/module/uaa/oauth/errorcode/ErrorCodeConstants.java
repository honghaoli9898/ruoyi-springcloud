package com.sdps.module.uaa.oauth.errorcode;

import com.sdps.common.exception.ErrorCode;

/**
 * System 错误码枚举类
 *
 * system 系统，使用 1-002-000-000 段
 */
public interface ErrorCodeConstants {
    // ========== OAuth2 授权 1002022000 =========
    ErrorCode OAUTH2_CODE_NOT_EXISTS = new ErrorCode(1002022000, "code 不存在");
    ErrorCode OAUTH2_CODE_EXPIRE = new ErrorCode(1002022000, "code 已过期");

}
