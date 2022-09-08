package com.sdps.common.errorcode.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Validated
@ConfigurationProperties("sdps.error-code")
public class ErrorCodeProperties {

	/**
	 * 是否开启
	 */
	private Boolean enable = true;
	/**
	 * 错误码枚举类
	 */
	@NotNull(message = "错误码枚举类不能为空")
	private List<String> constantsClassList;

}
