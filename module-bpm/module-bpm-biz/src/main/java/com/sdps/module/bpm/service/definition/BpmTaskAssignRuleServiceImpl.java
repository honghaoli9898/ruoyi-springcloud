package com.sdps.module.bpm.service.definition;

import static cn.hutool.core.text.CharSequenceUtil.format;
import static com.sdps.common.exception.util.ServiceExceptionUtil.exception;
import static com.sdps.common.util.collection.CollectionUtils.convertMap;
import static com.sdps.common.util.collection.CollectionUtils.convertSet;
import static com.sdps.common.util.json.JsonUtils.toJsonString;
import static com.sdps.module.bpm.enums.ErrorCodeConstants.MODEL_DEPLOY_FAIL_TASK_ASSIGN_RULE_NOT_CONFIG;
import static com.sdps.module.bpm.enums.ErrorCodeConstants.TASK_ASSIGN_RULE_EXISTS;
import static com.sdps.module.bpm.enums.ErrorCodeConstants.TASK_ASSIGN_RULE_NOT_EXISTS;
import static com.sdps.module.bpm.enums.ErrorCodeConstants.TASK_ASSIGN_SCRIPT_NOT_EXISTS;
import static com.sdps.module.bpm.enums.ErrorCodeConstants.TASK_CREATE_FAIL_NO_CANDIDATE_USER;
import static com.sdps.module.bpm.enums.ErrorCodeConstants.TASK_UPDATE_FAIL_NOT_MODEL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.annotation.Resource;
import javax.validation.Valid;

import lombok.extern.slf4j.Slf4j;

import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.UserTask;
import org.flowable.common.engine.api.FlowableException;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

import com.google.common.annotations.VisibleForTesting;
import com.sdps.common.datapermission.core.annotation.DataPermission;
import com.sdps.common.enums.CommonStatusEnum;
import com.sdps.common.flowable.core.util.FlowableUtils;
import com.sdps.common.util.collection.CollectionUtils;
import com.sdps.common.util.object.ObjectUtils;
import com.sdps.module.bpm.controller.admin.definition.vo.rule.BpmTaskAssignRuleCreateReqVO;
import com.sdps.module.bpm.controller.admin.definition.vo.rule.BpmTaskAssignRuleRespVO;
import com.sdps.module.bpm.controller.admin.definition.vo.rule.BpmTaskAssignRuleUpdateReqVO;
import com.sdps.module.bpm.convert.definition.BpmTaskAssignRuleConvert;
import com.sdps.module.bpm.dal.dataobject.definition.BpmTaskAssignRuleDO;
import com.sdps.module.bpm.dal.dataobject.definition.BpmUserGroupDO;
import com.sdps.module.bpm.dal.mysql.definition.BpmTaskAssignRuleMapper;
import com.sdps.module.bpm.enums.DictTypeConstants;
import com.sdps.module.bpm.enums.definition.BpmTaskAssignRuleTypeEnum;
import com.sdps.module.bpm.framework.flowable.core.behavior.script.BpmTaskAssignScript;
import com.sdps.module.system.api.dept.DeptApi;
import com.sdps.module.system.api.dept.PostApi;
import com.sdps.module.system.api.dept.dto.DeptRespDTO;
import com.sdps.module.system.api.dict.DictDataApi;
import com.sdps.module.system.api.permission.PermissionApi;
import com.sdps.module.system.api.permission.RoleApi;
import com.sdps.module.system.api.user.AdminUserApi;
import com.sdps.module.system.api.user.dto.AdminUserRespDTO;

/**
 * BPM ?????????????????? Service ?????????
 */
@Service
@Validated
@Slf4j
public class BpmTaskAssignRuleServiceImpl implements BpmTaskAssignRuleService {

    @Resource
    private BpmTaskAssignRuleMapper taskRuleMapper;
    @Resource
    @Lazy // ??????????????????
    private BpmModelService modelService;
    @Resource
    @Lazy // ??????????????????
    private BpmProcessDefinitionService processDefinitionService;
    @Resource
    private BpmUserGroupService userGroupService;
    @Resource
    private RoleApi roleApi;
    @Resource
    private DeptApi deptApi;
    @Resource
    private PostApi postApi;
    @Resource
    private AdminUserApi adminUserApi;
    @Resource
    private DictDataApi dictDataApi;
    @Resource
    private PermissionApi permissionApi;
    /**
     * ??????????????????
     */
    private Map<Long, BpmTaskAssignScript> scriptMap = Collections.emptyMap();

    @Resource
    public void setScripts(List<BpmTaskAssignScript> scripts) {
        this.scriptMap = convertMap(scripts, script -> script.getEnum().getId());
    }

    @Override
    public List<BpmTaskAssignRuleDO> getTaskAssignRuleListByProcessDefinitionId(String processDefinitionId,
                                                                                String taskDefinitionKey) {
        return taskRuleMapper.selectListByProcessDefinitionId(processDefinitionId, taskDefinitionKey);
    }

    @Override
    public List<BpmTaskAssignRuleDO> getTaskAssignRuleListByModelId(String modelId) {
        return taskRuleMapper.selectListByModelId(modelId);
    }

    @Override
    public List<BpmTaskAssignRuleRespVO> getTaskAssignRuleList(String modelId, String processDefinitionId) {
        // ????????????
        List<BpmTaskAssignRuleDO> rules = Collections.emptyList();
        BpmnModel model = null;
        if (StrUtil.isNotEmpty(modelId)) {
            rules = getTaskAssignRuleListByModelId(modelId);
            model = modelService.getBpmnModel(modelId);
        } else if (StrUtil.isNotEmpty(processDefinitionId)) {
            rules = getTaskAssignRuleListByProcessDefinitionId(processDefinitionId, null);
            model = processDefinitionService.getBpmnModel(processDefinitionId);
        }
        if (model == null) {
            return Collections.emptyList();
        }
        // ??????????????????????????????????????????????????????????????????
        List<UserTask> userTasks = FlowableUtils.getBpmnModelElements(model, UserTask.class);
        if (CollUtil.isEmpty(userTasks)) {
            return Collections.emptyList();
        }
        // ????????????
        return BpmTaskAssignRuleConvert.INSTANCE.convertList(userTasks, rules);
    }

    @Override
    public Long createTaskAssignRule(@Valid BpmTaskAssignRuleCreateReqVO reqVO) {
        // ????????????
        validTaskAssignRuleOptions(reqVO.getType(), reqVO.getOptions());
        // ????????????????????????
        BpmTaskAssignRuleDO existRule =
                taskRuleMapper.selectListByModelIdAndTaskDefinitionKey(reqVO.getModelId(), reqVO.getTaskDefinitionKey());
        if (existRule != null) {
            throw exception(TASK_ASSIGN_RULE_EXISTS, reqVO.getModelId(), reqVO.getTaskDefinitionKey());
        }

        // ??????
        BpmTaskAssignRuleDO rule = BpmTaskAssignRuleConvert.INSTANCE.convert(reqVO);
        rule.setProcessDefinitionId(BpmTaskAssignRuleDO.PROCESS_DEFINITION_ID_NULL); // ????????????????????????????????????
        taskRuleMapper.insert(rule);
        return rule.getId();
    }

    @Override
    public void updateTaskAssignRule(@Valid BpmTaskAssignRuleUpdateReqVO reqVO) {
        // ????????????
        validTaskAssignRuleOptions(reqVO.getType(), reqVO.getOptions());
        // ??????????????????
        BpmTaskAssignRuleDO existRule = taskRuleMapper.selectById(reqVO.getId());
        if (existRule == null) {
            throw exception(TASK_ASSIGN_RULE_NOT_EXISTS);
        }
        // ????????????????????????????????????
        if (!Objects.equals(BpmTaskAssignRuleDO.PROCESS_DEFINITION_ID_NULL, existRule.getProcessDefinitionId())) {
            throw exception(TASK_UPDATE_FAIL_NOT_MODEL);
        }

        // ????????????
        taskRuleMapper.updateById(BpmTaskAssignRuleConvert.INSTANCE.convert(reqVO));
    }

    @Override
    public boolean isTaskAssignRulesEquals(String modelId, String processDefinitionId) {
        // ?????? VO ???????????????????????????????????????????????????????????????????????? copyTaskAssignRules ??????????????????
        List<BpmTaskAssignRuleRespVO> modelRules = getTaskAssignRuleList(modelId, null);
        List<BpmTaskAssignRuleRespVO> processInstanceRules = getTaskAssignRuleList(null, processDefinitionId);
        if (modelRules.size() != processInstanceRules.size()) {
            return false;
        }

        // ??????????????????????????????
        Map<String, BpmTaskAssignRuleRespVO> processInstanceRuleMap =
                CollectionUtils.convertMap(processInstanceRules, BpmTaskAssignRuleRespVO::getTaskDefinitionKey);
        for (BpmTaskAssignRuleRespVO modelRule : modelRules) {
            BpmTaskAssignRuleRespVO processInstanceRule = processInstanceRuleMap.get(modelRule.getTaskDefinitionKey());
            if (processInstanceRule == null) {
                return false;
            }
            if (!ObjectUtil.equals(modelRule.getType(), processInstanceRule.getType()) || !ObjectUtil.equal(
                    modelRule.getOptions(), processInstanceRule.getOptions())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void copyTaskAssignRules(String fromModelId, String toProcessDefinitionId) {
        List<BpmTaskAssignRuleRespVO> rules = getTaskAssignRuleList(fromModelId, null);
        if (CollUtil.isEmpty(rules)) {
            return;
        }
        // ????????????
        List<BpmTaskAssignRuleDO> newRules = BpmTaskAssignRuleConvert.INSTANCE.convertList2(rules);
        newRules.forEach(rule -> {
            rule.setProcessDefinitionId(toProcessDefinitionId);
            rule.setId(null);
            rule.setCreateTime(null);
            rule.setUpdateTime(null);
        });
        taskRuleMapper.insertBatch(newRules);
    }

    @Override
    public void checkTaskAssignRuleAllConfig(String id) {
        // ?????????????????????????????????????????????????????????
        List<BpmTaskAssignRuleRespVO> taskAssignRules = getTaskAssignRuleList(id, null);
        if (CollUtil.isEmpty(taskAssignRules)) {
            return;
        }
        // ??????????????????????????????
        taskAssignRules.forEach(rule -> {
            if (CollUtil.isEmpty(rule.getOptions())) {
                throw exception(MODEL_DEPLOY_FAIL_TASK_ASSIGN_RULE_NOT_CONFIG, rule.getTaskDefinitionName());
            }
        });
    }

    private void validTaskAssignRuleOptions(Integer type, Set<Long> options) {
        if (Objects.equals(type, BpmTaskAssignRuleTypeEnum.ROLE.getType())) {
            roleApi.validRoles(options);
        } else if (ObjectUtils.equalsAny(type, BpmTaskAssignRuleTypeEnum.DEPT_MEMBER.getType(),
                BpmTaskAssignRuleTypeEnum.DEPT_LEADER.getType())) {
            deptApi.validDepts(options);
        } else if (Objects.equals(type, BpmTaskAssignRuleTypeEnum.POST.getType())) {
            postApi.validPosts(options);
        } else if (Objects.equals(type, BpmTaskAssignRuleTypeEnum.USER.getType())) {
            adminUserApi.validUsers(options);
        } else if (Objects.equals(type, BpmTaskAssignRuleTypeEnum.USER_GROUP.getType())) {
            userGroupService.validUserGroups(options);
        } else if (Objects.equals(type, BpmTaskAssignRuleTypeEnum.SCRIPT.getType())) {
            dictDataApi.validDictDatas(DictTypeConstants.TASK_ASSIGN_SCRIPT,
                    CollectionUtils.convertSet(options, String::valueOf));
        } else {
            throw new IllegalArgumentException(format("?????????????????????({})", type));
        }
    }

    @Override
    @DataPermission(enable = false) // ????????????????????????????????????????????????
    public Set<Long> calculateTaskCandidateUsers(DelegateExecution execution) {
        BpmTaskAssignRuleDO rule = getTaskRule(execution);
        return calculateTaskCandidateUsers(execution, rule);
    }

    @VisibleForTesting
    BpmTaskAssignRuleDO getTaskRule(DelegateExecution execution) {
        List<BpmTaskAssignRuleDO> taskRules = getTaskAssignRuleListByProcessDefinitionId(
                execution.getProcessDefinitionId(), execution.getCurrentActivityId());
        if (CollUtil.isEmpty(taskRules)) {
            throw new FlowableException(format("????????????({}/{}/{}) ??????????????????????????????",
                    execution.getId(), execution.getProcessDefinitionId(), execution.getCurrentActivityId()));
        }
        if (taskRules.size() > 1) {
            throw new FlowableException(format("????????????({}/{}/{}) ????????????????????????({})",
                    execution.getId(), execution.getProcessDefinitionId(), execution.getCurrentActivityId()));
        }
        return taskRules.get(0);
    }

    @VisibleForTesting
    Set<Long> calculateTaskCandidateUsers(DelegateExecution execution, BpmTaskAssignRuleDO rule) {
        Set<Long> assigneeUserIds = null;
        if (Objects.equals(BpmTaskAssignRuleTypeEnum.ROLE.getType(), rule.getType())) {
            assigneeUserIds = calculateTaskCandidateUsersByRole(rule);
        } else if (Objects.equals(BpmTaskAssignRuleTypeEnum.DEPT_MEMBER.getType(), rule.getType())) {
            assigneeUserIds = calculateTaskCandidateUsersByDeptMember(rule);
        } else if (Objects.equals(BpmTaskAssignRuleTypeEnum.DEPT_LEADER.getType(), rule.getType())) {
            assigneeUserIds = calculateTaskCandidateUsersByDeptLeader(rule);
        } else if (Objects.equals(BpmTaskAssignRuleTypeEnum.POST.getType(), rule.getType())) {
            assigneeUserIds = calculateTaskCandidateUsersByPost(rule);
        } else if (Objects.equals(BpmTaskAssignRuleTypeEnum.USER.getType(), rule.getType())) {
            assigneeUserIds = calculateTaskCandidateUsersByUser(rule);
        } else if (Objects.equals(BpmTaskAssignRuleTypeEnum.USER_GROUP.getType(), rule.getType())) {
            assigneeUserIds = calculateTaskCandidateUsersByUserGroup(rule);
        } else if (Objects.equals(BpmTaskAssignRuleTypeEnum.SCRIPT.getType(), rule.getType())) {
            assigneeUserIds = calculateTaskCandidateUsersByScript(execution, rule);
        }

        // ????????????????????????
        removeDisableUsers(assigneeUserIds);
        // ????????????????????????????????????
        if (CollUtil.isEmpty(assigneeUserIds)) {
            log.error("[calculateTaskCandidateUsers][????????????({}/{}/{}) ????????????({}) ??????????????????]", execution.getId(),
                    execution.getProcessDefinitionId(), execution.getCurrentActivityId(), toJsonString(rule));
            throw exception(TASK_CREATE_FAIL_NO_CANDIDATE_USER);
        }
        return assigneeUserIds;
    }

    private Set<Long> calculateTaskCandidateUsersByRole(BpmTaskAssignRuleDO rule) {
        return permissionApi.getUserRoleIdListByRoleIds(rule.getOptions());
    }

    private Set<Long> calculateTaskCandidateUsersByDeptMember(BpmTaskAssignRuleDO rule) {
        List<AdminUserRespDTO> users = adminUserApi.getUsersByDeptIds(rule.getOptions());
        return convertSet(users, AdminUserRespDTO::getId);
    }

    private Set<Long> calculateTaskCandidateUsersByDeptLeader(BpmTaskAssignRuleDO rule) {
        List<DeptRespDTO> depts = deptApi.getDepts(rule.getOptions());
        return convertSet(depts, DeptRespDTO::getLeaderUserId);
    }

    private Set<Long> calculateTaskCandidateUsersByPost(BpmTaskAssignRuleDO rule) {
        List<AdminUserRespDTO> users = adminUserApi.getUsersByPostIds(rule.getOptions());
        return convertSet(users, AdminUserRespDTO::getId);
    }

    private Set<Long> calculateTaskCandidateUsersByUser(BpmTaskAssignRuleDO rule) {
        return rule.getOptions();
    }

    private Set<Long> calculateTaskCandidateUsersByUserGroup(BpmTaskAssignRuleDO rule) {
        List<BpmUserGroupDO> userGroups = userGroupService.getUserGroupList(rule.getOptions());
        Set<Long> userIds = new HashSet<>();
        userGroups.forEach(group -> userIds.addAll(group.getMemberUserIds()));
        return userIds;
    }

    private Set<Long> calculateTaskCandidateUsersByScript(DelegateExecution execution, BpmTaskAssignRuleDO rule) {
        // ?????????????????????
        List<BpmTaskAssignScript> scripts = new ArrayList<>(rule.getOptions().size());
        rule.getOptions().forEach(id -> {
            BpmTaskAssignScript script = scriptMap.get(id);
            if (script == null) {
                throw exception(TASK_ASSIGN_SCRIPT_NOT_EXISTS, id);
            }
            scripts.add(script);
        });
        // ??????????????????
        Set<Long> userIds = new HashSet<>();
        scripts.forEach(script -> CollUtil.addAll(userIds, script.calculateTaskCandidateUsers(execution)));
        return userIds;
    }

    @VisibleForTesting
    void removeDisableUsers(Set<Long> assigneeUserIds) {
        if (CollUtil.isEmpty(assigneeUserIds)) {
            return;
        }
        Map<Long, AdminUserRespDTO> userMap = adminUserApi.getUserMap(assigneeUserIds);
        assigneeUserIds.removeIf(id -> {
            AdminUserRespDTO user = userMap.get(id);
            return user == null || !CommonStatusEnum.ENABLE.getStatus().equals(user.getStatus());
        });
    }

}
