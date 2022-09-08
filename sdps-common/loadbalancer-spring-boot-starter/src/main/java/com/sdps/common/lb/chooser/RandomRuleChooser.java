package com.sdps.common.lb.chooser;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import lombok.extern.log4j.Log4j2;

import org.springframework.cloud.client.ServiceInstance;

import cn.hutool.core.collection.CollUtil;

/**
 * 随机的选择器
 *
 * @author jarvis create by 2022/3/13
 */
@Log4j2
public class RandomRuleChooser implements IRuleChooser {
    @Override
    public ServiceInstance choose(List<ServiceInstance> instances) {
        if(CollUtil.isNotEmpty(instances)){
            int randomValue = ThreadLocalRandom.current().nextInt(instances.size());
            ServiceInstance serviceInstance = instances.get(randomValue);
            log.info("选择了ip为{}, 端口为：{}的服务", serviceInstance.getHost(), serviceInstance.getPort());
            return serviceInstance;
        }
        return null;
    }
}
