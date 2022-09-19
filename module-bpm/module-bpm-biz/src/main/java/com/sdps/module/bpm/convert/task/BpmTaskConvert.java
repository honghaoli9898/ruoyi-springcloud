package com.sdps.module.bpm.convert.task;

import com.sdps.common.util.collection.CollectionUtils;
import com.sdps.common.util.number.NumberUtils;
import com.sdps.module.bpm.controller.admin.task.vo.task.BpmTaskDonePageItemRespVO;
import com.sdps.module.bpm.controller.admin.task.vo.task.BpmTaskRespVO;
import com.sdps.module.bpm.controller.admin.task.vo.task.BpmTaskTodoPageItemRespVO;
import com.sdps.module.bpm.dal.dataobject.task.BpmTaskExtDO;
import com.sdps.module.bpm.service.message.dto.BpmMessageSendWhenTaskCreatedReqDTO;
import com.sdps.module.system.api.dept.dto.DeptRespDTO;
import com.sdps.module.system.api.user.dto.AdminUserRespDTO;
import org.flowable.common.engine.impl.db.SuspensionState;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.BeanUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Bpm 任务 Convert
 *
 * @author 芋道源码
 */
@Mapper
public interface BpmTaskConvert {

    BpmTaskConvert INSTANCE = Mappers.getMapper(BpmTaskConvert.class);

    /**
     * 复制对象
     *
     * @param source 源 要复制的对象
     * @param target 目标 复制到此对象
     * @param <T>
     * @return
     */
    public static <T> T copy(Object source, Class<T> target) {
        if (source == null || target == null) {
            return null;
        }
        try {
            T newInstance = target.newInstance();
            BeanUtils.copyProperties(source, newInstance);
            return newInstance;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    default <T, K> List<K> copyList(List<T> source, Class<K> target) {
        if (null == source || source.isEmpty()) {
            return Collections.emptyList();
        }
        return source.stream().map(e -> copy(e, target)).collect(Collectors.toList());
    }

    default List<BpmTaskTodoPageItemRespVO> convertList1(List<Task> tasks,
                                                         Map<String, ProcessInstance> processInstanceMap, Map<Long, AdminUserRespDTO> userMap) {
        return CollectionUtils.convertList(tasks, task -> {
            BpmTaskTodoPageItemRespVO respVO = convert1(task);
            ProcessInstance processInstance = processInstanceMap.get(task.getProcessInstanceId());
            if (processInstance != null) {
                AdminUserRespDTO startUser = userMap.get(NumberUtils.parseLong(processInstance.getStartUserId()));
                respVO.setProcessInstance(convert(processInstance, startUser));
            }
            return respVO;
        });
    }

    @Mapping(source = "suspended", target = "suspensionState", qualifiedByName = "convertSuspendedToSuspensionState")
    BpmTaskTodoPageItemRespVO convert1(Task bean);

    @Named("convertSuspendedToSuspensionState")
    default Integer convertSuspendedToSuspensionState(boolean suspended) {
        return suspended ? SuspensionState.SUSPENDED.getStateCode() : SuspensionState.ACTIVE.getStateCode();
    }

    default List<BpmTaskDonePageItemRespVO> convertList2(List<HistoricTaskInstance> tasks,
                                                         Map<String, BpmTaskExtDO> bpmTaskExtDOMap, Map<String, HistoricProcessInstance> historicProcessInstanceMap,
                                                         Map<Long, AdminUserRespDTO> userMap) {
        return CollectionUtils.convertList(tasks, task -> {
            BpmTaskDonePageItemRespVO respVO = convert2(task);
            BpmTaskExtDO taskExtDO = bpmTaskExtDOMap.get(task.getId());
            copyTo(taskExtDO, respVO);
            HistoricProcessInstance processInstance = historicProcessInstanceMap.get(task.getProcessInstanceId());
            if (processInstance != null) {
                AdminUserRespDTO startUser = userMap.get(NumberUtils.parseLong(processInstance.getStartUserId()));
                respVO.setProcessInstance(convert(processInstance, startUser));
            }
            return respVO;
        });
    }

    BpmTaskDonePageItemRespVO convert2(HistoricTaskInstance bean);

    @Mappings({@Mapping(source = "processInstance.id", target = "id"),
            @Mapping(source = "processInstance.name", target = "name"),
            @Mapping(source = "processInstance.startUserId", target = "startUserId"),
            @Mapping(source = "processInstance.processDefinitionId", target = "processDefinitionId"),
            @Mapping(source = "startUser.nickname", target = "startUserNickname")})
    BpmTaskTodoPageItemRespVO.ProcessInstance convert(ProcessInstance processInstance, AdminUserRespDTO startUser);

    default List<BpmTaskRespVO> convertList3(List<HistoricTaskInstance> tasks,
                                             Map<String, BpmTaskExtDO> bpmTaskExtDOMap, HistoricProcessInstance processInstance,
                                             Map<Long, AdminUserRespDTO> userMap, Map<Long, DeptRespDTO> deptMap) {
        return CollectionUtils.convertList(tasks, task -> {
            BpmTaskRespVO respVO = convert3(task);
            BpmTaskExtDO taskExtDO = bpmTaskExtDOMap.get(task.getId());
            copyTo(taskExtDO, respVO);
            if (processInstance != null) {
                AdminUserRespDTO startUser = userMap.get(NumberUtils.parseLong(processInstance.getStartUserId()));
                respVO.setProcessInstance(convert(processInstance, startUser));
            }
            AdminUserRespDTO assignUser = userMap.get(NumberUtils.parseLong(task.getAssignee()));
            if (assignUser != null) {
                respVO.setAssigneeUser(convert3(assignUser));
                DeptRespDTO dept = deptMap.get(assignUser.getDeptId());
                if (dept != null) {
                    respVO.getAssigneeUser().setDeptName(dept.getName());
                }
            }
            return respVO;
        });
    }

    @Mapping(source = "taskDefinitionKey", target = "definitionKey")
    BpmTaskRespVO convert3(HistoricTaskInstance bean);

    BpmTaskRespVO.User convert3(AdminUserRespDTO bean);

    @Mapping(target = "id", ignore = true)
    void copyTo(BpmTaskExtDO from, @MappingTarget BpmTaskDonePageItemRespVO to);

    @Mappings({@Mapping(source = "processInstance.id", target = "id"),
            @Mapping(source = "processInstance.name", target = "name"),
            @Mapping(source = "processInstance.startUserId", target = "startUserId"),
            @Mapping(source = "processInstance.processDefinitionId", target = "processDefinitionId"),
            @Mapping(source = "startUser.nickname", target = "startUserNickname")})
    BpmTaskTodoPageItemRespVO.ProcessInstance convert(HistoricProcessInstance processInstance,
                                                      AdminUserRespDTO startUser);

    default BpmTaskExtDO convert2TaskExt(Task task) {
        BpmTaskExtDO taskExtDO = new BpmTaskExtDO();
        taskExtDO.setTaskId(task.getId());
        taskExtDO.setAssigneeUserId(NumberUtils.parseLong(task.getAssignee()));
        taskExtDO.setName(task.getName());
        taskExtDO.setProcessDefinitionId(task.getProcessDefinitionId());
        taskExtDO.setProcessInstanceId(task.getProcessInstanceId());
        taskExtDO.setCreateTime(task.getCreateTime());
        return taskExtDO;
    }

    default BpmMessageSendWhenTaskCreatedReqDTO convert(ProcessInstance processInstance, AdminUserRespDTO startUser,
                                                        Task task) {
        BpmMessageSendWhenTaskCreatedReqDTO reqDTO = new BpmMessageSendWhenTaskCreatedReqDTO();
        reqDTO.setProcessInstanceId(processInstance.getProcessInstanceId());
        reqDTO.setProcessInstanceName(processInstance.getName());
        reqDTO.setStartUserId(startUser.getId());
        reqDTO.setStartUserNickname(startUser.getNickname());
        reqDTO.setTaskId(task.getId());
        reqDTO.setTaskName(task.getName());
        reqDTO.setAssigneeUserId(NumberUtils.parseLong(task.getAssignee()));
        return reqDTO;
    }

}
