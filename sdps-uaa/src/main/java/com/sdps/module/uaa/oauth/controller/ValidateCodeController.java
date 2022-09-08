package com.sdps.module.uaa.oauth.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.sdps.common.captcha.model.common.ResponseModel;
import com.sdps.common.captcha.model.vo.CaptchaVO;
import com.sdps.common.captcha.service.CaptchaService;
import com.sdps.common.captcha.util.StringUtils;
import com.sdps.common.constant.SecurityConstants;
import com.sdps.common.pojo.CommonResult;
import com.sdps.module.uaa.oauth.service.IValidateCodeService;
import com.wf.captcha.ArithmeticCaptcha;
import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.utils.CaptchaUtil;

/**
 * 验证码提供
 * 
 * @author zlt
 * @date 2018/12/18
 */
@RestController
public class ValidateCodeController {
	@Autowired
	private IValidateCodeService validateCodeService;

	/**
	 * 创建验证码
	 *
	 * @throws Exception
	 */
	@GetMapping(SecurityConstants.DEFAULT_VALIDATE_CODE_URL_PREFIX
			+ "/{deviceId}")
	public void createCode(@PathVariable String deviceId,
			HttpServletResponse response) throws Exception {
		Assert.notNull(deviceId, "机器码不能为空");
		// 设置请求头为输出图片类型
		CaptchaUtil.setHeader(response);
		ArithmeticCaptcha captcha = new ArithmeticCaptcha(100, 35);
		validateCodeService.saveImageCode(deviceId, captcha.text()
				.toLowerCase());
		// 输出图片流
		captcha.out(response.getOutputStream());
	}

	/**
	 * 创建验证码
	 *
	 * @throws Exception
	 */
	@GetMapping(SecurityConstants.RESET_PASSWORD_VALIDATE_CODE_URL_PREFIX
			+ "/{deviceId}")
	public void createResetPasswordCode(@PathVariable String deviceId,
			HttpServletResponse response) throws Exception {
		Assert.notNull(deviceId, "机器码不能为空");
		// 设置请求头为输出图片类型
		CaptchaUtil.setHeader(response);
		SpecCaptcha captcha = new SpecCaptcha(100, 35);
		captcha.setLen(4);
		validateCodeService.saveImageCode(deviceId, captcha.text()
				.toLowerCase());
		// 输出图片流
		captcha.out(response.getOutputStream());
	}

	/**
	 * 发送手机验证码 后期要加接口限制
	 *
	 * @param mobile
	 *            手机号
	 * @return R
	 */
	@SuppressWarnings("rawtypes")
	@ResponseBody
	@GetMapping(SecurityConstants.MOBILE_VALIDATE_CODE_URL_PREFIX + "/{mobile}")
	public CommonResult createCode(@PathVariable String mobile) {
		Assert.notNull(mobile, "手机号不能为空");
		return validateCodeService.sendSmsCode(mobile);
	}

	@SuppressWarnings("rawtypes")
	@ResponseBody
	@GetMapping("/check/code")
	public CommonResult checkCode(@RequestParam("deviceId") String deviceId,
			@RequestParam("validCode") String validCode) {
		Assert.notNull(validCode, "验证码不能为空");
		Assert.notNull(deviceId, "机器码不能为空");
		validateCodeService.validate(deviceId, validCode);
		return CommonResult.success("操作成功");
	}

	@Autowired
	private CaptchaService captchaService;

	@PostMapping(SecurityConstants.CAPTCHE_VALIDATE_CODE_URL_PREFIX + "/get")
	public ResponseModel get(@RequestBody CaptchaVO data) {
		ServletRequestAttributes requestAttributes = ServletRequestAttributes.class
				.cast(RequestContextHolder.getRequestAttributes());
		HttpServletRequest request = requestAttributes.getRequest();
		assert request.getRemoteHost() != null;
		data.setBrowserInfo(getRemoteId(request));
		return captchaService.get(data);
	}

	@PostMapping(SecurityConstants.CAPTCHE_VALIDATE_CODE_URL_PREFIX + "/check")
	public ResponseModel check(@RequestBody CaptchaVO data) {
		ServletRequestAttributes requestAttributes = ServletRequestAttributes.class
				.cast(RequestContextHolder.getRequestAttributes());
		HttpServletRequest request = requestAttributes.getRequest();
		data.setBrowserInfo(getRemoteId(request));
		return captchaService.check(data);
	}

	// @PostMapping("/verify")
	public ResponseModel verify(@RequestBody CaptchaVO data) {
		return captchaService.verification(data);
	}

	public static final String getRemoteId(HttpServletRequest request) {
		String xfwd = request.getHeader("X-Forwarded-For");
		String ip = getRemoteIpFromXfwd(xfwd);
		String ua = request.getHeader("user-agent");
		if (StringUtils.isNotBlank(ip)) {
			return ip + ua;
		}
		return request.getRemoteAddr() + ua;
	}

	private static String getRemoteIpFromXfwd(String xfwd) {
		if (StringUtils.isNotBlank(xfwd)) {
			String[] ipList = xfwd.split(",");
			return StringUtils.trim(ipList[0]);
		}
		return null;
	}

}
