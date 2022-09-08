package com.sdps.common.errorcode.core.loader;

import com.sdps.common.exception.util.ServiceExceptionUtil;

public interface ErrorCodeLoader {

	/**
	 * 添加错误码
	 *
	 * @param code
	 *            错误码的编号
	 * @param msg
	 *            错误码的提示
	 */
	default void putErrorCode(Integer code, String msg) {
		ServiceExceptionUtil.put(code, msg);
	}

}
