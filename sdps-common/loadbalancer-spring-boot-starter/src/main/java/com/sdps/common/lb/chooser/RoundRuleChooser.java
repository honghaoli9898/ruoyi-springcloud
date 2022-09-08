package com.sdps.common.lb.chooser;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.extern.log4j.Log4j2;

import org.springframework.cloud.client.ServiceInstance;

import cn.hutool.core.collection.CollUtil;

/**
 * 轮询选择器
 *
 * @author jarvis create by 2022/3/13
 */
@Log4j2
public class RoundRuleChooser implements IRuleChooser{

    private AtomicInteger position;

    public RoundRuleChooser() {
        this.position = new AtomicInteger(1000);
    }

    @Override
    public ServiceInstance choose(List<ServiceInstance> instances) {
        if(CollUtil.isNotEmpty(instances)){
            ServiceInstance serviceInstance = instances.get(Math.abs(position.incrementAndGet() % instances.size()));
            log.info("选择了ip为{}, 端口为：{}的服务", serviceInstance.getHost(), serviceInstance.getPort());
            return serviceInstance;
        }
        return null;
    }
}
