package com.sdps.common.lb.config;


import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cloud.client.ServiceInstance;

import cn.hutool.core.util.StrUtil;

import com.sdps.common.constant.CommonConstant;
import com.sdps.common.constant.ConfigConstants;


/**
 * 将版本注册到注册中心的组件
 *
 * @author jarvis create by 2022/3/20
 */
public class VersionRegisterBeanPostProcessor  implements BeanPostProcessor {
    @Value("${"+ ConfigConstants.CONFIG_LOADBALANCE_VERSION+":}")
    private String version;
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if(bean instanceof ServiceInstance && StrUtil.isNotBlank(version)){
        	ServiceInstance serviceInstance = (ServiceInstance) bean;
        	serviceInstance.getMetadata().putIfAbsent(CommonConstant.METADATA_VERSION, version);
        }
        return bean;
    }
}
