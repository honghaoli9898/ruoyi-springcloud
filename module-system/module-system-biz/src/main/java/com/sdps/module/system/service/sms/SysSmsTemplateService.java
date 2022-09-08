package com.sdps.module.system.service.sms;


import com.sdps.module.system.dal.dataobject.sms.SmsTemplateDO;

import java.util.Map;

/**
 * 短信模板 Service 接口
 *
 * @author zzf
 * @date 2021/1/25 9:24
 */
public interface SysSmsTemplateService {

    /**
     * 初始化短信模板的本地缓存
     */
    void initLocalCache();

    Map<String, SmsTemplateDO> getSmsTemplateCache() ;

}
