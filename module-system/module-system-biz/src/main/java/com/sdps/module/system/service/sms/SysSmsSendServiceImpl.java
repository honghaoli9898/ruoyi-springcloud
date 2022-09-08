package com.sdps.module.system.service.sms;

import cn.hutool.core.lang.Assert;
import com.sdps.common.sms.core.client.SmsClient;
import com.sdps.common.sms.core.client.SmsClientFactory;
import com.sdps.common.sms.core.client.SmsCommonResult;
import com.sdps.common.sms.core.client.dto.SmsSendRespDTO;
import com.sdps.module.system.mq.message.sms.SmsSendMessage;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;


/**
 * 短信发送 Service 发送的实现
 *
 * @author 芋道源码
 */
@Service
public class SysSmsSendServiceImpl implements SysSmsSendService {


    @Resource
    private SysSmsLogService sysSmsLogService;

    @Resource
    private SmsClientFactory smsClientFactory;


    @Override
    public void doSendSms(SmsSendMessage message) {
        // 获得渠道对应的 SmsClient 客户端
        SmsClient smsClient = smsClientFactory.getSmsClient(message.getChannelId());
        Assert.notNull(smsClient, "短信客户端({}) 不存在", message.getChannelId());
        // 发送短信
        SmsCommonResult<SmsSendRespDTO> sendResult = smsClient.sendSms(message.getLogId(), message.getMobile(),
                message.getApiTemplateId(), message.getTemplateParams());
        sysSmsLogService.updateSmsSendResult(message.getLogId(), sendResult.getCode(), sendResult.getMsg(),
                sendResult.getApiCode(), sendResult.getApiMsg(), sendResult.getApiRequestId(),
                sendResult.getData() != null ? sendResult.getData().getSerialNo() : null);
    }


}
