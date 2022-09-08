package com.sdps.module.user.controller.admin.sms.vo.channel;


import com.sdps.common.pojo.PageParam;
import com.sdps.common.util.date.DateUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;


@ApiModel("管理后台 - 短信渠道分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SmsChannelPageReqVO extends PageParam {

    @ApiModelProperty(value = "任务状态", example = "1")
    private Integer status;

    @ApiModelProperty(value = "短信签名", example = "芋道源码", notes = "模糊匹配")
    private String signature;

    @DateTimeFormat(pattern = DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    @ApiModelProperty(value = "创建时间")
    private Date[] createTime;

}
