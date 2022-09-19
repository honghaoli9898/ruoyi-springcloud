package com.sdps.module.system.service.dept;

import java.util.Collection;
import java.util.List;

import com.sdps.module.system.dal.dataobject.dept.DeptDO;

/**
 * 部门 Service 接口
 *
 * @author 芋道源码
 */
public interface SysDeptService {

	/**
	 * 初始化部门的本地缓存
	 */
	void initLocalCache();

	/**
	 * 获得所有子部门，从缓存中
	 *
	 * @param parentId
	 *            部门编号
	 * @param recursive
	 *            是否递归获取所有
	 * @return 子部门列表
	 */
	List<DeptDO> getDeptsByParentIdFromCache(Long parentId, boolean recursive);

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
}
