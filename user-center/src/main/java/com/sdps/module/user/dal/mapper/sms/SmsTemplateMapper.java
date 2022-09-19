package com.sdps.module.user.dal.mapper.sms;

import com.sdps.common.mybatis.core.mapper.BaseMapperX;
import com.sdps.common.mybatis.core.query.LambdaQueryWrapperX;
import com.sdps.common.pojo.PageResult;
import com.sdps.module.user.controller.admin.sms.vo.template.SmsTemplateExportReqVO;
import com.sdps.module.user.controller.admin.sms.vo.template.SmsTemplatePageReqVO;
import com.sdps.module.system.dal.dataobject.sms.SmsTemplateDO;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

@Mapper
public interface SmsTemplateMapper extends BaseMapperX<SmsTemplateDO> {

	default SmsTemplateDO selectByCode(String code) {
		return selectOne(SmsTemplateDO::getCode, code);
	}

	@Select("SELECT COUNT(*) FROM system_sms_template WHERE update_time > #{maxUpdateTime}")
	Long selectCountByUpdateTimeGt(Date maxUpdateTime);

	default PageResult<SmsTemplateDO> selectPage(SmsTemplatePageReqVO reqVO) {
		return selectPage(
				reqVO,
				new LambdaQueryWrapperX<SmsTemplateDO>()
						.eqIfPresent(SmsTemplateDO::getType, reqVO.getType())
						.eqIfPresent(SmsTemplateDO::getStatus,
								reqVO.getStatus())
						.likeIfPresent(SmsTemplateDO::getCode, reqVO.getCode())
						.likeIfPresent(SmsTemplateDO::getContent,
								reqVO.getContent())
						.likeIfPresent(SmsTemplateDO::getApiTemplateId,
								reqVO.getApiTemplateId())
						.eqIfPresent(SmsTemplateDO::getChannelId,
								reqVO.getChannelId())
						.betweenIfPresent(SmsTemplateDO::getCreateTime,
								reqVO.getCreateTime())
						.orderByDesc(SmsTemplateDO::getId));
	}

	default List<SmsTemplateDO> selectList(SmsTemplateExportReqVO reqVO) {
		return selectList(new LambdaQueryWrapperX<SmsTemplateDO>()
				.eqIfPresent(SmsTemplateDO::getType, reqVO.getType())
				.eqIfPresent(SmsTemplateDO::getStatus, reqVO.getStatus())
				.likeIfPresent(SmsTemplateDO::getCode, reqVO.getCode())
				.likeIfPresent(SmsTemplateDO::getContent, reqVO.getContent())
				.likeIfPresent(SmsTemplateDO::getApiTemplateId,
						reqVO.getApiTemplateId())
				.eqIfPresent(SmsTemplateDO::getChannelId, reqVO.getChannelId())
				.betweenIfPresent(SmsTemplateDO::getCreateTime,
						reqVO.getCreateTime())
				.orderByDesc(SmsTemplateDO::getId));
	}

	default Long selectCountByChannelId(Long channelId) {
		return selectCount(SmsTemplateDO::getChannelId, channelId);
	}

}
