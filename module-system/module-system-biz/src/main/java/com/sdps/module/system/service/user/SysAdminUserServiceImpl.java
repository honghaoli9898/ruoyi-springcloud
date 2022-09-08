package com.sdps.module.system.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sdps.common.model.dataobject.user.AdminUserDO;
import com.sdps.module.system.dal.mapper.user.SysAdminUserMapper;

/**
 * 后台用户 Service 实现类
 * 
 * @author 芋道源码
 */
@Service
public class SysAdminUserServiceImpl implements SysAdminUserService {

	@Autowired
	private SysAdminUserMapper userMapper;

	@Override
	public AdminUserDO getUser(Long id) {
		return userMapper.selectById(id);
	}
}
