package com.sdps.module.bpm.framework.flowable.core.behavior.script.impl;

import java.util.Set;

import javax.annotation.Resource;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.sdps.common.util.collection.SetUtils;
import com.sdps.common.util.number.NumberUtils;
import com.sdps.module.bpm.enums.definition.BpmTaskRuleScriptEnum;
import com.sdps.module.bpm.framework.flowable.core.behavior.script.BpmTaskAssignScript;
import com.sdps.module.bpm.service.task.BpmProcessInstanceService;

/**
 * 分配给发起人审批的 Script 实现类
 *
 * @author 芋道源码
 */
@Component
public class BpmTaskAssignStartUserScript implements BpmTaskAssignScript {

    @Resource
    @Lazy // 解决循环依赖
    private BpmProcessInstanceService bpmProcessInstanceService;

    @Override
    public Set<Long> calculateTaskCandidateUsers(DelegateExecution execution) {
        ProcessInstance processInstance = bpmProcessInstanceService.getProcessInstance(execution.getProcessInstanceId());
        Long startUserId = NumberUtils.parseLong(processInstance.getStartUserId());
        return SetUtils.asSet(startUserId);
    }

    @Override
    public BpmTaskRuleScriptEnum getEnum() {
        return BpmTaskRuleScriptEnum.START_USER;
    }

}
