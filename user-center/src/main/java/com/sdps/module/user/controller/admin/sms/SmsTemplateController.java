package com.sdps.module.user.controller.admin.sms;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sdps.common.model.dto.SmsSendSingleToUserReqDTO;
import com.sdps.common.pojo.CommonResult;
import com.sdps.common.pojo.PageResult;
import com.sdps.common.util.excel.ExcelUtils;
import com.sdps.module.system.dal.dataobject.sms.SmsTemplateDO;
import com.sdps.module.user.controller.admin.sms.vo.template.SmsTemplateCreateReqVO;
import com.sdps.module.user.controller.admin.sms.vo.template.SmsTemplateExcelVO;
import com.sdps.module.user.controller.admin.sms.vo.template.SmsTemplateExportReqVO;
import com.sdps.module.user.controller.admin.sms.vo.template.SmsTemplatePageReqVO;
import com.sdps.module.user.controller.admin.sms.vo.template.SmsTemplateRespVO;
import com.sdps.module.user.controller.admin.sms.vo.template.SmsTemplateSendReqVO;
import com.sdps.module.user.controller.admin.sms.vo.template.SmsTemplateUpdateReqVO;
import com.sdps.module.user.convert.sms.SmsTemplateConvert;
import com.sdps.module.user.service.sms.SmsSendService;
import com.sdps.module.user.service.sms.SmsTemplateService;

@Api(tags = "管理后台 - 短信模板")
@RestController
@RequestMapping("/system/sms-template")
public class SmsTemplateController {

	@Resource
	private SmsTemplateService smsTemplateService;
	@Resource
	private SmsSendService smsSendService;

	@PostMapping("/create")
	@ApiOperation("创建短信模板")
	@PreAuthorize("@ss.hasPermission('system:sms-template:create')")
	public CommonResult<Long> createSmsTemplate(
			@Valid @RequestBody SmsTemplateCreateReqVO createReqVO) {
		return CommonResult.success(smsTemplateService
				.createSmsTemplate(createReqVO));
	}

	@PutMapping("/update")
	@ApiOperation("更新短信模板")
	@PreAuthorize("@ss.hasPermission('system:sms-template:update')")
	public CommonResult<Boolean> updateSmsTemplate(
			@Valid @RequestBody SmsTemplateUpdateReqVO updateReqVO) {
		smsTemplateService.updateSmsTemplate(updateReqVO);
		return CommonResult.success(true);
	}

	@DeleteMapping("/delete")
	@ApiOperation("删除短信模板")
	@ApiImplicitParam(name = "id", value = "编号", required = true, dataTypeClass = Long.class)
	@PreAuthorize("@ss.hasPermission('system:sms-template:delete')")
	public CommonResult<Boolean> deleteSmsTemplate(@RequestParam("id") Long id) {
		smsTemplateService.deleteSmsTemplate(id);
		return CommonResult.success(true);
	}

	@GetMapping("/get")
	@ApiOperation("获得短信模板")
	@ApiImplicitParam(name = "id", value = "编号", required = true, example = "1024", dataTypeClass = Long.class)
	@PreAuthorize("@ss.hasPermission('system:sms-template:query')")
	public CommonResult<SmsTemplateRespVO> getSmsTemplate(
			@RequestParam("id") Long id) {
		SmsTemplateDO smsTemplate = smsTemplateService.getSmsTemplate(id);
		return CommonResult.success(SmsTemplateConvert.INSTANCE
				.convert(smsTemplate));
	}

	@GetMapping("/page")
	@ApiOperation("获得短信模板分页")
	@PreAuthorize("@ss.hasPermission('system:sms-template:query')")
	public CommonResult<PageResult<SmsTemplateRespVO>> getSmsTemplatePage(
			@Valid SmsTemplatePageReqVO pageVO) {
		PageResult<SmsTemplateDO> pageResult = smsTemplateService
				.getSmsTemplatePage(pageVO);
		return CommonResult.success(SmsTemplateConvert.INSTANCE
				.convertPage(pageResult));
	}

	@GetMapping("/export-excel")
	@ApiOperation("导出短信模板 Excel")
	@PreAuthorize("@ss.hasPermission('system:sms-template:export')")
	public void exportSmsTemplateExcel(
			@Valid SmsTemplateExportReqVO exportReqVO,
			HttpServletResponse response) throws IOException {
		List<SmsTemplateDO> list = smsTemplateService
				.getSmsTemplateList(exportReqVO);
		// 导出 Excel
		List<SmsTemplateExcelVO> datas = SmsTemplateConvert.INSTANCE
				.convertList02(list);
		ExcelUtils.write(response, "短信模板.xls", "数据", SmsTemplateExcelVO.class,
				datas);
	}

	@PostMapping("/send-sms")
	@ApiOperation("发送短信")
	@PreAuthorize("@ss.hasPermission('system:sms-template:send-sms')")
	public CommonResult<Long> sendSms(
			@Valid @RequestBody SmsTemplateSendReqVO sendReqVO) {
		return CommonResult.success(smsSendService.sendSingleSmsToAdmin(
				sendReqVO.getMobile(), null, sendReqVO.getTemplateCode(),
				sendReqVO.getTemplateParams()));
	}

	@PostMapping("/wf-send-sms")
	@ApiOperation("工作流发送短信")
	public CommonResult<Long> wfSendSms(
			@Valid @RequestBody SmsSendSingleToUserReqDTO smsSendSingleToUserReqDTO) {
		return CommonResult.success(smsSendService.sendSingleSmsToAdmin(
				smsSendSingleToUserReqDTO.getMobile(),
				smsSendSingleToUserReqDTO.getUserId(),
				smsSendSingleToUserReqDTO.getTemplateCode(),
				smsSendSingleToUserReqDTO.getTemplateParams()));
	}

}
