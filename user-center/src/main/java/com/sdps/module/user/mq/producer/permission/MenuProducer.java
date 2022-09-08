package com.sdps.module.user.mq.producer.permission;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.sdps.common.mq.core.RedisMQTemplate;
import com.sdps.module.system.mq.message.permission.MenuRefreshMessage;

/**
 * Menu 菜单相关消息的 Producer
 */
@Component
public class MenuProducer {

    @Resource
    private RedisMQTemplate redisMQTemplate;

    /**
     * 发送 {@link MenuRefreshMessage} 消息
     */
    public void sendMenuRefreshMessage() {
        MenuRefreshMessage message = new MenuRefreshMessage();
        redisMQTemplate.send(message);
    }

}
