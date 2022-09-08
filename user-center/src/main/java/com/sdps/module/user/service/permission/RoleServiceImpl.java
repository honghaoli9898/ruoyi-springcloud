package com.sdps.module.user.service.permission;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StringUtils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;

import com.google.common.annotations.VisibleForTesting;
import com.sdps.common.enums.CommonStatusEnum;
import com.sdps.common.exception.util.ServiceExceptionUtil;
import com.sdps.common.model.dataobject.permission.RoleDO;
import com.sdps.common.pojo.PageResult;
import com.sdps.common.util.collection.CollectionUtils;
import com.sdps.module.system.enums.ErrorCodeConstants;
import com.sdps.module.system.enums.permission.DataScopeEnum;
import com.sdps.module.system.enums.permission.RoleCodeEnum;
import com.sdps.module.system.enums.permission.RoleTypeEnum;
import com.sdps.module.user.controller.admin.permission.vo.role.RoleCreateReqVO;
import com.sdps.module.user.controller.admin.permission.vo.role.RoleExportReqVO;
import com.sdps.module.user.controller.admin.permission.vo.role.RolePageReqVO;
import com.sdps.module.user.controller.admin.permission.vo.role.RoleUpdateReqVO;
import com.sdps.module.user.convert.permission.RoleConvert;
import com.sdps.module.user.dal.mapper.permission.RoleMapper;
import com.sdps.module.user.mq.producer.permission.RoleProducer;

/**
 * 角色 Service 实现类
 *
 * @author 芋道源码
 */
@Service
public class RoleServiceImpl implements RoleService {

	@Autowired
	private PermissionService permissionService;

	@Autowired
	private RoleMapper roleMapper;

	@Autowired
	private RoleProducer roleProducer;

	@Override
	@Transactional
	public Long createRole(RoleCreateReqVO reqVO, Integer type) {
		// 校验角色
		checkDuplicateRole(reqVO.getName(), reqVO.getCode(), null);
		// 插入到数据库
		RoleDO role = RoleConvert.INSTANCE.convert(reqVO);
		role.setType(ObjectUtil.defaultIfNull(type,
				RoleTypeEnum.CUSTOM.getType()));
		role.setStatus(CommonStatusEnum.ENABLE.getStatus());
		role.setDataScope(DataScopeEnum.ALL.getScope()); // 默认可查看所有数据。原因是，可能一些项目不需要项目权限
		roleMapper.insert(role);
		// 发送刷新消息
		TransactionSynchronizationManager
				.registerSynchronization(new TransactionSynchronization() {
					@Override
					public void afterCommit() {
						roleProducer.sendRoleRefreshMessage();
					}
				});
		// 返回
		return role.getId();
	}

	@Override
	public void updateRole(RoleUpdateReqVO reqVO) {
		// 校验是否可以更新
		checkUpdateRole(reqVO.getId());
		// 校验角色的唯一字段是否重复
		checkDuplicateRole(reqVO.getName(), reqVO.getCode(), reqVO.getId());

		// 更新到数据库
		RoleDO updateObject = RoleConvert.INSTANCE.convert(reqVO);
		roleMapper.updateById(updateObject);
		// 发送刷新消息
		roleProducer.sendRoleRefreshMessage();
	}

	@Override
	public void updateRoleStatus(Long id, Integer status) {
		// 校验是否可以更新
		checkUpdateRole(id);
		// 更新状态
		RoleDO updateObject = new RoleDO();
		updateObject.setId(id);
		updateObject.setStatus(status);
		roleMapper.updateById(updateObject);
		// 发送刷新消息
		roleProducer.sendRoleRefreshMessage();
	}

	@Override
	public void updateRoleDataScope(Long id, Integer dataScope,
			Set<Long> dataScopeDeptIds) {
		// 校验是否可以更新
		checkUpdateRole(id);
		// 更新数据范围
		RoleDO updateObject = new RoleDO();
		updateObject.setId(id);
		updateObject.setDataScope(dataScope);
		updateObject.setDataScopeDeptIds(dataScopeDeptIds);
		roleMapper.updateById(updateObject);
		// 发送刷新消息
		roleProducer.sendRoleRefreshMessage();
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteRole(Long id) {
		// 校验是否可以更新
		this.checkUpdateRole(id);
		// 标记删除
		roleMapper.deleteById(id);
		// 删除相关数据
		permissionService.processRoleDeleted(id);
		// 发送刷新消息. 注意，需要事务提交后，在进行发送刷新消息。不然 db 还未提交，结果缓存先刷新了
		TransactionSynchronizationManager
				.registerSynchronization(new TransactionSynchronization() {

					@Override
					public void afterCommit() {
						roleProducer.sendRoleRefreshMessage();
					}

				});
	}

	@Override
	public List<RoleDO> getRoles(@Nullable Collection<Integer> statuses) {
		if (CollUtil.isEmpty(statuses)) {
			return roleMapper.selectList();
		}
		return roleMapper.selectListByStatus(statuses);
	}

	@Override
	public RoleDO getRole(Long id) {
		return roleMapper.selectById(id);
	}

	@Override
	public PageResult<RoleDO> getRolePage(RolePageReqVO reqVO) {
		return roleMapper.selectPage(reqVO);
	}

	@Override
	public List<RoleDO> getRoleList(RoleExportReqVO reqVO) {
		return roleMapper.selectList(reqVO);
	}

	/**
	 * 校验角色的唯一字段是否重复
	 *
	 * 1. 是否存在相同名字的角色 2. 是否存在相同编码的角色
	 *
	 * @param name
	 *            角色名字
	 * @param code
	 *            角色额编码
	 * @param id
	 *            角色编号
	 */
	@VisibleForTesting
	public void checkDuplicateRole(String name, String code, Long id) {
		// 0. 超级管理员，不允许创建
		if (RoleCodeEnum.isSuperAdmin(code)) {
			throw ServiceExceptionUtil.exception(
					ErrorCodeConstants.ROLE_ADMIN_CODE_ERROR, code);
		}
		// 1. 该 name 名字被其它角色所使用
		RoleDO role = roleMapper.selectByName(name);
		if (role != null && !role.getId().equals(id)) {
			throw ServiceExceptionUtil.exception(
					ErrorCodeConstants.ROLE_NAME_DUPLICATE, name);
		}
		// 2. 是否存在相同编码的角色
		if (!StringUtils.hasText(code)) {
			return;
		}
		// 该 code 编码被其它角色所使用
		role = roleMapper.selectByCode(code);
		if (role != null && !role.getId().equals(id)) {
			throw ServiceExceptionUtil.exception(
					ErrorCodeConstants.ROLE_CODE_DUPLICATE, code);
		}
	}

	/**
	 * 校验角色是否可以被更新
	 *
	 * @param id
	 *            角色编号
	 */
	@VisibleForTesting
	public void checkUpdateRole(Long id) {
		RoleDO roleDO = roleMapper.selectById(id);
		if (roleDO == null) {
			throw ServiceExceptionUtil
					.exception(ErrorCodeConstants.ROLE_NOT_EXISTS);
		}
		// 内置角色，不允许删除
		if (RoleTypeEnum.SYSTEM.getType().equals(roleDO.getType())) {
			throw ServiceExceptionUtil
					.exception(ErrorCodeConstants.ROLE_CAN_NOT_UPDATE_SYSTEM_TYPE_ROLE);
		}
	}

	@Override
	public void validRoles(Collection<Long> ids) {
		if (CollUtil.isEmpty(ids)) {
			return;
		}
		// 获得角色信息
		List<RoleDO> roles = roleMapper.selectBatchIds(ids);
		Map<Long, RoleDO> roleMap = CollectionUtils.convertMap(roles,
				RoleDO::getId);
		// 校验
		ids.forEach(id -> {
			RoleDO role = roleMap.get(id);
			if (role == null) {
				throw ServiceExceptionUtil
						.exception(ErrorCodeConstants.ROLE_NOT_EXISTS);
			}
			if (!CommonStatusEnum.ENABLE.getStatus().equals(role.getStatus())) {
				throw ServiceExceptionUtil.exception(
						ErrorCodeConstants.ROLE_IS_DISABLE, role.getName());
			}
		});
	}
}
