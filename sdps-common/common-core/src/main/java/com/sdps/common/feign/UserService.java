package com.sdps.common.feign;

import javax.validation.Valid;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.sdps.common.config.MultipartSupportConfig;
import com.sdps.common.constant.ServiceNameConstants;
import com.sdps.common.feign.fallback.UserServiceFallbackFactory;
import com.sdps.common.model.dataobject.user.AdminUserDO;
import com.sdps.common.model.dto.SmsSendSingleToUserReqDTO;
import com.sdps.common.model.user.LoginAppUser;
import com.sdps.common.pojo.CommonResult;

@FeignClient(name = ServiceNameConstants.USER_SERVICE, fallbackFactory = UserServiceFallbackFactory.class, decode404 = true, configuration = MultipartSupportConfig.class)
public interface UserService {
	/**
	 * feign rpc访问远程/users/{username}接口 查询用户实体对象SysUser
	 *
	 * @param username
	 * @return
	 */
	@GetMapping(value = "/admin-api/system/user/name/{username}")
	AdminUserDO selectByUsername(@PathVariable("username") String username);

	/**
	 * feign rpc访问远程接口
	 *
	 * @param username
	 * @return
	 */
	@GetMapping(value = "/admin-api/system/user/login", params = "username")
	LoginAppUser findByUsername(@RequestParam("username") String username);

	/**
	 * 通过手机号查询用户、角色信息
	 *
	 * @param mobile
	 *            手机号
	 */
	@GetMapping(value = "/admin-api/system/user/mobile", params = "mobile")
	AdminUserDO findByMobile(@RequestParam("mobile") String mobile);

	/**
	 * 根据OpenId查询用户信息
	 *
	 * @param openId
	 *            openId
	 */
	@GetMapping(value = "/admin-api/system/user/openId", params = "openId")
	LoginAppUser findByOpenId(@RequestParam("openId") String openId);

	/**
	 * 通过用户id获取用户信息
	 *
	 * @param userId
	 * @return
	 */
	@GetMapping(value = "/admin-api/system/user/name", params = "userId")
	AdminUserDO selectByUserId(@RequestParam("userId") String userId);

	@GetMapping(value = "/admin-api/system/auth/sms-login")
	CommonResult smsLogin(@RequestParam("mobile") String mobile,
			@RequestParam("code") String code);

	@PostMapping("/admin-api/system/sms-template/wf-send-sms")
	public CommonResult<Long> sendSms(
			@Valid @RequestBody SmsSendSingleToUserReqDTO smsSendSingleToUserReqDTO);
}
