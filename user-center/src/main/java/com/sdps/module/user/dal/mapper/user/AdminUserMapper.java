package com.sdps.module.user.dal.mapper.user;

import java.util.Collection;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sdps.common.model.dataobject.user.AdminUserDO;
import com.sdps.common.mybatis.core.mapper.BaseMapperX;
import com.sdps.common.mybatis.core.query.LambdaQueryWrapperX;
import com.sdps.common.pojo.PageResult;
import com.sdps.module.user.controller.admin.user.vo.user.UserExportReqVO;
import com.sdps.module.user.controller.admin.user.vo.user.UserPageReqVO;

@Mapper
public interface AdminUserMapper extends BaseMapperX<AdminUserDO> {

	default AdminUserDO selectByUsername(String username) {
		return selectOne(new LambdaQueryWrapper<AdminUserDO>().eq(
				AdminUserDO::getUsername, username));
	}

	default AdminUserDO selectByEmail(String email) {
		return selectOne(new LambdaQueryWrapper<AdminUserDO>().eq(
				AdminUserDO::getEmail, email));
	}

	default AdminUserDO selectByMobile(String mobile) {
		return selectOne(new LambdaQueryWrapper<AdminUserDO>().eq(
				AdminUserDO::getMobile, mobile));
	}

	default PageResult<AdminUserDO> selectPage(UserPageReqVO reqVO,
			Collection<Long> deptIds) {
		return selectPage(
				reqVO,
				new LambdaQueryWrapperX<AdminUserDO>()
						.likeIfPresent(AdminUserDO::getUsername,
								reqVO.getUsername())
						.likeIfPresent(AdminUserDO::getMobile,
								reqVO.getMobile())
						.eqIfPresent(AdminUserDO::getStatus, reqVO.getStatus())
						.betweenIfPresent(AdminUserDO::getCreateTime,
								reqVO.getCreateTime())
						.inIfPresent(AdminUserDO::getDeptId, deptIds)
						.orderByDesc(AdminUserDO::getId));
	}

	default List<AdminUserDO> selectList(UserExportReqVO reqVO,
			Collection<Long> deptIds) {
		return selectList(new LambdaQueryWrapperX<AdminUserDO>()
				.likeIfPresent(AdminUserDO::getUsername, reqVO.getUsername())
				.likeIfPresent(AdminUserDO::getMobile, reqVO.getMobile())
				.eqIfPresent(AdminUserDO::getStatus, reqVO.getStatus())
				.betweenIfPresent(AdminUserDO::getCreateTime,
						reqVO.getCreateTime())
				.inIfPresent(AdminUserDO::getDeptId, deptIds));
	}

	default List<AdminUserDO> selectListByNickname(String nickname) {
		return selectList(new LambdaQueryWrapperX<AdminUserDO>().like(
				AdminUserDO::getNickname, nickname));
	}

	default List<AdminUserDO> selectListByUsername(String username) {
		return selectList(new LambdaQueryWrapperX<AdminUserDO>().like(
				AdminUserDO::getUsername, username));
	}

	default List<AdminUserDO> selectListByStatus(Integer status) {
		return selectList(AdminUserDO::getStatus, status);
	}


}
