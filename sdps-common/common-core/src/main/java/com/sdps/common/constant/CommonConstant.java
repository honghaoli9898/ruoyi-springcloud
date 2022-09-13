package com.sdps.common.constant;

/**
 * 全局公共常量
 *
 */
public interface CommonConstant {
	String TIME_ZONE_GMT8 = "GMT+8";
	/**
	 * token请求头名称
	 */
	String TOKEN_HEADER = "Authorization";


	String BEARER_TYPE = "Bearer";

	/**
	 * 超级管理员用户名
	 */
	String ADMIN_USER_NAME = "admin";

	/**
	 * 公共日期格式
	 */
	String MONTH_FORMAT = "yyyy-MM";
	String DATE_FORMAT = "yyyy-MM-dd";
	String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	String SIMPLE_MONTH_FORMAT = "yyyyMM";
	String SIMPLE_DATE_FORMAT = "yyyyMMdd";
	String SIMPLE_DATETIME_FORMAT = "yyyyMMddHHmmss";

	String LOCK_KEY_PREFIX = "LOCK_KEY:";

	/**
	 * 租户id参数
	 */
	String TENANT_ID_PARAM = "tenantId";

	/**
	 * 日志链路追踪id信息头
	 */
	String TRACE_ID_HEADER = "x-traceId-header";
	/**
	 * 日志链路追踪id日志标志
	 */
	String LOG_TRACE_ID = "traceId";
	/**
	 * 负载均衡策略-版本号 信息头
	 */
	String Z_L_T_VERSION = "z-l-t-version";
	/**
	 * 注册中心元数据 版本号
	 */
	String METADATA_VERSION = "version";

}
