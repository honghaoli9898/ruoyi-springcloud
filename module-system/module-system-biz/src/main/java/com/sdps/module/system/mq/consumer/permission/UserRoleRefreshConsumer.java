package com.sdps.module.system.mq.consumer.permission;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import com.sdps.common.mq.core.pubsub.AbstractChannelMessageListener;
import com.sdps.module.system.mq.message.permission.UserRoleRefreshMessage;
import com.sdps.module.system.service.permission.SysPermissionService;

/**
 * 针对 {@link UserRoleRefreshMessage} 的消费者
 *
 * @author 芋道源码
 */
@Component
@Slf4j
public class UserRoleRefreshConsumer extends AbstractChannelMessageListener<UserRoleRefreshMessage> {

    @Resource
    private SysPermissionService permissionService;

    @Override
    public void onMessage(UserRoleRefreshMessage message) {
        log.info("[onMessage][收到 User 与 Role 的关联刷新消息]");
        permissionService.initLocalCache();
    }

}
