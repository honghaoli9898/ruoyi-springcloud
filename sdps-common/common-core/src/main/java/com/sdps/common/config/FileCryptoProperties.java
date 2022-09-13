package com.sdps.common.config;

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "sdps.file.crypto")
public class FileCryptoProperties {
	private String type;
	private String secretKey;
}
