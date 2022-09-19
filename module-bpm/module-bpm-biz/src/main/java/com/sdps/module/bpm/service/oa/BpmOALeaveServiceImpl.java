package com.sdps.module.bpm.service.oa;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import cn.hutool.core.date.DateUtil;

import com.sdps.common.exception.util.ServiceExceptionUtil;
import com.sdps.common.pojo.PageResult;
import com.sdps.module.bpm.api.task.BpmProcessInstanceApi;
import com.sdps.module.bpm.api.task.dto.BpmProcessInstanceCreateReqDTO;
import com.sdps.module.bpm.controller.admin.oa.vo.BpmOALeaveCreateReqVO;
import com.sdps.module.bpm.controller.admin.oa.vo.BpmOALeavePageReqVO;
import com.sdps.module.bpm.convert.oa.BpmOALeaveConvert;
import com.sdps.module.bpm.dal.dataobject.oa.BpmOALeaveDO;
import com.sdps.module.bpm.dal.mysql.oa.BpmOALeaveMapper;
import com.sdps.module.bpm.enums.ErrorCodeConstants;
import com.sdps.module.bpm.enums.task.BpmProcessInstanceResultEnum;

/**
 * OA 请假申请 Service 实现类
 *
 * @author jason
 * @author 芋道源码
 */
@Service
@Validated
public class BpmOALeaveServiceImpl implements BpmOALeaveService {

	/**
	 * OA 请假对应的流程定义 KEY
	 */
	public static final String PROCESS_KEY = "oa_leave";

	@Resource
	private BpmOALeaveMapper leaveMapper;

	@Resource
	private BpmProcessInstanceApi processInstanceApi;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Long createLeave(Long userId, BpmOALeaveCreateReqVO createReqVO) {
		// 插入 OA 请假单
		long day = DateUtil.betweenDay(createReqVO.getStartTime(),
				createReqVO.getEndTime(), false);
		BpmOALeaveDO leave = BpmOALeaveConvert.INSTANCE.convert(createReqVO);
		leave.setUserId(userId);
		leave.setDay(day);
		leave.setResult(BpmProcessInstanceResultEnum.PROCESS.getResult());
		leaveMapper.insert(leave);

		// 发起 BPM 流程
		Map<String, Object> processInstanceVariables = new HashMap<>();
		processInstanceVariables.put("day", day);
		BpmProcessInstanceCreateReqDTO bpmProcessInstanceCreateReqDTO = new BpmProcessInstanceCreateReqDTO();
		bpmProcessInstanceCreateReqDTO.setProcessDefinitionKey(PROCESS_KEY);
		bpmProcessInstanceCreateReqDTO.setVariables(processInstanceVariables);
		bpmProcessInstanceCreateReqDTO.setBusinessKey(String.valueOf(leave
				.getId()));
		String processInstanceId = processInstanceApi.createProcessInstance(
				userId, bpmProcessInstanceCreateReqDTO);
		BpmOALeaveDO bpmOALeaveDO = new BpmOALeaveDO();
		bpmOALeaveDO.setId(leave.getId());
		bpmOALeaveDO.setProcessInstanceId(processInstanceId);
		// 将工作流的编号，更新到 OA 请假单中
		leaveMapper.updateById(bpmOALeaveDO);
		return leave.getId();
	}

	@Override
	public void updateLeaveResult(Long id, Integer result) {
		validateLeaveExists(id);
		BpmOALeaveDO bpmOALeaveDO = new BpmOALeaveDO();
		bpmOALeaveDO.setId(id);
		bpmOALeaveDO.setResult(result);
		leaveMapper.updateById(bpmOALeaveDO);
	}

	private void validateLeaveExists(Long id) {
		if (leaveMapper.selectById(id) == null) {
			throw ServiceExceptionUtil
					.exception(ErrorCodeConstants.OA_LEAVE_NOT_EXISTS);
		}
	}

	@Override
	public BpmOALeaveDO getLeave(Long id) {
		return leaveMapper.selectById(id);
	}

	@Override
	public PageResult<BpmOALeaveDO> getLeavePage(Long userId,
			BpmOALeavePageReqVO pageReqVO) {
		return leaveMapper.selectPage(userId, pageReqVO);
	}

}
