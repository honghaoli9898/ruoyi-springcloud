package com.sdps.common.constant;

public interface ConfigConstants {
	/**
	 * 是否开启自定义隔离规则
	 */
	String CONFIG_RIBBON_ISOLATION_ENABLED = "sdps.ribbon.isolation.enabled";

	String CONFIG_LOADBALANCE_ISOLATION = "sdps.loadbalance.isolation";

	String CONFIG_LOADBALANCE_ISOLATION_ENABLE = CONFIG_LOADBALANCE_ISOLATION
			+ ".enabled";

	String CONFIG_LOADBALANCE_ISOLATION_CHOOSER = CONFIG_LOADBALANCE_ISOLATION
			+ ".chooser";

	String CONFIG_LOADBALANCE_VERSION = "sdps.loadbalance.version";

}