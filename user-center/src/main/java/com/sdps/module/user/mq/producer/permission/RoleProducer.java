package com.sdps.module.user.mq.producer.permission;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.sdps.common.mq.core.RedisMQTemplate;
import com.sdps.module.system.mq.message.permission.RoleRefreshMessage;

/**
 * Role 角色相关消息的 Producer
 *
 * @author 芋道源码
 */
@Component
public class RoleProducer {

    @Resource
    private RedisMQTemplate redisMQTemplate;

    /**
     * 发送 {@link RoleRefreshMessage} 消息
     */
    public void sendRoleRefreshMessage() {
        RoleRefreshMessage message = new RoleRefreshMessage();
        redisMQTemplate.send(message);
    }

}
