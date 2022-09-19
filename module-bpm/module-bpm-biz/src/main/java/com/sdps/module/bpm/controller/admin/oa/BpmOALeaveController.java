package com.sdps.module.bpm.controller.admin.oa;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sdps.common.pojo.CommonResult;
import com.sdps.common.pojo.PageResult;
import com.sdps.common.util.web.WebFrameworkUtils;
import com.sdps.module.bpm.controller.admin.oa.vo.BpmOALeaveCreateReqVO;
import com.sdps.module.bpm.controller.admin.oa.vo.BpmOALeavePageReqVO;
import com.sdps.module.bpm.controller.admin.oa.vo.BpmOALeaveRespVO;
import com.sdps.module.bpm.convert.oa.BpmOALeaveConvert;
import com.sdps.module.bpm.dal.dataobject.oa.BpmOALeaveDO;
import com.sdps.module.bpm.service.oa.BpmOALeaveService;

/**
 * OA 请假申请 Controller，用于演示自己存储数据，接入工作流的例子
 *
 * @author jason
 * @author 芋道源码
 */
@Api(tags = "管理后台 - OA 请假申请")
@RestController
@RequestMapping("/bpm/oa/leave")
@Validated
public class BpmOALeaveController {

	@Resource
	private BpmOALeaveService leaveService;

	@PostMapping("/create")
	@PreAuthorize("@ss.hasPermission('bpm:oa-leave:create')")
	@ApiOperation("创建请求申请")
	public CommonResult<Long> createLeave(
			@Valid @RequestBody BpmOALeaveCreateReqVO createReqVO) {
		return CommonResult.success(leaveService.createLeave(
				WebFrameworkUtils.getLoginUserId(), createReqVO));
	}

	@GetMapping("/get")
	@PreAuthorize("@ss.hasPermission('bpm:oa-leave:query')")
	@ApiOperation("获得请假申请")
	@ApiImplicitParam(name = "id", value = "编号", required = true, example = "1024", dataTypeClass = Long.class)
	public CommonResult<BpmOALeaveRespVO> getLeave(@RequestParam("id") Long id) {
		BpmOALeaveDO leave = leaveService.getLeave(id);
		return CommonResult.success(BpmOALeaveConvert.INSTANCE.convert(leave));
	}

	@GetMapping("/page")
	@PreAuthorize("@ss.hasPermission('bpm:oa-leave:query')")
	@ApiOperation("获得请假申请分页")
	public CommonResult<PageResult<BpmOALeaveRespVO>> getLeavePage(
			@Valid BpmOALeavePageReqVO pageVO) {
		PageResult<BpmOALeaveDO> pageResult = leaveService.getLeavePage(
				WebFrameworkUtils.getLoginUserId(), pageVO);
		return CommonResult.success(BpmOALeaveConvert.INSTANCE
				.convertPage(pageResult));
	}

}
