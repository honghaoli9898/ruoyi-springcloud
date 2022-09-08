package com.sdps.module.user.controller.admin.logger;

import static com.sdps.common.pojo.CommonResult.success;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sdps.common.pojo.CommonResult;
import com.sdps.common.pojo.PageResult;
import com.sdps.common.util.excel.ExcelUtils;
import com.sdps.module.system.dal.dataobject.logger.ApiAccessLogDO;
import com.sdps.module.user.controller.admin.logger.vo.apiaccesslog.ApiAccessLogExcelVO;
import com.sdps.module.user.controller.admin.logger.vo.apiaccesslog.ApiAccessLogExportReqVO;
import com.sdps.module.user.controller.admin.logger.vo.apiaccesslog.ApiAccessLogPageReqVO;
import com.sdps.module.user.controller.admin.logger.vo.apiaccesslog.ApiAccessLogRespVO;
import com.sdps.module.user.convert.logger.ApiAccessLogConvert;
import com.sdps.module.user.service.logger.ApiAccessLogService;

@Api(tags = "管理后台 - API 访问日志")
@RestController
@RequestMapping("/infra/api-access-log")
@Validated
public class ApiAccessLogController {

	@Resource
	private ApiAccessLogService apiAccessLogService;

	@GetMapping("/page")
	@ApiOperation("获得API 访问日志分页")
	@PreAuthorize("@ss.hasPermission('infra:api-access-log:query')")
	public CommonResult<PageResult<ApiAccessLogRespVO>> getApiAccessLogPage(
			@Valid ApiAccessLogPageReqVO pageVO) {
		PageResult<ApiAccessLogDO> pageResult = apiAccessLogService
				.getApiAccessLogPage(pageVO);
		return success(ApiAccessLogConvert.INSTANCE.convertPage(pageResult));
	}

	@GetMapping("/export-excel")
	@ApiOperation("导出API 访问日志 Excel")
	@PreAuthorize("@ss.hasPermission('infra:api-access-log:export')")
	public void exportApiAccessLogExcel(
			@Valid ApiAccessLogExportReqVO exportReqVO,
			HttpServletResponse response) throws IOException {
		List<ApiAccessLogDO> list = apiAccessLogService
				.getApiAccessLogList(exportReqVO);
		// 导出 Excel
		List<ApiAccessLogExcelVO> datas = ApiAccessLogConvert.INSTANCE
				.convertList02(list);
		ExcelUtils.write(response, "API 访问日志.xls", "数据",
				ApiAccessLogExcelVO.class, datas);
	}

}
