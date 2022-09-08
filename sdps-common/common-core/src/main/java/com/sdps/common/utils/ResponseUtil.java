package com.sdps.common.utils;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdps.common.pojo.CommonResult;

public class ResponseUtil {
	private ResponseUtil() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * 通过流写到前端
	 *
	 * @param objectMapper
	 *            对象序列化
	 * @param response
	 * @param msg
	 *            返回信息
	 * @param httpStatus
	 *            返回状态码
	 * @throws IOException
	 */
	@SuppressWarnings("rawtypes")
	public static void responseWriter(ObjectMapper objectMapper,
			HttpServletResponse response, String msg, int httpStatus)
			throws IOException {
		CommonResult result = CommonResult.of(null, httpStatus, msg);
		responseWrite(objectMapper, response, result);
	}

	/**
	 * 通过流写到前端
	 * 
	 * @param objectMapper
	 *            对象序列化
	 * @param response
	 * @param obj
	 */
	@SuppressWarnings("rawtypes")
	public static void responseSucceed(ObjectMapper objectMapper,
			HttpServletResponse response, Object obj) throws IOException {
		CommonResult result = CommonResult.success(obj);
		responseWrite(objectMapper, response, result);
	}

	/**
	 * 通过流写到前端
	 * 
	 * @param objectMapper
	 * @param response
	 * @param msg
	 * @throws IOException
	 */
	@SuppressWarnings("rawtypes")
	public static void responseFailed(ObjectMapper objectMapper,
			HttpServletResponse response, String msg) throws IOException {
		CommonResult result = CommonResult.of(null, response.getStatus(), msg);
		responseWrite(objectMapper, response, result);
	}

	@SuppressWarnings({ "rawtypes", "deprecation" })
	private static void responseWrite(ObjectMapper objectMapper,
			HttpServletResponse response, CommonResult result) throws IOException {
		response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
		try (Writer writer = response.getWriter()) {
			writer.write(objectMapper.writeValueAsString(result));
			writer.flush();
		}
	}
}
