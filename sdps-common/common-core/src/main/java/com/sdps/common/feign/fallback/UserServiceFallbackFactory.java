package com.sdps.common.feign.fallback;

import javax.validation.Valid;

import lombok.extern.slf4j.Slf4j;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.web.bind.annotation.RequestParam;

import com.sdps.common.feign.UserService;
import com.sdps.common.model.dataobject.user.AdminUserDO;
import com.sdps.common.model.dto.SmsSendSingleToUserReqDTO;
import com.sdps.common.model.user.LoginAppUser;
import com.sdps.common.pojo.CommonResult;

/**
 * userService降级工场
 *
 */
@Slf4j
public class UserServiceFallbackFactory implements FallbackFactory<UserService> {
	@Override
	public UserService create(Throwable throwable) {
		return new UserService() {
			@Override
			public AdminUserDO selectByUsername(String username) {
				return new AdminUserDO();
			}

			@Override
			public LoginAppUser findByUsername(String username) {
				return new LoginAppUser();
			}

			@Override
			public LoginAppUser findByMobile(String mobile) {
				return new LoginAppUser();
			}

			@Override
			public LoginAppUser findByOpenId(String openId) {
				return new LoginAppUser();
			}

			@Override
			public AdminUserDO selectByUserId(@RequestParam String userId) {
				return new LoginAppUser();
			}

			@Override
			public CommonResult smsLogin(String mobile, String code) {
				return CommonResult.error(999,mobile);
			}

			@Override
			public CommonResult<Long> sendSms(
					@Valid SmsSendSingleToUserReqDTO sendReqVO) {
				return CommonResult.error(999,sendReqVO.getTemplateCode());
			}
		};
	}
}
