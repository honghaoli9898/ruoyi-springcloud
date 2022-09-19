package com.sdps.module.bpm.controller.admin.task;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

import java.util.List;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sdps.common.pojo.CommonResult;
import com.sdps.common.pojo.PageResult;
import com.sdps.common.util.web.WebFrameworkUtils;
import com.sdps.module.bpm.controller.admin.task.vo.task.BpmTaskApproveReqVO;
import com.sdps.module.bpm.controller.admin.task.vo.task.BpmTaskDonePageItemRespVO;
import com.sdps.module.bpm.controller.admin.task.vo.task.BpmTaskDonePageReqVO;
import com.sdps.module.bpm.controller.admin.task.vo.task.BpmTaskRejectReqVO;
import com.sdps.module.bpm.controller.admin.task.vo.task.BpmTaskRespVO;
import com.sdps.module.bpm.controller.admin.task.vo.task.BpmTaskTodoPageItemRespVO;
import com.sdps.module.bpm.controller.admin.task.vo.task.BpmTaskTodoPageReqVO;
import com.sdps.module.bpm.controller.admin.task.vo.task.BpmTaskUpdateAssigneeReqVO;
import com.sdps.module.bpm.service.task.BpmTaskService;

@Api(tags = "管理后台 - 流程任务实例")
@RestController
@RequestMapping("/bpm/task")
@Validated
public class BpmTaskController {

	@Resource
	private BpmTaskService taskService;

	@GetMapping("todo-page")
	@ApiOperation("获取 Todo 待办任务分页")
	@PreAuthorize("@ss.hasPermission('bpm:task:query')")
	public CommonResult<PageResult<BpmTaskTodoPageItemRespVO>> getTodoTaskPage(
			@Valid BpmTaskTodoPageReqVO pageVO) {
		return CommonResult.success(taskService.getTodoTaskPage(
				WebFrameworkUtils.getLoginUserId(), pageVO));
	}

	@GetMapping("done-page")
	@ApiOperation("获取 Done 已办任务分页")
	@PreAuthorize("@ss.hasPermission('bpm:task:query')")
	public CommonResult<PageResult<BpmTaskDonePageItemRespVO>> getDoneTaskPage(
			@Valid BpmTaskDonePageReqVO pageVO) {
		return CommonResult.success(taskService.getDoneTaskPage(
				WebFrameworkUtils.getLoginUserId(), pageVO));
	}

	@GetMapping("/list-by-process-instance-id")
	@ApiOperation(value = "获得指定流程实例的任务列表", notes = "包括完成的、未完成的")
	@ApiImplicitParam(name = "processInstanceId", value = "流程实例的编号", required = true, dataTypeClass = String.class)
	@PreAuthorize("@ss.hasPermission('bpm:task:query')")
	public CommonResult<List<BpmTaskRespVO>> getTaskListByProcessInstanceId(
			@RequestParam("processInstanceId") String processInstanceId) {
		return CommonResult.success(taskService
				.getTaskListByProcessInstanceId(processInstanceId));
	}

	@PutMapping("/approve")
	@ApiOperation("通过任务")
	@PreAuthorize("@ss.hasPermission('bpm:task:update')")
	public CommonResult<Boolean> approveTask(
			@Valid @RequestBody BpmTaskApproveReqVO reqVO) {
		taskService.approveTask(WebFrameworkUtils.getLoginUserId(), reqVO);
		return CommonResult.success(true);
	}

	@PutMapping("/reject")
	@ApiOperation("不通过任务")
	@PreAuthorize("@ss.hasPermission('bpm:task:update')")
	public CommonResult<Boolean> rejectTask(
			@Valid @RequestBody BpmTaskRejectReqVO reqVO) {
		taskService.rejectTask(WebFrameworkUtils.getLoginUserId(), reqVO);
		return CommonResult.success(true);
	}

	@PutMapping("/update-assignee")
	@ApiOperation(value = "更新任务的负责人", notes = "用于【流程详情】的【转派】按钮")
	@PreAuthorize("@ss.hasPermission('bpm:task:update')")
	public CommonResult<Boolean> updateTaskAssignee(
			@Valid @RequestBody BpmTaskUpdateAssigneeReqVO reqVO) {
		taskService.updateTaskAssignee(WebFrameworkUtils.getLoginUserId(),
				reqVO);
		return CommonResult.success(true);
	}

}
