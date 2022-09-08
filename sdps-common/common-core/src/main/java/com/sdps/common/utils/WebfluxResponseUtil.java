package com.sdps.common.utils;

import java.nio.charset.Charset;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

import com.sdps.common.pojo.CommonResult;

/**
 * @author zlt
 * @date 2020/5/5
 *       <p>
 *       Blog: https://zlt2000.gitee.io Github: https://github.com/zlt2000
 */
public class WebfluxResponseUtil {
	/**
	 * webflux的response返回json对象
	 */
	@SuppressWarnings("rawtypes")
	public static Mono<Void> responseWriter(ServerWebExchange exchange,
			int httpStatus, String msg) {
		CommonResult result = CommonResult.of(null, httpStatus, msg);
		return responseWrite(exchange, httpStatus, result);
	}

	@SuppressWarnings("rawtypes")
	public static Mono<Void> responseFailed(ServerWebExchange exchange,
			String msg) {
		CommonResult result = CommonResult.of(null, exchange.getResponse()
				.getStatusCode().value(), msg);
		;
		return responseWrite(exchange,
				HttpStatus.INTERNAL_SERVER_ERROR.value(), result);
	}

	@SuppressWarnings("rawtypes")
	public static Mono<Void> responseFailed(ServerWebExchange exchange,
			int httpStatus, String msg) {
		CommonResult result = CommonResult.of(null, httpStatus, msg);
		return responseWrite(exchange, httpStatus, result);
	}

	@SuppressWarnings("rawtypes")
	public static Mono<Void> responseWrite(ServerWebExchange exchange,
			int httpStatus, CommonResult result) {
		if (httpStatus == 0) {
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR.value();
		}
		ServerHttpResponse response = exchange.getResponse();
		response.getHeaders().setAccessControlAllowCredentials(true);
		response.getHeaders().setAccessControlAllowOrigin("*");
		response.setStatusCode(HttpStatus.valueOf(httpStatus));
		response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
		DataBufferFactory dataBufferFactory = response.bufferFactory();
		DataBuffer buffer = dataBufferFactory.wrap(JsonUtil
				.toJSONString(result).getBytes(Charset.defaultCharset()));
		return response.writeWith(Mono.just(buffer)).doOnError((error) -> {
			DataBufferUtils.release(buffer);
		});
	}
}
