package com.sdps.module.bpm.service.message;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.sdps.common.config.WebProperties;
import com.sdps.common.feign.UserService;
import com.sdps.common.model.dto.SmsSendSingleToUserReqDTO;
import com.sdps.module.bpm.convert.message.BpmMessageConvert;
import com.sdps.module.bpm.enums.message.BpmMessageEnum;
import com.sdps.module.bpm.service.message.dto.BpmMessageSendWhenProcessInstanceApproveReqDTO;
import com.sdps.module.bpm.service.message.dto.BpmMessageSendWhenProcessInstanceRejectReqDTO;
import com.sdps.module.bpm.service.message.dto.BpmMessageSendWhenTaskCreatedReqDTO;

/**
 * BPM 消息 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class BpmMessageServiceImpl implements BpmMessageService {

	@Resource
	private UserService userService;

	@Resource
	private WebProperties webProperties;

	@Override
	public void sendMessageWhenProcessInstanceApprove(
			BpmMessageSendWhenProcessInstanceApproveReqDTO reqDTO) {
		Map<String, Object> templateParams = new HashMap<>();
		templateParams.put("processInstanceName",
				reqDTO.getProcessInstanceName());
		templateParams.put("detailUrl",
				getProcessInstanceDetailUrl(reqDTO.getProcessInstanceId()));
		SmsSendSingleToUserReqDTO smsSendSingleToUserReqDTO = BpmMessageConvert.INSTANCE
				.convert(reqDTO.getStartUserId(),
						BpmMessageEnum.PROCESS_INSTANCE_APPROVE
								.getSmsTemplateCode(), templateParams);
		userService.sendSms(smsSendSingleToUserReqDTO);
	}

	@Override
	public void sendMessageWhenProcessInstanceReject(
			BpmMessageSendWhenProcessInstanceRejectReqDTO reqDTO) {
		Map<String, Object> templateParams = new HashMap<>();
		templateParams.put("processInstanceName",
				reqDTO.getProcessInstanceName());
		templateParams.put("reason", reqDTO.getReason());
		templateParams.put("detailUrl",
				getProcessInstanceDetailUrl(reqDTO.getProcessInstanceId()));
		SmsSendSingleToUserReqDTO smsSendSingleToUserReqDTO = BpmMessageConvert.INSTANCE
				.convert(reqDTO.getStartUserId(),
						BpmMessageEnum.PROCESS_INSTANCE_REJECT
								.getSmsTemplateCode(), templateParams);
		userService.sendSms(smsSendSingleToUserReqDTO);
	}

	@Override
	public void sendMessageWhenTaskAssigned(
			BpmMessageSendWhenTaskCreatedReqDTO reqDTO) {
		Map<String, Object> templateParams = new HashMap<>();
		templateParams.put("processInstanceName",
				reqDTO.getProcessInstanceName());
		templateParams.put("taskName", reqDTO.getTaskName());
		templateParams.put("startUserNickname", reqDTO.getStartUserNickname());
		templateParams.put("detailUrl",
				getProcessInstanceDetailUrl(reqDTO.getProcessInstanceId()));
		SmsSendSingleToUserReqDTO smsSendSingleToUserReqDTO = BpmMessageConvert.INSTANCE
				.convert(reqDTO.getStartUserId(),
						BpmMessageEnum.TASK_ASSIGNED.getSmsTemplateCode(),
						templateParams);
		userService.sendSms(smsSendSingleToUserReqDTO);
	}

	private String getProcessInstanceDetailUrl(String taskId) {
		return webProperties.getAdminUi().getUrl()
				+ "/bpm/process-instance/detail?id=" + taskId;
	}

}
