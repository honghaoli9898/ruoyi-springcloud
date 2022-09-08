package com.sdps.common.util.json;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.sdps.common.constant.CommonConstant;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

/**
 * JSON 工具类
 *
 * @author 芋道源码
 */
@UtilityClass
@Slf4j
public class JsonUtils {

	private static ObjectMapper objectMapper = new ObjectMapper();

	static {
		// 忽略在json字符串中存在，但是在java对象中不存在对应属性的情况
		objectMapper.configure(
				DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		// 忽略空Bean转json的错误
		objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		// 允许不带引号的字段名称
		objectMapper.configure(
				JsonReadFeature.ALLOW_UNQUOTED_FIELD_NAMES.mappedFeature(),
				true);
		// 允许单引号
		objectMapper.configure(
				JsonReadFeature.ALLOW_SINGLE_QUOTES.mappedFeature(), true);
		// allow int startWith 0
		objectMapper
				.configure(JsonReadFeature.ALLOW_LEADING_ZEROS_FOR_NUMBERS
						.mappedFeature(), true);
		// 允许字符串存在转义字符：\r \n \t
		objectMapper.configure(
				JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(),
				true);
		// 排除空值字段
		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		// 使用驼峰式
		objectMapper
				.setPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CAMEL_CASE);
		// 使用bean名称
		objectMapper.enable(MapperFeature.USE_STD_BEAN_NAMING);
		// 所有日期格式都统一为固定格式
		objectMapper.setDateFormat(new SimpleDateFormat(
				CommonConstant.DATETIME_FORMAT));
		objectMapper.setTimeZone(TimeZone
				.getTimeZone(CommonConstant.TIME_ZONE_GMT8));
	}

	/**
	 * 初始化 objectMapper 属性
	 * <p>
	 * 通过这样的方式，使用 Spring 创建的 ObjectMapper Bean
	 *
	 * @param objectMapper
	 *            ObjectMapper 对象
	 */
	public static void init(ObjectMapper objectMapper) {
		JsonUtils.objectMapper = objectMapper;
	}

	@SneakyThrows
	public static String toJsonString(Object object) {
		return objectMapper.writeValueAsString(object);
	}

	@SneakyThrows
	public static byte[] toJsonByte(Object object) {
		return objectMapper.writeValueAsBytes(object);
	}

	@SneakyThrows
	public static String toJsonPrettyString(Object object) {
		return objectMapper.writerWithDefaultPrettyPrinter()
				.writeValueAsString(object);
	}

	public static <T> T parseObject(String text, Class<T> clazz) {
		if (StrUtil.isEmpty(text)) {
			return null;
		}
		try {
			return objectMapper.readValue(text, clazz);
		} catch (IOException e) {
			log.error("json parse err,json:{}", text, e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * 将字符串解析成指定类型的对象 使用 {@link #parseObject(String, Class)}
	 * 时，在@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS) 的场景下， 如果 text 没有 class
	 * 属性，则会报错。此时，使用这个方法，可以解决。
	 *
	 * @param text
	 *            字符串
	 * @param clazz
	 *            类型
	 * @return 对象
	 */
	public static <T> T parseObject2(String text, Class<T> clazz) {
		if (StrUtil.isEmpty(text)) {
			return null;
		}
		return JSONUtil.toBean(text, clazz);
	}

	public static <T> T parseObject(byte[] bytes, Class<T> clazz) {
		if (ArrayUtil.isEmpty(bytes)) {
			return null;
		}
		try {
			return objectMapper.readValue(bytes, clazz);
		} catch (IOException e) {
			log.error("json parse err,json:{}", bytes, e);
			throw new RuntimeException(e);
		}
	}

	public static <T> T parseObject(String text, TypeReference<T> typeReference) {
		try {
			return objectMapper.readValue(text, typeReference);
		} catch (IOException e) {
			log.error("json parse err,json:{}", text, e);
			throw new RuntimeException(e);
		}
	}

	public static <T> List<T> parseArray(String text, Class<T> clazz) {
		if (StrUtil.isEmpty(text)) {
			return new ArrayList<>();
		}
		try {
			return objectMapper.readValue(text, objectMapper.getTypeFactory()
					.constructCollectionType(List.class, clazz));
		} catch (IOException e) {
			log.error("json parse err,json:{}", text, e);
			throw new RuntimeException(e);
		}
	}

	public static JsonNode parseTree(String text) {
		try {
			return objectMapper.readTree(text);
		} catch (IOException e) {
			log.error("json parse err,json:{}", text, e);
			throw new RuntimeException(e);
		}
	}

	public static JsonNode parseTree(byte[] text) {
		try {
			return objectMapper.readTree(text);
		} catch (IOException e) {
			log.error("json parse err,json:{}", text, e);
			throw new RuntimeException(e);
		}
	}

	public static boolean isJson(String text) {
		return JSONUtil.isTypeJSON(text);
	}

}
