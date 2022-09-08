package com.sdps.module.dynamicroute.controller;

import java.util.Objects;

import com.sdps.module.dynamicroute.service.IDynamicVersionService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sdps.common.exception.util.ServiceExceptionUtil;
import com.sdps.common.pojo.CommonResult;
import com.sdps.module.dynamicroute.config.RedisConfig;
import com.sdps.module.dynamicroute.entity.DynamicVersion;
import com.sdps.module.dynamicroute.errorcode.ErrorCodeConstants;

@Slf4j
@RestController
@RequestMapping("/version")
@SuppressWarnings("rawtypes")
public class DynamicVersionController {

	@Autowired
	private IDynamicVersionService dynamicVersionService;
	@Autowired
	private StringRedisTemplate redisTemplate;

	
	@RequestMapping(value = "/add")
	public CommonResult add() {
		try {
			DynamicVersion version = new DynamicVersion();
			dynamicVersionService.add(version);
			return CommonResult.success("操作成功");
		} catch (Exception e) {
			log.error("发布路由版本失败", e);
			throw ServiceExceptionUtil
					.exception(ErrorCodeConstants.RELEASE_ROUTE_ERROR);
		}
	}

	// 获取最后一次发布的版本号
	@RequestMapping(value = "/lastVersion", method = RequestMethod.GET)
	public Long getLastVersion() {
		Long versionId = 0L;
		String result = redisTemplate.opsForValue().get(RedisConfig.versionKey);
		if (Objects.nonNull(result) || !StringUtils.isEmpty(result)) {
			log.info("返回 redis 中的版本信息......");
			versionId = Long.valueOf(result);
		} else {
			log.info("返回 mysql 中的版本信息......");
			versionId = dynamicVersionService.getLastVersion();
			redisTemplate.opsForValue().set(RedisConfig.versionKey,
					String.valueOf(versionId));
		}
		return versionId;
	}

	// 打开发布版本列表页面
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public CommonResult listAll(ModelMap map) {
		return CommonResult.success(dynamicVersionService.listAll());
	}
}
