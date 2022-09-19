package com.sdps.module.user.service.dept;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.sdps.common.enums.CommonStatusEnum;
import com.sdps.common.exception.util.ServiceExceptionUtil;
import com.sdps.module.system.dal.dataobject.dept.DeptDO;
import com.sdps.module.system.enums.ErrorCodeConstants;
import com.sdps.module.system.enums.dept.DeptIdEnum;
import com.sdps.module.system.service.dept.SysDeptService;
import com.sdps.module.user.controller.admin.dept.vo.dept.DeptCreateReqVO;
import com.sdps.module.user.controller.admin.dept.vo.dept.DeptListReqVO;
import com.sdps.module.user.controller.admin.dept.vo.dept.DeptUpdateReqVO;
import com.sdps.module.user.convert.dept.DeptConvert;
import com.sdps.module.user.dal.mapper.dept.DeptMapper;
import com.sdps.module.user.mq.producer.dept.DeptProducer;

/**
 * 部门 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class DeptServiceImpl implements DeptService {

	@Autowired
	private DeptMapper deptMapper;

	@Autowired
	private DeptProducer deptProducer;

	@Autowired
	// 注入自己，所以延迟加载
	private SysDeptService self;

	@Override
	public Long createDept(DeptCreateReqVO reqVO) {
		// 校验正确性
		if (reqVO.getParentId() == null) {
			reqVO.setParentId(DeptIdEnum.ROOT.getId());
		}
		checkCreateOrUpdate(null, reqVO.getParentId(), reqVO.getName());
		// 插入部门
		DeptDO dept = DeptConvert.INSTANCE.convert(reqVO);
		deptMapper.insert(dept);
		// 发送刷新消息
		deptProducer.sendDeptRefreshMessage();
		return dept.getId();
	}

	@Override
	public void updateDept(DeptUpdateReqVO reqVO) {
		// 校验正确性
		if (reqVO.getParentId() == null) {
			reqVO.setParentId(DeptIdEnum.ROOT.getId());
		}
		checkCreateOrUpdate(reqVO.getId(), reqVO.getParentId(), reqVO.getName());
		// 更新部门
		DeptDO updateObj = DeptConvert.INSTANCE.convert(reqVO);
		deptMapper.updateById(updateObj);
		// 发送刷新消息
		deptProducer.sendDeptRefreshMessage();
	}

	@Override
	public void deleteDept(Long id) {
		// 校验是否存在
		checkDeptExists(id);
		// 校验是否有子部门
		if (deptMapper.selectCountByParentId(id) > 0) {
			throw ServiceExceptionUtil
					.exception(ErrorCodeConstants.DEPT_EXITS_CHILDREN);
		}
		// 删除部门
		deptMapper.deleteById(id);
		// 发送刷新消息
		deptProducer.sendDeptRefreshMessage();
	}

	@Override
	public List<DeptDO> getSimpleDepts(DeptListReqVO reqVO) {
		return deptMapper.selectList(reqVO);
	}

	private void checkCreateOrUpdate(Long id, Long parentId, String name) {
		// 校验自己存在
		checkDeptExists(id);
		// 校验父部门的有效性
		checkParentDeptEnable(id, parentId);
		// 校验部门名的唯一性
		checkDeptNameUnique(id, parentId, name);
	}

	private void checkParentDeptEnable(Long id, Long parentId) {
		if (parentId == null || DeptIdEnum.ROOT.getId().equals(parentId)) {
			return;
		}
		// 不能设置自己为父部门
		if (parentId.equals(id)) {
			throw ServiceExceptionUtil
					.exception(ErrorCodeConstants.DEPT_PARENT_ERROR);
		}
		// 父岗位不存在
		DeptDO dept = deptMapper.selectById(parentId);
		if (dept == null) {
			throw ServiceExceptionUtil
					.exception(ErrorCodeConstants.DEPT_PARENT_NOT_EXITS);
		}
		// 父部门被禁用
		if (!CommonStatusEnum.ENABLE.getStatus().equals(dept.getStatus())) {
			throw ServiceExceptionUtil
					.exception(ErrorCodeConstants.DEPT_NOT_ENABLE);
		}
		// 父部门不能是原来的子部门
		List<DeptDO> children = self.getDeptsByParentIdFromCache(id, true);
		if (children.stream().anyMatch(dept1 -> dept1.getId().equals(parentId))) {
			throw ServiceExceptionUtil
					.exception(ErrorCodeConstants.DEPT_PARENT_IS_CHILD);
		}
	}

	private void checkDeptExists(Long id) {
		if (id == null) {
			return;
		}
		DeptDO dept = deptMapper.selectById(id);
		if (dept == null) {
			throw ServiceExceptionUtil
					.exception(ErrorCodeConstants.DEPT_NOT_FOUND);
		}
	}

	private void checkDeptNameUnique(Long id, Long parentId, String name) {
		DeptDO menu = deptMapper.selectByParentIdAndName(parentId, name);
		if (menu == null) {
			return;
		}
		// 如果 id 为空，说明不用比较是否为相同 id 的岗位
		if (id == null) {
			throw ServiceExceptionUtil
					.exception(ErrorCodeConstants.DEPT_NAME_DUPLICATE);
		}
		if (!menu.getId().equals(id)) {
			throw ServiceExceptionUtil
					.exception(ErrorCodeConstants.DEPT_NAME_DUPLICATE);
		}
	}

	@Override
	public List<DeptDO> getSimpleDepts(Collection<Long> ids) {
		return deptMapper.selectBatchIds(ids);
	}

}
