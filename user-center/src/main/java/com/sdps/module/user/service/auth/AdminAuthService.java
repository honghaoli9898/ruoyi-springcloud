package com.sdps.module.user.service.auth;

import com.sdps.common.model.dataobject.user.AdminUserDO;
import com.sdps.common.model.user.LoginAppUser;
import com.sdps.common.model.user.LoginUser;
import com.sdps.module.user.controller.admin.auth.vo.AuthSmsSendReqVO;


/**
 * 管理后台的认证 Service 接口
 *
 * 提供用户的登录、登出的能力
 *
 * @author 芋道源码
 */
public interface AdminAuthService {

      /**
     * 短信验证码发送
     *
     * @param reqVO 发送请求
     */
    void sendSmsCode(AuthSmsSendReqVO reqVO);

    AdminUserDO smsLogin(String mobile, String code);
}
