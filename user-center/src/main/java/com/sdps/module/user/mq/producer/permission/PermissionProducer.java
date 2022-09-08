package com.sdps.module.user.mq.producer.permission;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.sdps.common.mq.core.RedisMQTemplate;
import com.sdps.module.system.mq.message.permission.RoleMenuRefreshMessage;
import com.sdps.module.system.mq.message.permission.UserRoleRefreshMessage;

/**
 * Permission 权限相关消息的 Producer
 */
@Component
public class PermissionProducer {

    @Resource
    private RedisMQTemplate redisMQTemplate;

    /**
     * 发送 {@link RoleMenuRefreshMessage} 消息
     */
    public void sendRoleMenuRefreshMessage() {
        RoleMenuRefreshMessage message = new RoleMenuRefreshMessage();
        redisMQTemplate.send(message);
    }

    /**
     * 发送 {@link UserRoleRefreshMessage} 消息
     */
    public void sendUserRoleRefreshMessage() {
        UserRoleRefreshMessage message = new UserRoleRefreshMessage();
        redisMQTemplate.send(message);
    }

}
