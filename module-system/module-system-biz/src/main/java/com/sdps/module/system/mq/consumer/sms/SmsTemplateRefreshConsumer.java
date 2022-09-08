package com.sdps.module.system.mq.consumer.sms;

import com.sdps.common.mq.core.pubsub.AbstractChannelMessageListener;
import com.sdps.module.system.mq.message.sms.SmsTemplateRefreshMessage;
import com.sdps.module.system.service.sms.SysSmsTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 针对 {@link SmsTemplateRefreshMessage} 的消费者
 *
 * @author 芋道源码
 */
@Component
@Slf4j
public class SmsTemplateRefreshConsumer extends AbstractChannelMessageListener<SmsTemplateRefreshMessage> {

    @Resource
    private SysSmsTemplateService sysSmsTemplateService;

    @Override
    public void onMessage(SmsTemplateRefreshMessage message) {
        log.info("[onMessage][收到 SmsTemplate 刷新消息]");
        sysSmsTemplateService.initLocalCache();
    }

}
