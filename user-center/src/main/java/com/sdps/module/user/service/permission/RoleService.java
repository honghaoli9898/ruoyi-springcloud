package com.sdps.module.user.service.permission;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.validation.Valid;

import org.springframework.lang.Nullable;

import com.sdps.common.model.dataobject.permission.RoleDO;
import com.sdps.common.pojo.PageResult;
import com.sdps.module.user.controller.admin.permission.vo.role.RoleCreateReqVO;
import com.sdps.module.user.controller.admin.permission.vo.role.RoleExportReqVO;
import com.sdps.module.user.controller.admin.permission.vo.role.RolePageReqVO;
import com.sdps.module.user.controller.admin.permission.vo.role.RoleUpdateReqVO;

/**
 * 角色 Service 接口
 *
 * @author 芋道源码
 */
public interface RoleService {

	/**
	 * 创建角色
	 *
	 * @param reqVO
	 *            创建角色信息
	 * @param type
	 *            角色类型
	 * @return 角色编号
	 */
	Long createRole(@Valid RoleCreateReqVO reqVO, Integer type);

	/**
	 * 更新角色
	 *
	 * @param reqVO
	 *            更新角色信息
	 */
	void updateRole(@Valid RoleUpdateReqVO reqVO);

	/**
	 * 删除角色
	 *
	 * @param id
	 *            角色编号
	 */
	void deleteRole(Long id);

	/**
	 * 更新角色状态
	 *
	 * @param id
	 *            角色编号
	 * @param status
	 *            状态
	 */
	void updateRoleStatus(Long id, Integer status);

	/**
	 * 设置角色的数据权限
	 *
	 * @param id
	 *            角色编号
	 * @param dataScope
	 *            数据范围
	 * @param dataScopeDeptIds
	 *            部门编号数组
	 */
	void updateRoleDataScope(Long id, Integer dataScope,
			Set<Long> dataScopeDeptIds);

	/**
	 * 获得角色列表
	 *
	 * @param statuses
	 *            筛选的状态。允许空，空时不筛选
	 * @return 角色列表
	 */
	List<RoleDO> getRoles(@Nullable Collection<Integer> statuses);

	/**
	 * 获得角色
	 *
	 * @param id
	 *            角色编号
	 * @return 角色
	 */
	RoleDO getRole(Long id);

	/**
	 * 获得角色分页
	 *
	 * @param reqVO
	 *            角色分页查询
	 * @return 角色分页结果
	 */
	PageResult<RoleDO> getRolePage(RolePageReqVO reqVO);

	/**
	 * 获得角色列表
	 *
	 * @param reqVO
	 *            列表查询
	 * @return 角色列表
	 */
	List<RoleDO> getRoleList(RoleExportReqVO reqVO);

	/**
	 * 校验角色们是否有效。如下情况，视为无效： 1. 角色编号不存在 2. 角色被禁用
	 *
	 * @param ids
	 *            角色编号数组
	 */
	void validRoles(Collection<Long> ids);

}
