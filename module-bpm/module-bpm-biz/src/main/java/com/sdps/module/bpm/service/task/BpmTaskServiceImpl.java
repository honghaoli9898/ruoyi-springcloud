package com.sdps.module.bpm.service.task;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.annotation.Resource;
import javax.validation.Valid;

import lombok.extern.slf4j.Slf4j;

import org.flowable.engine.HistoryService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.api.history.HistoricTaskInstanceQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;

import com.sdps.common.exception.util.ServiceExceptionUtil;
import com.sdps.common.pojo.PageResult;
import com.sdps.common.util.collection.CollectionUtils;
import com.sdps.common.util.number.NumberUtils;
import com.sdps.common.util.object.PageUtils;
import com.sdps.module.bpm.controller.admin.task.vo.task.BpmTaskApproveReqVO;
import com.sdps.module.bpm.controller.admin.task.vo.task.BpmTaskDonePageItemRespVO;
import com.sdps.module.bpm.controller.admin.task.vo.task.BpmTaskDonePageReqVO;
import com.sdps.module.bpm.controller.admin.task.vo.task.BpmTaskRejectReqVO;
import com.sdps.module.bpm.controller.admin.task.vo.task.BpmTaskRespVO;
import com.sdps.module.bpm.controller.admin.task.vo.task.BpmTaskTodoPageItemRespVO;
import com.sdps.module.bpm.controller.admin.task.vo.task.BpmTaskTodoPageReqVO;
import com.sdps.module.bpm.controller.admin.task.vo.task.BpmTaskUpdateAssigneeReqVO;
import com.sdps.module.bpm.convert.task.BpmTaskConvert;
import com.sdps.module.bpm.dal.dataobject.task.BpmTaskExtDO;
import com.sdps.module.bpm.dal.mysql.task.BpmTaskExtMapper;
import com.sdps.module.bpm.enums.ErrorCodeConstants;
import com.sdps.module.bpm.enums.task.BpmProcessInstanceDeleteReasonEnum;
import com.sdps.module.bpm.enums.task.BpmProcessInstanceResultEnum;
import com.sdps.module.bpm.service.message.BpmMessageService;
import com.sdps.module.system.api.dept.DeptApi;
import com.sdps.module.system.api.dept.dto.DeptRespDTO;
import com.sdps.module.system.api.user.AdminUserApi;
import com.sdps.module.system.api.user.dto.AdminUserRespDTO;

/**
 * ?????????????????? Service ?????????
 *
 * @author ????????????
 * @author jason
 */
@Slf4j
@Service
public class BpmTaskServiceImpl implements BpmTaskService {

	@Resource
	private TaskService taskService;
	@Resource
	private HistoryService historyService;

	@Resource
	private BpmProcessInstanceService processInstanceService;
	@Resource
	private AdminUserApi sysAdminUserApi;
	@Resource
	private DeptApi deptApi;
	@Resource
	private BpmTaskExtMapper taskExtMapper;
	@Resource
	private BpmMessageService messageService;
	@Resource
	private AdminUserApi adminUserApi;

	@Override
	public PageResult<BpmTaskTodoPageItemRespVO> getTodoTaskPage(Long userId,
			BpmTaskTodoPageReqVO pageVO) {
		// ??????????????????
		TaskQuery taskQuery = taskService.createTaskQuery()
				.taskAssignee(String.valueOf(userId)) // ???????????????
				.orderByTaskCreateTime().desc(); // ??????????????????
		if (StrUtil.isNotBlank(pageVO.getName())) {
			taskQuery.taskNameLike("%" + pageVO.getName() + "%");
		}
		if (pageVO.getBeginCreateTime() != null) {
			taskQuery.taskCreatedAfter(pageVO.getBeginCreateTime());
		}
		if (pageVO.getEndCreateTime() != null) {
			taskQuery.taskCreatedBefore(pageVO.getEndCreateTime());
		}
		// ????????????
		List<Task> tasks = taskQuery.listPage(PageUtils.getStart(pageVO),
				pageVO.getPageSize());
		if (CollUtil.isEmpty(tasks)) {
			return PageResult.empty(taskQuery.count());
		}

		// ?????? ProcessInstance Map
		Map<String, ProcessInstance> processInstanceMap = processInstanceService
				.getProcessInstanceMap(CollectionUtils.convertSet(tasks,
						Task::getProcessInstanceId));
		// ?????? User Map
		Map<Long, AdminUserRespDTO> userMap = adminUserApi
				.getUserMap(CollectionUtils.convertSet(
						processInstanceMap.values(),
						instance -> Long.valueOf(instance.getStartUserId())));
		// ????????????
		return new PageResult<>(BpmTaskConvert.INSTANCE.convertList1(tasks,
				processInstanceMap, userMap), taskQuery.count());
	}

	@Override
	public PageResult<BpmTaskDonePageItemRespVO> getDoneTaskPage(Long userId,
			BpmTaskDonePageReqVO pageVO) {
		// ??????????????????
		HistoricTaskInstanceQuery taskQuery = historyService
				.createHistoricTaskInstanceQuery().finished() // ?????????
				.taskAssignee(String.valueOf(userId)) // ???????????????
				.orderByHistoricTaskInstanceEndTime().desc(); // ??????????????????
		if (StrUtil.isNotBlank(pageVO.getName())) {
			taskQuery.taskNameLike("%" + pageVO.getName() + "%");
		}
		if (pageVO.getBeginCreateTime() != null) {
			taskQuery.taskCreatedAfter(pageVO.getBeginCreateTime());
		}
		if (pageVO.getEndCreateTime() != null) {
			taskQuery.taskCreatedBefore(pageVO.getEndCreateTime());
		}
		// ????????????
		List<HistoricTaskInstance> tasks = taskQuery.listPage(
				PageUtils.getStart(pageVO), pageVO.getPageSize());
		if (CollUtil.isEmpty(tasks)) {
			return PageResult.empty(taskQuery.count());
		}

		// ?????? TaskExtDO Map
		List<BpmTaskExtDO> bpmTaskExtDOs = taskExtMapper
				.selectListByTaskIds(CollectionUtils.convertSet(tasks,
						HistoricTaskInstance::getId));
		Map<String, BpmTaskExtDO> bpmTaskExtDOMap = CollectionUtils.convertMap(
				bpmTaskExtDOs, BpmTaskExtDO::getTaskId);
		// ?????? ProcessInstance Map
		Map<String, HistoricProcessInstance> historicProcessInstanceMap = processInstanceService
				.getHistoricProcessInstanceMap(CollectionUtils.convertSet(
						tasks, HistoricTaskInstance::getProcessInstanceId));
		// ?????? User Map
		Map<Long, AdminUserRespDTO> userMap = adminUserApi
				.getUserMap(CollectionUtils.convertSet(
						historicProcessInstanceMap.values(),
						instance -> Long.valueOf(instance.getStartUserId())));
		// ????????????
		return new PageResult<>(BpmTaskConvert.INSTANCE.convertList2(tasks,
				bpmTaskExtDOMap, historicProcessInstanceMap, userMap),
				taskQuery.count());
	}

	@Override
	public List<Task> getTasksByProcessInstanceIds(
			List<String> processInstanceIds) {
		if (CollUtil.isEmpty(processInstanceIds)) {
			return Collections.emptyList();
		}
		return taskService.createTaskQuery()
				.processInstanceIdIn(processInstanceIds).list();
	}

	@Override
	public List<BpmTaskRespVO> getTaskListByProcessInstanceId(
			String processInstanceId) {
		// ??????????????????
		List<HistoricTaskInstance> tasks = historyService
				.createHistoricTaskInstanceQuery()
				.processInstanceId(processInstanceId)
				.orderByHistoricTaskInstanceStartTime().desc() // ??????????????????
				.list();
		if (CollUtil.isEmpty(tasks)) {
			return Collections.emptyList();
		}

		// ?????? TaskExtDO Map
		List<BpmTaskExtDO> bpmTaskExtDOs = taskExtMapper
				.selectListByTaskIds(CollectionUtils.convertSet(tasks,
						HistoricTaskInstance::getId));
		Map<String, BpmTaskExtDO> bpmTaskExtDOMap = CollectionUtils.convertMap(
				bpmTaskExtDOs, BpmTaskExtDO::getTaskId);
		// ?????? ProcessInstance Map
		HistoricProcessInstance processInstance = processInstanceService
				.getHistoricProcessInstance(processInstanceId);
		// ?????? User Map
		Set<Long> userIds = CollectionUtils.convertSet(tasks,
				task -> NumberUtils.parseLong(task.getAssignee()));
		userIds.add(NumberUtils.parseLong(processInstance.getStartUserId()));
		Map<Long, AdminUserRespDTO> userMap = adminUserApi.getUserMap(userIds);
		// ?????? Dept Map
		Map<Long, DeptRespDTO> deptMap = deptApi.getDeptMap(CollectionUtils
				.convertSet(userMap.values(), AdminUserRespDTO::getDeptId));

		// ????????????
		return BpmTaskConvert.INSTANCE.convertList3(tasks, bpmTaskExtDOMap,
				processInstance, userMap, deptMap);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void approveTask(Long userId, @Valid BpmTaskApproveReqVO reqVO) {
		// ??????????????????
		Task task = checkTask(userId, reqVO.getId());
		// ????????????????????????
		ProcessInstance instance = processInstanceService
				.getProcessInstance(task.getProcessInstanceId());
		if (instance == null) {
			throw ServiceExceptionUtil
					.exception(ErrorCodeConstants.PROCESS_INSTANCE_NOT_EXISTS);
		}

		// ???????????????????????????
		taskService.complete(task.getId(), instance.getProcessVariables());

		// ??????????????????????????????
		BpmTaskExtDO bpmTaskExtDO = new BpmTaskExtDO();
		bpmTaskExtDO.setTaskId(task.getId());
		bpmTaskExtDO
				.setResult(BpmProcessInstanceResultEnum.APPROVE.getResult());
		bpmTaskExtDO.setReason(reqVO.getReason());
		taskExtMapper.updateByTaskId(bpmTaskExtDO);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void rejectTask(Long userId, @Valid BpmTaskRejectReqVO reqVO) {
		Task task = checkTask(userId, reqVO.getId());
		// ????????????????????????
		ProcessInstance instance = processInstanceService
				.getProcessInstance(task.getProcessInstanceId());
		if (instance == null) {
			throw ServiceExceptionUtil
					.exception(ErrorCodeConstants.PROCESS_INSTANCE_NOT_EXISTS);
		}

		// ??????????????????????????????
		processInstanceService.updateProcessInstanceExtReject(
				instance.getProcessInstanceId(), reqVO.getReason());

		// ?????????????????????????????????
		BpmTaskExtDO bpmTaskExtDO = new BpmTaskExtDO();
		bpmTaskExtDO.setTaskId(task.getId());
		bpmTaskExtDO.setResult(BpmProcessInstanceResultEnum.REJECT.getResult());
		bpmTaskExtDO.setEndTime(new Date());
		bpmTaskExtDO.setReason(reqVO.getReason());
		taskExtMapper.updateByTaskId(bpmTaskExtDO);
	}

	@Override
	public void updateTaskAssignee(Long userId, BpmTaskUpdateAssigneeReqVO reqVO) {
		// ??????????????????
		Task task = checkTask(userId, reqVO.getId());
		// ???????????????
		updateTaskAssignee(task.getId(), reqVO.getAssigneeUserId());
	}

	@Override
	public void updateTaskAssignee(String id, Long userId) {
		taskService.setAssignee(id, String.valueOf(userId));
	}

	/**
	 * ??????????????????????????? ???????????????????????????????????????
	 *
	 * @param userId
	 *            ?????? id
	 * @param taskId
	 *            task id
	 */
	private Task checkTask(Long userId, String taskId) {
		Task task = getTask(taskId);
		if (task == null) {
			throw ServiceExceptionUtil
					.exception(ErrorCodeConstants.TASK_COMPLETE_FAIL_NOT_EXISTS);
		}
		if (!Objects.equals(userId, NumberUtils.parseLong(task.getAssignee()))) {
			throw ServiceExceptionUtil
					.exception(ErrorCodeConstants.TASK_COMPLETE_FAIL_ASSIGN_NOT_SELF);
		}
		return task;
	}

	@Override
	public void createTaskExt(Task task) {
		BpmTaskExtDO taskExtDO = BpmTaskConvert.INSTANCE.convert2TaskExt(task);
		taskExtDO.setResult(BpmProcessInstanceResultEnum.PROCESS.getResult());
		taskExtMapper.insert(taskExtDO);
	}

	@Override
	public void updateTaskExtComplete(Task task) {
		BpmTaskExtDO taskExtDO = BpmTaskConvert.INSTANCE.convert2TaskExt(task);
		taskExtDO.setResult(BpmProcessInstanceResultEnum.APPROVE.getResult()); // ?????????????????????????????????
																				// Complete
																				// ????????????????????????????????????
		taskExtDO.setEndTime(new Date());
		taskExtMapper.updateByTaskId(taskExtDO);
	}

	@Override
	public void updateTaskExtCancel(String taskId) {
		// ??????????????????????????????????????????????????????????????????????????????
		TransactionSynchronizationManager
				.registerSynchronization(new TransactionSynchronization() {

					@Override
					public void afterCommit() {
						// ??????????????????????????????????????????????????????
						HistoricTaskInstance task = getHistoricTask(taskId);
						if (task == null) {
							return;
						}

						// ?????????????????????????????????????????????????????????
						BpmTaskExtDO taskExt = taskExtMapper
								.selectByTaskId(taskId);
						if (taskExt == null) {
							log.error(
									"[updateTaskExtCancel][taskId({}) ????????????????????????????????????????????????]",
									taskId);
							return;
						}
						// ??????????????????????????????????????????
						if (BpmProcessInstanceResultEnum.isEndResult(taskExt
								.getResult())) {
							log.error(
									"[updateTaskExtCancel][taskId({}) ????????????({})?????????????????????]",
									taskId, taskExt.getResult());
							return;
						}

						// ????????????
						BpmTaskExtDO bpmTaskExtDO = new BpmTaskExtDO();
						bpmTaskExtDO.setId(taskExt.getId());
						bpmTaskExtDO
								.setResult(BpmProcessInstanceResultEnum.CANCEL
										.getResult());
						bpmTaskExtDO.setEndTime(new Date());
						bpmTaskExtDO.setReason(BpmProcessInstanceDeleteReasonEnum
								.translateReason(task.getDeleteReason()));
						taskExtMapper.updateById(bpmTaskExtDO);
					}

				});
	}

	@Override
	public void updateTaskExtAssign(Task task) {
		BpmTaskExtDO taskExtDO = new BpmTaskExtDO();
		taskExtDO.setAssigneeUserId(NumberUtils.parseLong(task.getAssignee()));
		taskExtDO.setTaskId(task.getId());
		taskExtMapper.updateByTaskId(taskExtDO);
		// ????????????????????????????????????????????????????????????????????????????????????????????? ProcessInstance?????????????????????????????????????????????????????????
		TransactionSynchronizationManager
				.registerSynchronization(new TransactionSynchronization() {
					@Override
					public void afterCommit() {
						ProcessInstance processInstance = processInstanceService
								.getProcessInstance(task.getProcessInstanceId());
						AdminUserRespDTO startUser = adminUserApi.getUser(Long
								.valueOf(processInstance.getStartUserId()));
						messageService
								.sendMessageWhenTaskAssigned(BpmTaskConvert.INSTANCE
										.convert(processInstance, startUser,
												task));
					}
				});
	}

	private Task getTask(String id) {
		return taskService.createTaskQuery().taskId(id).singleResult();
	}

	private HistoricTaskInstance getHistoricTask(String id) {
		return historyService.createHistoricTaskInstanceQuery().taskId(id)
				.singleResult();
	}

}
