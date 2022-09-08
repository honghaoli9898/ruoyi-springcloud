package com.sdps.module.user.service.permission;

import java.util.Collection;
import java.util.List;

import com.sdps.common.model.dataobject.permission.MenuDO;
import com.sdps.module.user.controller.admin.permission.vo.menu.MenuCreateReqVO;
import com.sdps.module.user.controller.admin.permission.vo.menu.MenuListReqVO;
import com.sdps.module.user.controller.admin.permission.vo.menu.MenuUpdateReqVO;

/**
 * 菜单 Service 接口
 *
 * @author 芋道源码
 */
public interface MenuService {

	/**
	 * 创建菜单
	 *
	 * @param reqVO
	 *            菜单信息
	 * @return 创建出来的菜单编号
	 */
	Long createMenu(MenuCreateReqVO reqVO);

	/**
	 * 更新菜单
	 *
	 * @param reqVO
	 *            菜单信息
	 */
	void updateMenu(MenuUpdateReqVO reqVO);

	/**
	 * 删除菜单
	 *
	 * @param id
	 *            菜单编号
	 */
	void deleteMenu(Long id);

	/**
	 * 获得所有菜单列表
	 *
	 * @return 菜单列表
	 */
	List<MenuDO> getMenus();

	/**
	 * 基于租户，筛选菜单列表 注意，如果是系统租户，返回的还是全菜单
	 *
	 * @param reqVO
	 *            筛选条件请求 VO
	 * @return 菜单列表
	 */
	List<MenuDO> getTenantMenus(MenuListReqVO reqVO);

	/**
	 * 筛选菜单列表
	 *
	 * @param reqVO
	 *            筛选条件请求 VO
	 * @return 菜单列表
	 */
	List<MenuDO> getMenus(MenuListReqVO reqVO);

	/**
	 * 获得所有菜单，从缓存中
	 *
	 * 任一参数为空时，则返回为空
	 *
	 * @param menuTypes
	 *            菜单类型数组
	 * @param menusStatuses
	 *            菜单状态数组
	 * @return 菜单列表
	 */
	List<MenuDO> getMenuListFromCache(Collection<Integer> menuTypes,
			Collection<Integer> menusStatuses);

	/**
	 * 获得指定编号的菜单数组，从缓存中
	 *
	 * 任一参数为空时，则返回为空
	 *
	 * @param menuIds
	 *            菜单编号数组
	 * @param menuTypes
	 *            菜单类型数组
	 * @param menusStatuses
	 *            菜单状态数组
	 * @return 菜单数组
	 */
	List<MenuDO> getMenuListFromCache(Collection<Long> menuIds,
			Collection<Integer> menuTypes, Collection<Integer> menusStatuses);

	/**
	 * 获得菜单
	 *
	 * @param id
	 *            菜单编号
	 * @return 菜单
	 */
	MenuDO getMenu(Long id);

}
