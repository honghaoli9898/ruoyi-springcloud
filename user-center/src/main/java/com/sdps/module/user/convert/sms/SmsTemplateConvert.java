package com.sdps.module.user.convert.sms;

import com.sdps.common.pojo.PageResult;
import com.sdps.module.user.controller.admin.sms.vo.template.SmsTemplateCreateReqVO;
import com.sdps.module.user.controller.admin.sms.vo.template.SmsTemplateExcelVO;
import com.sdps.module.user.controller.admin.sms.vo.template.SmsTemplateRespVO;
import com.sdps.module.user.controller.admin.sms.vo.template.SmsTemplateUpdateReqVO;
import com.sdps.module.system.dal.dataobject.sms.SmsTemplateDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface SmsTemplateConvert {

    SmsTemplateConvert INSTANCE = Mappers.getMapper(SmsTemplateConvert.class);

    SmsTemplateDO convert(SmsTemplateCreateReqVO bean);

    SmsTemplateDO convert(SmsTemplateUpdateReqVO bean);

    SmsTemplateRespVO convert(SmsTemplateDO bean);

    List<SmsTemplateRespVO> convertList(List<SmsTemplateDO> list);

    PageResult<SmsTemplateRespVO> convertPage(PageResult<SmsTemplateDO> page);

    List<SmsTemplateExcelVO> convertList02(List<SmsTemplateDO> list);

}
