package com.sdps.module.bpm.framework.flowable.core.behavior.script.impl;

import java.util.Set;

import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;

import com.sdps.module.bpm.enums.definition.BpmTaskRuleScriptEnum;

/**
 * 分配给发起人的一级 Leader 审批的 Script 实现类
 *
 * @author 芋道源码
 */
@Component
public class BpmTaskAssignLeaderX1Script extends BpmTaskAssignLeaderAbstractScript {

    @Override
    public Set<Long> calculateTaskCandidateUsers(DelegateExecution execution) {
        return calculateTaskCandidateUsers(execution, 1);
    }

    @Override
    public BpmTaskRuleScriptEnum getEnum() {
        return BpmTaskRuleScriptEnum.LEADER_X1;
    }

}
