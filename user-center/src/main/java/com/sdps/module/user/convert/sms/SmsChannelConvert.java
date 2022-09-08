package com.sdps.module.user.convert.sms;

import com.sdps.common.pojo.PageResult;
import com.sdps.module.user.controller.admin.sms.vo.channel.SmsChannelCreateReqVO;
import com.sdps.module.user.controller.admin.sms.vo.channel.SmsChannelRespVO;
import com.sdps.module.user.controller.admin.sms.vo.channel.SmsChannelSimpleRespVO;
import com.sdps.module.user.controller.admin.sms.vo.channel.SmsChannelUpdateReqVO;
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
public interface SmsChannelConvert {

    SmsChannelConvert INSTANCE = Mappers.getMapper(SmsChannelConvert.class);

    SmsChannelDO convert(SmsChannelCreateReqVO bean);

    SmsChannelDO convert(SmsChannelUpdateReqVO bean);

    SmsChannelRespVO convert(SmsChannelDO bean);

    List<SmsChannelRespVO> convertList(List<SmsChannelDO> list);

    PageResult<SmsChannelRespVO> convertPage(PageResult<SmsChannelDO> page);

    List<SmsChannelSimpleRespVO> convertList03(List<SmsChannelDO> list);

}
