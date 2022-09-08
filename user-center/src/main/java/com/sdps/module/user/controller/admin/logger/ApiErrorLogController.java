package com.sdps.module.user.controller.admin.logger;

import static com.sdps.common.pojo.CommonResult.success;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sdps.common.pojo.CommonResult;
import com.sdps.common.pojo.PageResult;
import com.sdps.common.util.excel.ExcelUtils;
import com.sdps.common.util.web.WebFrameworkUtils;
import com.sdps.module.system.dal.dataobject.logger.ApiErrorLogDO;
import com.sdps.module.user.controller.admin.logger.vo.apierrorlog.ApiErrorLogExcelVO;
import com.sdps.module.user.controller.admin.logger.vo.apierrorlog.ApiErrorLogExportReqVO;
import com.sdps.module.user.controller.admin.logger.vo.apierrorlog.ApiErrorLogPageReqVO;
import com.sdps.module.user.controller.admin.logger.vo.apierrorlog.ApiErrorLogRespVO;
import com.sdps.module.user.convert.logger.ApiErrorLogConvert;
import com.sdps.module.user.service.logger.ApiErrorLogService;

@Api(tags = "管理后台 - API 错误日志")
@RestController
@RequestMapping("/infra/api-error-log")
@Validated
public class ApiErrorLogController {

	@Resource
	private ApiErrorLogService apiErrorLogService;

	@PutMapping("/update-status")
	@ApiOperation("更新 API 错误日志的状态")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", value = "编号", required = true, example = "1024", dataTypeClass = Long.class),
			@ApiImplicitParam(name = "processStatus", value = "处理状态", required = true, example = "1", dataTypeClass = Integer.class) })
	@PreAuthorize("@ss.hasPermission('infra:api-error-log:update-status')")
	public CommonResult<Boolean> updateApiErrorLogProcess(
			@RequestParam("id") Long id,
			@RequestParam("processStatus") Integer processStatus) {
		apiErrorLogService.updateApiErrorLogProcess(id, processStatus,
				WebFrameworkUtils.getLoginUserId());
		return success(true);
	}

	@GetMapping("/page")
	@ApiOperation("获得 API 错误日志分页")
	@PreAuthorize("@ss.hasPermission('infra:api-error-log:query')")
	public CommonResult<PageResult<ApiErrorLogRespVO>> getApiErrorLogPage(
			@Valid ApiErrorLogPageReqVO pageVO) {
		PageResult<ApiErrorLogDO> pageResult = apiErrorLogService
				.getApiErrorLogPage(pageVO);
		return success(ApiErrorLogConvert.INSTANCE.convertPage(pageResult));
	}

	@GetMapping("/export-excel")
	@ApiOperation("导出 API 错误日志 Excel")
	@PreAuthorize("@ss.hasPermission('infra:api-error-log:export')")
	public void exportApiErrorLogExcel(
			@Valid ApiErrorLogExportReqVO exportReqVO,
			HttpServletResponse response) throws IOException {
		List<ApiErrorLogDO> list = apiErrorLogService
				.getApiErrorLogList(exportReqVO);
		// 导出 Excel
		List<ApiErrorLogExcelVO> datas = ApiErrorLogConvert.INSTANCE
				.convertList02(list);
		ExcelUtils.write(response, "API 错误日志.xls", "数据",
				ApiErrorLogExcelVO.class, datas);
	}

}
