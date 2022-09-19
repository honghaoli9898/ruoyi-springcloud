package com.sdps.module.system.service.user;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.sdps.common.model.dataobject.user.AdminUserDO;

/**
 * 后台用户 Service 接口
 *
 * @author 芋道源码
 */
public interface SysAdminUserService {

	/**
	 * 通过用户 ID 查询用户
	 *
	 * @param id
	 *            用户ID
	 * @return 用户对象信息
	 */
	AdminUserDO getUser(Long id);

	/**
	 * 获得指定部门的用户数组
	 *
	 * @param deptIds
	 *            部门数组
	 * @return 用户数组
	 */
	List<AdminUserDO> getUsersByDeptIds(Collection<Long> deptIds);

	/**
	 * 获得指定岗位的用户数组
	 *
	 * @param postIds
	 *            岗位数组
	 * @return 用户数组
	 */
	List<AdminUserDO> getUsersByPostIds(Collection<Long> postIds);

	/**
	 * 获得用户列表
	 *
	 * @param ids
	 *            用户编号数组
	 * @return 用户列表
	 */
	List<AdminUserDO> getUsers(Collection<Long> ids);

	/**
	 * 校验用户们是否有效。如下情况，视为无效： 1. 用户编号不存在 2. 用户被禁用
	 *
	 * @param ids
	 *            用户编号数组
	 */
	void validUsers(Set<Long> ids);

}
