package com.sdps.module.system.service.user;

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


}
