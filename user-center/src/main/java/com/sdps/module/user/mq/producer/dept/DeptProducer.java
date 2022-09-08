package com.sdps.module.user.mq.producer.dept;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.sdps.common.mq.core.RedisMQTemplate;
import com.sdps.module.system.mq.message.dept.DeptRefreshMessage;

/**
 * Dept 部门相关消息的 Producer
 */
@Component
public class DeptProducer {

    @Resource
    private RedisMQTemplate redisMQTemplate;

    /**
     * 发送 {@link DeptRefreshMessage} 消息
     */
    public void sendDeptRefreshMessage() {
        DeptRefreshMessage message = new DeptRefreshMessage();
        redisMQTemplate.send(message);
    }

}
