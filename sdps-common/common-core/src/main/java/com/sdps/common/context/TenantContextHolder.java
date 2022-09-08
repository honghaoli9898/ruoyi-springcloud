package com.sdps.common.context;

import com.alibaba.ttl.TransmittableThreadLocal;

/**
 * 多租户上下文 Holder
 *
 * @author 芋道源码
 */
public class TenantContextHolder {

	/**
	 * 当前租户编号
	 */
	private static final ThreadLocal<Long> TENANTID = new TransmittableThreadLocal<>();

	/**
	 * 当前租户编号
	 */
	private static final ThreadLocal<String> TENANT = new TransmittableThreadLocal<String>();

	/**
	 * 是否忽略租户
	 */
	private static final ThreadLocal<Boolean> IGNORE = new TransmittableThreadLocal<>();

	/**
	 * 获得租户编号。
	 *
	 * @return 租户编号
	 */
	public static Long getTenantId() {
		return TENANTID.get();
	}

	public static String getTenant() {
		return TENANT.get();
	}

	/**
	 * 获得租户编号。如果不存在，则抛出 NullPointerException 异常
	 *
	 * @return 租户编号
	 */
	public static Long getRequiredTenantId() {
		Long tenantId = getTenantId();
		if (tenantId == null) {
//			throw new NullPointerException("TenantContextHolder 不存在租户ID编号");
			return -1L;
		}
		return tenantId;
	}

	public static String getRequiredTenant() {
		String tenant = getTenant();
		if (tenant == null) {
			throw new NullPointerException("TenantContextHolder 不存在租户编号");
		}
		return tenant;
	}

	public static void setTenantId(Long tenantId) {
		TENANTID.set(tenantId);
	}

	public static void setTenant(String tenant) {
		TENANT.set(tenant);
	}

	public static void setIgnore(Boolean ignore) {
		IGNORE.set(ignore);
	}

	/**
	 * 当前是否忽略租户
	 *
	 * @return 是否忽略
	 */
	public static boolean isIgnore() {
		return Boolean.TRUE.equals(IGNORE.get());
	}

	public static void clear() {
		TENANTID.remove();
		IGNORE.remove();
		TENANT.remove();
	}
}