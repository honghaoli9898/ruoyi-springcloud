package com.sdps.module.system.service.sms;
import com.sdps.module.system.mq.message.sms.SmsSendMessage;
/**
 * 短信发送 Service 接口
 *
 * @author 芋道源码
 */
public interface SysSmsSendService {

    /**
     * 执行真正的短信发送
     * 注意，该方法仅仅提供给 MQ Consumer 使用
     *
     * @param message 短信
     */
    void doSendSms(SmsSendMessage message);


}
