package com.sdps.module.system.mq.consumer.permission;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import com.sdps.common.mq.core.pubsub.AbstractChannelMessageListener;
import com.sdps.module.system.mq.message.permission.RoleRefreshMessage;
import com.sdps.module.system.service.permission.SysRoleService;

/**
 * 针对 {@link RoleRefreshMessage} 的消费者
 *
 * @author 芋道源码
 */
@Component
@Slf4j
public class RoleRefreshConsumer extends AbstractChannelMessageListener<RoleRefreshMessage> {

    @Resource
    private SysRoleService roleService;

    @Override
    public void onMessage(RoleRefreshMessage message) {
        log.info("[onMessage][收到 Role 刷新消息]");
        roleService.initLocalCache();
    }

}
