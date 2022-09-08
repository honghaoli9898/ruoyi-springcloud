package com.sdps.module.system.dal.mapper.sms;

import com.sdps.common.mybatis.core.mapper.BaseMapperX;
import com.sdps.module.system.dal.dataobject.sms.SmsTemplateDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Date;


@Mapper
public interface SysSmsTemplateMapper extends BaseMapperX<SmsTemplateDO> {

    @Select("SELECT COUNT(*) FROM system_sms_template WHERE update_time > #{maxUpdateTime}")
    Long selectCountByUpdateTimeGt(Date maxUpdateTime);


}
