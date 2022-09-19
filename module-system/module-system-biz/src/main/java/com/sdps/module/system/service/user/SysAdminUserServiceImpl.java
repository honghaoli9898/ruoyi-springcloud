package com.sdps.module.system.service.user;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.hutool.core.collection.CollUtil;

import com.sdps.common.enums.CommonStatusEnum;
import com.sdps.common.exception.util.ServiceExceptionUtil;
import com.sdps.common.model.dataobject.user.AdminUserDO;
import com.sdps.common.util.collection.CollectionUtils;
import com.sdps.module.system.dal.dataobject.dept.UserPostDO;
import com.sdps.module.system.dal.mapper.dept.SysUserPostMapper;
import com.sdps.module.system.dal.mapper.user.SysAdminUserMapper;
import com.sdps.module.system.enums.ErrorCodeConstants;

/**
 * 后台用户 Service 实现类
 * 
 * @author 芋道源码
 */
@Service
public class SysAdminUserServiceImpl implements SysAdminUserService {

	@Autowired
	private SysAdminUserMapper sysAdminUserMapper;
	@Autowired
	private SysUserPostMapper sysUserPostMapper;

	@Override
	public AdminUserDO getUser(Long id) {
		return sysAdminUserMapper.selectById(id);
	}

	@Override
	public List<AdminUserDO> getUsersByDeptIds(Collection<Long> deptIds) {
		if (CollUtil.isEmpty(deptIds)) {
			return Collections.emptyList();
		}
		return sysAdminUserMapper.selectListByDeptIds(deptIds);
	}

	@Override
	public List<AdminUserDO> getUsersByPostIds(Collection<Long> postIds) {
		if (CollUtil.isEmpty(postIds)) {
			return Collections.emptyList();
		}
		Set<Long> userIds = CollectionUtils.convertSet(
				sysUserPostMapper.selectListByPostIds(postIds),
				UserPostDO::getUserId);
		if (CollUtil.isEmpty(userIds)) {
			return Collections.emptyList();
		}
		return sysAdminUserMapper.selectBatchIds(userIds);
	}

	@Override
	public List<AdminUserDO> getUsers(Collection<Long> ids) {
		if (CollUtil.isEmpty(ids)) {
			return Collections.emptyList();
		}
		return sysAdminUserMapper.selectBatchIds(ids);
	}

	@Override
	public void validUsers(Set<Long> ids) {
		if (CollUtil.isEmpty(ids)) {
			return;
		}
		// 获得岗位信息
		List<AdminUserDO> users = sysAdminUserMapper.selectBatchIds(ids);
		Map<Long, AdminUserDO> userMap = CollectionUtils.convertMap(users,
				AdminUserDO::getId);
		// 校验
		ids.forEach(id -> {
			AdminUserDO user = userMap.get(id);
			if (user == null) {
				throw ServiceExceptionUtil
						.exception(ErrorCodeConstants.USER_NOT_EXISTS);
			}
			if (!CommonStatusEnum.ENABLE.getStatus().equals(user.getStatus())) {
				throw ServiceExceptionUtil.exception(
						ErrorCodeConstants.USER_IS_DISABLE, user.getNickname());
			}
		});
	}
}
