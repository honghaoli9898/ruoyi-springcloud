package com.sdps.module.dynamicroute.service;

import com.sdps.module.dynamicroute.dto.GatewayRouteDefinition;
import com.sdps.module.dynamicroute.entity.GatewayRoutes;

import java.util.List;

public interface IRoutesService {

	int add(GatewayRoutes route);

	int update(GatewayRoutes route);

	int delete(Long id, boolean isDel);

	int enableById(Long id, boolean isEbl);

	GatewayRoutes getById(Long id);

	/**
	 * 查询路由信息
	 * 
	 * @return
	 */
	List<GatewayRoutes> getRoutes(GatewayRoutes route);

	/**
	 * 返回组装后网关需要的路由信息
	 * 
	 * @return
	 */
	List<GatewayRouteDefinition> getRouteDefinitions();
}
