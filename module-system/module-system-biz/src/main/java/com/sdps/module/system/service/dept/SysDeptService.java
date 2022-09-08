package com.sdps.module.system.service.dept;

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

}
