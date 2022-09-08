package com.sdps.module.system.service.sms;

/**
 * 短信渠道 Service 接口
 *
 * @author zzf
 * @date 2021/1/25 9:24
 */
public interface SysSmsChannelService {

    /**
     * 初始化短信客户端
     */
    void initSmsClients();

}
