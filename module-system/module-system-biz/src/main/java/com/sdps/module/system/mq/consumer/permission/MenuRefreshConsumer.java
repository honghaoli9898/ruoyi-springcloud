package com.sdps.module.system.mq.consumer.permission;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import com.sdps.common.mq.core.pubsub.AbstractChannelMessageListener;
import com.sdps.module.system.mq.message.permission.MenuRefreshMessage;
import com.sdps.module.system.service.permission.SysMenuService;

/**
 * 针对 {@link MenuRefreshMessage} 的消费者
 *
 * @author 芋道源码
 */
@Component
@Slf4j
public class MenuRefreshConsumer extends AbstractChannelMessageListener<MenuRefreshMessage> {

    @Resource
    private SysMenuService menuService;

    @Override
    public void onMessage(MenuRefreshMessage message) {
        log.info("[onMessage][收到 Menu 刷新消息]");
        menuService.initLocalCache();
    }

}
