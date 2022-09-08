package com.sdps.module.dynamicroute.controller;

import com.sdps.module.dynamicroute.entity.GatewayRoutes;
import com.sdps.module.dynamicroute.service.IRoutesService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;

import com.alibaba.fastjson.JSON;
import com.sdps.common.annotation.LoginUser;
import com.sdps.common.constant.CommonConstant;
import com.sdps.common.exception.util.ServiceExceptionUtil;
import com.sdps.common.model.dataobject.user.AdminUserDO;
import com.sdps.common.pojo.CommonResult;
import com.sdps.module.dynamicroute.config.RedisConfig;
import com.sdps.module.dynamicroute.errorcode.ErrorCodeConstants;

@Slf4j
@RestController
@RequestMapping("/gateway-routes")
@SuppressWarnings("rawtypes")
public class GatewayRoutesController {

	@Autowired
	private IRoutesService routesService;
	@Autowired
	private StringRedisTemplate redisTemplate;

	/**
	 * 获取所有动态路由信息
	 * 
	 * @return
	 */
	@RequestMapping("/routes")
	public CommonResult<String> getRouteDefinitions() {
		try {
			String result = redisTemplate.opsForValue().get(
					RedisConfig.routeKey);
			if (!StringUtils.isEmpty(result)) {
				log.info("返回 redis 中的路由信息......");
			} else {
				log.info("返回 mysql 中的路由信息......");
				result = JSON.toJSONString(routesService.getRouteDefinitions());
				// 再set到redis
				redisTemplate.opsForValue().set(RedisConfig.routeKey, result);
			}
			log.info("路由信息：" + result);
			return CommonResult.success(result);
		} catch (Exception e) {
			log.error("获取路由信息失败", e);
			throw ServiceExceptionUtil
					.exception(ErrorCodeConstants.GET_ROUTE_ERROR);
		}
	}

	// 添加路由信息
	@PostMapping(value = "/add")
	public CommonResult add(@LoginUser AdminUserDO sysUser,
			@RequestBody GatewayRoutes route) {
		try {
			if (!StrUtil.equalsIgnoreCase(sysUser.getUsername(),
					CommonConstant.ADMIN_USER_NAME)) {
				CommonResult.error(ErrorCodeConstants.NO_INTERFACE_AUTH_ERROR);
			}
			return routesService.add(route) > 0 ? CommonResult.success("操作成功")
					: CommonResult.error(1, "操作失败");
		} catch (Exception e) {
			log.error("添加路由失败", e);
			throw ServiceExceptionUtil
					.exception(ErrorCodeConstants.ADD_ROUTE_ERROR);
		}

	}

	// 添加路由信息
	@PostMapping(value = "/edit")
	public CommonResult edit(@LoginUser AdminUserDO sysUser,
			@RequestBody GatewayRoutes route) {
		try {
			if (!StrUtil.equalsIgnoreCase(sysUser.getUsername(),
					CommonConstant.ADMIN_USER_NAME)) {
				return CommonResult
						.error(ErrorCodeConstants.NO_INTERFACE_AUTH_ERROR);
			}
			return routesService.update(route) > 0 ? CommonResult
					.success("操作成功") : CommonResult.error(1, "操作失败");
		} catch (Exception e) {
			log.error("更新路由失败", e);
			throw ServiceExceptionUtil
					.exception(ErrorCodeConstants.UPDATE_ROUTE_ERROR);
		}
	}

	// 打开路由列表
	@GetMapping("/list")
	public CommonResult list(@LoginUser AdminUserDO sysUser) {
		if (StrUtil.equalsIgnoreCase(sysUser.getUsername(),
				CommonConstant.ADMIN_USER_NAME)) {
			GatewayRoutes route = new GatewayRoutes();
			route.setIsDel(false);
			return CommonResult.success(routesService.getRoutes(route));
		}
		return CommonResult.success(CollUtil.newArrayList());
	}

	@PostMapping("/delete/{id}")
	public CommonResult delete(@LoginUser AdminUserDO sysUser,
			@PathVariable("id") Long id) {
		if (!StrUtil.equalsIgnoreCase(sysUser.getUsername(),
				CommonConstant.ADMIN_USER_NAME)) {
			return CommonResult
					.error(ErrorCodeConstants.NO_INTERFACE_AUTH_ERROR);
		}
		routesService.delete(id, true);
		return CommonResult.success("操作成功");
	}
}
