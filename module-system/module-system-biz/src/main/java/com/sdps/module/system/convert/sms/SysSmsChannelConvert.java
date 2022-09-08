package com.sdps.module.system.convert.sms;

import com.sdps.common.sms.core.property.SmsChannelProperties;
import com.sdps.module.system.dal.dataobject.sms.SmsChannelDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 短信渠道 Convert
 *
 * @author 芋道源码
 */
@Mapper
public interface SysSmsChannelConvert {

    SysSmsChannelConvert INSTANCE = Mappers.getMapper(SysSmsChannelConvert.class);

    List<SmsChannelProperties> convertList02(List<SmsChannelDO> list);

}
