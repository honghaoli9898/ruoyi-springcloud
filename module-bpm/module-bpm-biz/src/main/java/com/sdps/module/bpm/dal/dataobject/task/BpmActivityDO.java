package com.sdps.module.bpm.dal.dataobject.task;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sdps.common.util.date.DateUtils;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;


/**
 * 任务流程关联表
 *
 * @author kemengkai
 * @create 2022-05-09 10:33
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BpmActivityDO {

    /**
     * 任务流程关联id
     */
    private String id;

    /**
     * 审批结果
     */
    private Integer rev;

    /**
     * 任务流程部署id
     */
    private String procDefId;

    /**
     * 任务流程id
     */
    private String processInstanceId;

    /**
     * 任务执行id
     */
    private String executionId;

    /**
     * 任务key
     */
    private String activityId;

    /**
     * 任务id
     */
    private String taskId;

    /**
     * 调用流程id
     */
    private String callProcInstId;

    /**
     * 任务名称
     */
    private String activityName;

    /**
     * 任务类型
     */
    private String activityType;

    /**
     * 任务审批人id
     */
    private String assignee;

    /**
     * 任务开始时间
     */
    @DateTimeFormat(pattern = DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    @JsonFormat(pattern = DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND, timezone = DateUtils.TIME_ZONE_DEFAULT)
    private Date startTime;

    /**
     * 任务结束时间
     */
    @DateTimeFormat(pattern = DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    @JsonFormat(pattern = DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND, timezone = DateUtils.TIME_ZONE_DEFAULT)
    private Date endTime;

    private Integer transactionOrder;

    private Long duration;

    /**
     * 删除结果
     */
    private String deleteReason;

    /**
     * 租户id
     */
    private String tenantId;
}
