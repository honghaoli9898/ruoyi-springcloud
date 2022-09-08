package com.sdps.common.context;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.sdps.common.model.dataobject.user.AdminUserDO;

/**
 * 登录用户holder
 *
 * @author zlt
 * @date 2022/6/26
 *       <p>
 *       Blog: https://zlt2000.gitee.io Github: https://github.com/zlt2000
 */
public class LoginUserContextHolder {
	private static final ThreadLocal<AdminUserDO> CONTEXT = new TransmittableThreadLocal<>();

	public static void setUser(AdminUserDO user) {
		CONTEXT.set(user);
	}

	public static AdminUserDO getUser() {
		return CONTEXT.get();
	}

	public static void clear() {
		CONTEXT.remove();
	}
}