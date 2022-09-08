package com.sdps.module.system.service.sms;

/**
 * 短信日志 Service 接口
 *
 * @author zzf
 * @date 13:48 2021/3/2
 */
public interface SysSmsLogService {


    /**
     * 更新日志的发送结果
     *
     * @param id           日志编号
     * @param sendCode     发送结果的编码
     * @param sendMsg      发送结果的提示
     * @param apiSendCode  短信 API 发送结果的编码
     * @param apiSendMsg   短信 API 发送失败的提示
     * @param apiRequestId 短信 API 发送返回的唯一请求 ID
     * @param apiSerialNo  短信 API 发送返回的序号
     */
    void updateSmsSendResult(Long id, Integer sendCode, String sendMsg,
                             String apiSendCode, String apiSendMsg, String apiRequestId, String apiSerialNo);


}
