package com.sdps.common.lb.utils;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import cn.hutool.core.util.StrUtil;

/**
 * 解析request的query参数工具
 *
 * @author jarvis create by 2022/5/8
 */
public class QueryUtils {
	/**
	 * 通过query字符串得到参数的map
	 * 
	 * @param queryString
	 *            ?后的字符
	 * @return
	 */
	public static Map<String, String> getQueryMap(String queryString) {
		if (StrUtil.isNotBlank(queryString)) {
			return Arrays
					.stream(queryString.split("&"))
					.map(item -> item.split("="))
					.collect(
							Collectors
									.toMap(key -> key[0],
											value -> value.length > 1
													&& StrUtil
															.isNotBlank(value[1]) ? value[1]
													: ""));
		}
		return Collections.emptyMap();
	}

	/**
	 * 通过url获取参数map
	 * 
	 * @param uri
	 * @return
	 */
	public static Map<String, String> getQueryMap(URI uri) {
		if (Objects.nonNull(uri)) {
			return getQueryMap(uri.getQuery());
		}
		return Collections.emptyMap();
	}
}
