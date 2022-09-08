package com.sdps.module.system.mq.consumer.dept;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import com.sdps.common.mq.core.pubsub.AbstractChannelMessageListener;
import com.sdps.module.system.mq.message.dept.DeptRefreshMessage;
import com.sdps.module.system.service.dept.SysDeptService;

/**
 * 针对 {@link DeptRefreshMessage} 的消费者
 *
 * @author 芋道源码
 */
@Component
@Slf4j
public class DeptRefreshConsumer extends AbstractChannelMessageListener<DeptRefreshMessage> {

    @Resource
    private SysDeptService deptService;

    @Override
    public void onMessage(DeptRefreshMessage message) {
        log.info("[onMessage][收到 Dept 刷新消息]");
        deptService.initLocalCache();
    }

}
