package com.sdps.module.system.mq.consumer.sms;

import com.sdps.common.mq.core.stream.AbstractStreamMessageListener;
import com.sdps.module.system.mq.message.sms.SmsSendMessage;
import com.sdps.module.system.service.sms.SysSmsSendService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 针对 {@link SmsSendMessage} 的消费者
 *
 * @author zzf
 */
@Component
@Slf4j
public class SmsSendConsumer extends AbstractStreamMessageListener<SmsSendMessage> {

    @Resource
    private SysSmsSendService sysSmsSendService;

    @Override
    public void onMessage(SmsSendMessage message) {
        log.info("[onMessage][消息内容({})]", message);
        sysSmsSendService.doSendSms(message);
    }

}
