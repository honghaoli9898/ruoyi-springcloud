package com.sdps.module.user.controller.admin.sms;

import com.sdps.common.pojo.CommonResult;
import com.sdps.common.pojo.PageResult;
import com.sdps.common.util.excel.ExcelUtils;
import com.sdps.module.system.dal.dataobject.sms.SmsLogDO;
import com.sdps.module.user.controller.admin.sms.vo.log.SmsLogExcelVO;
import com.sdps.module.user.controller.admin.sms.vo.log.SmsLogExportReqVO;
import com.sdps.module.user.controller.admin.sms.vo.log.SmsLogPageReqVO;
import com.sdps.module.user.controller.admin.sms.vo.log.SmsLogRespVO;
import com.sdps.module.user.convert.sms.SmsLogConvert;
import com.sdps.module.user.service.sms.SmsLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;


@Api(tags = "管理后台 - 短信日志")
@RestController
@RequestMapping("/system/sms-log")
@Validated
public class SmsLogController {

    @Resource
    private SmsLogService smsLogService;

    @GetMapping("/page")
    @ApiOperation("获得短信日志分页")
    @PreAuthorize("@ss.hasPermission('system:sms-log:query')")
    public CommonResult<PageResult<SmsLogRespVO>> getSmsLogPage(@Valid SmsLogPageReqVO pageVO) {
        PageResult<SmsLogDO> pageResult = smsLogService.getSmsLogPage(pageVO);
        return CommonResult.success(SmsLogConvert.INSTANCE.convertPage(pageResult));
    }

    @GetMapping("/export-excel")
    @ApiOperation("导出短信日志 Excel")
    @PreAuthorize("@ss.hasPermission('system:sms-log:export')")
    public void exportSmsLogExcel(@Valid SmsLogExportReqVO exportReqVO,
                                  HttpServletResponse response) throws IOException {
        List<SmsLogDO> list = smsLogService.getSmsLogList(exportReqVO);
        // 导出 Excel
        List<SmsLogExcelVO> datas = SmsLogConvert.INSTANCE.convertList02(list);
        ExcelUtils.write(response, "短信日志.xls", "数据", SmsLogExcelVO.class, datas);
    }

}
