package com.sdps.common.oauth2;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

import com.sdps.common.oauth2.properties.SecurityProperties;
import com.sdps.common.oauth2.properties.TokenStoreProperties;

/**
 * 鉴权自动配置
 *
 * @author zlt
 * @version 1.0
 * @date 2021/7/24
 *       <p>
 *       Blog: https://zlt2000.gitee.io Github: https://github.com/zlt2000
 */
@EnableConfigurationProperties({ SecurityProperties.class,
		TokenStoreProperties.class })
@ComponentScan
public class AuthClientAutoConfiguration {
}
