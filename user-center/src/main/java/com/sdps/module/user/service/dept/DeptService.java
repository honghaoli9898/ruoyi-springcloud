package com.sdps.module.user.service.dept;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import cn.hutool.core.collection.CollUtil;

import com.sdps.common.util.collection.CollectionUtils;
import com.sdps.module.system.dal.dataobject.dept.DeptDO;
import com.sdps.module.user.controller.admin.dept.vo.dept.DeptCreateReqVO;
import com.sdps.module.user.controller.admin.dept.vo.dept.DeptListReqVO;
import com.sdps.module.user.controller.admin.dept.vo.dept.DeptUpdateReqVO;

/**
 * 部门 Service 接口
 *
 * @author 芋道源码
 */
public interface DeptService {

	/**
	 * 创建部门
	 *
	 * @param reqVO
	 *            部门信息
	 * @return 部门编号
	 */
	Long createDept(DeptCreateReqVO reqVO);

	/**
	 * 更新部门
	 *
	 * @param reqVO
	 *            部门信息
	 */
	void updateDept(DeptUpdateReqVO reqVO);

	/**
	 * 删除部门
	 *
	 * @param id
	 *            部门编号
	 */
	void deleteDept(Long id);

	/**
	 * 筛选部门列表
	 *
	 * @param reqVO
	 *            筛选条件请求 VO
	 * @return 部门列表
	 */
	List<DeptDO> getSimpleDepts(DeptListReqVO reqVO);

	/**
	 * 获得部门信息数组
	 *
	 * @param ids
	 *            部门编号数组
	 * @return 部门信息数组
	 */
	List<DeptDO> getDepts(Collection<Long> ids);

	/**
	 * 获得部门信息
	 *
	 * @param id
	 *            部门编号
	 * @return 部门信息
	 */
	DeptDO getDept(Long id);

	/**
	 * 校验部门们是否有效。如下情况，视为无效： 1. 部门编号不存在 2. 部门被禁用
	 *
	 * @param ids
	 *            角色编号数组
	 */
	void validDepts(Collection<Long> ids);

	/**
	 * 获得指定编号的部门列表
	 *
	 * @param ids
	 *            部门编号数组
	 * @return 部门列表
	 */
	List<DeptDO> getSimpleDepts(Collection<Long> ids);

	/**
	 * 获得指定编号的部门 Map
	 *
	 * @param ids
	 *            部门编号数组
	 * @return 部门 Map
	 */
	default Map<Long, DeptDO> getDeptMap(Collection<Long> ids) {
		if (CollUtil.isEmpty(ids)) {
			return Collections.emptyMap();
		}
		List<DeptDO> list = getSimpleDepts(ids);
		return CollectionUtils.convertMap(list, DeptDO::getId);
	}
}
