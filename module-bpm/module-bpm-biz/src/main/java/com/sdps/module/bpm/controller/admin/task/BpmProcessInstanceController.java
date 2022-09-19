package com.sdps.module.bpm.controller.admin.task;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sdps.common.pojo.CommonResult;
import com.sdps.common.pojo.PageResult;
import com.sdps.common.util.web.WebFrameworkUtils;
import com.sdps.module.bpm.controller.admin.task.vo.instance.BpmProcessInstanceCancelReqVO;
import com.sdps.module.bpm.controller.admin.task.vo.instance.BpmProcessInstanceCreateReqVO;
import com.sdps.module.bpm.controller.admin.task.vo.instance.BpmProcessInstanceMyPageReqVO;
import com.sdps.module.bpm.controller.admin.task.vo.instance.BpmProcessInstancePageItemRespVO;
import com.sdps.module.bpm.controller.admin.task.vo.instance.BpmProcessInstanceRespVO;
import com.sdps.module.bpm.service.task.BpmProcessInstanceService;

@Api(tags = "管理后台 - 流程实例")
// 流程实例，通过流程定义创建的一次“申请”
@RestController
@RequestMapping("/bpm/process-instance")
@Validated
public class BpmProcessInstanceController {

	@Resource
	private BpmProcessInstanceService processInstanceService;

	@GetMapping("/my-page")
	@ApiOperation(value = "获得我的实例分页列表", notes = "在【我的流程】菜单中，进行调用")
	@PreAuthorize("@ss.hasPermission('bpm:process-instance:query')")
	public CommonResult<PageResult<BpmProcessInstancePageItemRespVO>> getMyProcessInstancePage(
			@Valid BpmProcessInstanceMyPageReqVO pageReqVO) {
		return CommonResult.success(processInstanceService
				.getMyProcessInstancePage(WebFrameworkUtils.getLoginUserId(),
						pageReqVO));
	}

	@PostMapping("/create")
	@ApiOperation("新建流程实例")
	@PreAuthorize("@ss.hasPermission('bpm:process-instance:query')")
	public CommonResult<String> createProcessInstance(
			@Valid @RequestBody BpmProcessInstanceCreateReqVO createReqVO) {
		return CommonResult.success(processInstanceService
				.createProcessInstance(WebFrameworkUtils.getLoginUserId(),
						createReqVO));
	}

	@GetMapping("/get")
	@ApiOperation(value = "获得指定流程实例", notes = "在【流程详细】界面中，进行调用")
	@ApiImplicitParam(name = "id", value = "流程实例的编号", required = true, dataTypeClass = String.class)
	@PreAuthorize("@ss.hasPermission('bpm:process-instance:query')")
	public CommonResult<BpmProcessInstanceRespVO> getProcessInstance(
			@RequestParam("id") String id) {
		return CommonResult.success(processInstanceService
				.getProcessInstanceVO(id));
	}

	@DeleteMapping("/cancel")
	@ApiOperation(value = "取消流程实例", notes = "撤回发起的流程")
	@PreAuthorize("@ss.hasPermission('bpm:process-instance:cancel')")
	public CommonResult<Boolean> cancelProcessInstance(
			@Valid @RequestBody BpmProcessInstanceCancelReqVO cancelReqVO) {
		processInstanceService.cancelProcessInstance(
				WebFrameworkUtils.getLoginUserId(), cancelReqVO);
		return CommonResult.success(true);
	}
}
