package com.sdps.module.user.service.permission;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import cn.hutool.core.collection.CollUtil;

import com.google.common.annotations.VisibleForTesting;
import com.sdps.common.exception.util.ServiceExceptionUtil;
import com.sdps.common.model.dataobject.permission.MenuDO;
import com.sdps.common.util.collection.CollectionUtils;
import com.sdps.module.system.enums.ErrorCodeConstants;
import com.sdps.module.system.enums.permission.MenuIdEnum;
import com.sdps.module.system.enums.permission.MenuTypeEnum;
import com.sdps.module.system.service.permission.SysMenuService;
import com.sdps.module.user.controller.admin.permission.vo.menu.MenuCreateReqVO;
import com.sdps.module.user.controller.admin.permission.vo.menu.MenuListReqVO;
import com.sdps.module.user.controller.admin.permission.vo.menu.MenuUpdateReqVO;
import com.sdps.module.user.convert.permission.MenuConvert;
import com.sdps.module.user.dal.mapper.permission.MenuMapper;
import com.sdps.module.user.mq.producer.permission.MenuProducer;
import com.sdps.module.user.service.tenant.TenantService;

/**
 * 菜单 Service 实现
 *
 * @author 芋道源码
 */
@Service
public class MenuServiceImpl implements MenuService {

	@Autowired
	private MenuMapper menuMapper;
	@Autowired
	private PermissionService permissionService;
	@Autowired
	@Lazy
	// 延迟，避免循环依赖报错
	private TenantService tenantService;

	@Autowired
	private MenuProducer menuProducer;

	@Autowired
	@Lazy
	private SysMenuService self;

	@Override
	public Long createMenu(MenuCreateReqVO reqVO) {
		// 校验父菜单存在
		checkParentResource(reqVO.getParentId(), null);
		// 校验菜单（自己）
		checkResource(reqVO.getParentId(), reqVO.getName(), null);
		// 插入数据库
		MenuDO menu = MenuConvert.INSTANCE.convert(reqVO);
		initMenuProperty(menu);
		menuMapper.insert(menu);
		// 发送刷新消息
		menuProducer.sendMenuRefreshMessage();
		// 返回
		return menu.getId();
	}

	@Override
	public void updateMenu(MenuUpdateReqVO reqVO) {
		// 校验更新的菜单是否存在
		if (menuMapper.selectById(reqVO.getId()) == null) {
			throw ServiceExceptionUtil
					.exception(ErrorCodeConstants.MENU_NOT_EXISTS);
		}
		// 校验父菜单存在
		checkParentResource(reqVO.getParentId(), reqVO.getId());
		// 校验菜单（自己）
		checkResource(reqVO.getParentId(), reqVO.getName(), reqVO.getId());
		// 更新到数据库
		MenuDO updateObject = MenuConvert.INSTANCE.convert(reqVO);
		initMenuProperty(updateObject);
		menuMapper.updateById(updateObject);
		// 发送刷新消息
		menuProducer.sendMenuRefreshMessage();
	}

	/**
	 * 删除菜单
	 *
	 * @param menuId
	 *            菜单编号
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public void deleteMenu(Long menuId) {
		// 校验是否还有子菜单
		if (menuMapper.selectCountByParentId(menuId) > 0) {
			throw ServiceExceptionUtil
					.exception(ErrorCodeConstants.MENU_EXISTS_CHILDREN);
		}
		// 校验删除的菜单是否存在
		if (menuMapper.selectById(menuId) == null) {
			throw ServiceExceptionUtil
					.exception(ErrorCodeConstants.MENU_NOT_EXISTS);
		}
		// 标记删除
		menuMapper.deleteById(menuId);
		// 删除授予给角色的权限
		permissionService.processMenuDeleted(menuId);
		// 发送刷新消息. 注意，需要事务提交后，在进行发送刷新消息。不然 db 还未提交，结果缓存先刷新了
		TransactionSynchronizationManager
				.registerSynchronization(new TransactionSynchronization() {

					@Override
					public void afterCommit() {
						menuProducer.sendMenuRefreshMessage();
					}

				});
	}

	@Override
	public List<MenuDO> getMenus() {
		return menuMapper.selectList();
	}

	@Override
	public List<MenuDO> getTenantMenus(MenuListReqVO reqVO) {
		List<MenuDO> menus = getMenus(reqVO);
		// 开启多租户的情况下，需要过滤掉未开通的菜单
		tenantService.handleTenantMenu(menuIds -> menus
				.removeIf(menu -> !CollUtil.contains(menuIds, menu.getId())));
		return menus;
	}

	@Override
	public List<MenuDO> getMenus(MenuListReqVO reqVO) {
		return menuMapper.selectList(reqVO);
	}

	@Override
	public List<MenuDO> getMenuListFromCache(Collection<Integer> menuTypes,
			Collection<Integer> menusStatuses) {
		// 任一一个参数为空，则返回空
		if (CollectionUtils.isAnyEmpty(menuTypes, menusStatuses)) {
			return Collections.emptyList();
		}
		// 创建新数组，避免缓存被修改
		return self
				.getMenuCache()
				.values()
				.stream()
				.filter(menu -> menuTypes.contains(menu.getType())
						&& menusStatuses.contains(menu.getStatus()))
				.collect(Collectors.toList());
	}

	@Override
	public List<MenuDO> getMenuListFromCache(Collection<Long> menuIds,
			Collection<Integer> menuTypes, Collection<Integer> menusStatuses) {
		// 任一一个参数为空，则返回空
		if (CollectionUtils.isAnyEmpty(menuIds, menuTypes, menusStatuses)) {
			return Collections.emptyList();
		}
		return self
				.getMenuCache()
				.values()
				.stream()
				.filter(menu -> menuIds.contains(menu.getId())
						&& menuTypes.contains(menu.getType())
						&& menusStatuses.contains(menu.getStatus()))
				.collect(Collectors.toList());
	}

	@Override
	public MenuDO getMenu(Long id) {
		return menuMapper.selectById(id);
	}

	/**
	 * 校验父菜单是否合法
	 *
	 * 1. 不能设置自己为父菜单 2. 父菜单不存在 3. 父菜单必须是 {@link MenuTypeEnum#MENU} 菜单类型
	 *
	 * @param parentId
	 *            父菜单编号
	 * @param childId
	 *            当前菜单编号
	 */
	@VisibleForTesting
	public void checkParentResource(Long parentId, Long childId) {
		if (parentId == null || MenuIdEnum.ROOT.getId().equals(parentId)) {
			return;
		}
		// 不能设置自己为父菜单
		if (parentId.equals(childId)) {
			throw ServiceExceptionUtil
					.exception(ErrorCodeConstants.MENU_PARENT_ERROR);
		}
		MenuDO menu = menuMapper.selectById(parentId);
		// 父菜单不存在
		if (menu == null) {
			throw ServiceExceptionUtil
					.exception(ErrorCodeConstants.MENU_PARENT_NOT_EXISTS);
		}
		// 父菜单必须是目录或者菜单类型
		if (!MenuTypeEnum.DIR.getType().equals(menu.getType())
				&& !MenuTypeEnum.MENU.getType().equals(menu.getType())) {
			throw ServiceExceptionUtil
					.exception(ErrorCodeConstants.MENU_PARENT_NOT_DIR_OR_MENU);
		}
	}

	/**
	 * 校验菜单是否合法
	 *
	 * 1. 校验相同父菜单编号下，是否存在相同的菜单名
	 *
	 * @param name
	 *            菜单名字
	 * @param parentId
	 *            父菜单编号
	 * @param id
	 *            菜单编号
	 */
	@VisibleForTesting
	public void checkResource(Long parentId, String name, Long id) {
		MenuDO menu = menuMapper.selectByParentIdAndName(parentId, name);
		if (menu == null) {
			return;
		}
		// 如果 id 为空，说明不用比较是否为相同 id 的菜单
		if (id == null) {
			throw ServiceExceptionUtil
					.exception(ErrorCodeConstants.MENU_NAME_DUPLICATE);
		}
		if (!menu.getId().equals(id)) {
			throw ServiceExceptionUtil
					.exception(ErrorCodeConstants.MENU_NAME_DUPLICATE);
		}
	}

	/**
	 * 初始化菜单的通用属性。
	 *
	 * 例如说，只有目录或者菜单类型的菜单，才设置 icon
	 *
	 * @param menu
	 *            菜单
	 */
	private void initMenuProperty(MenuDO menu) {
		// 菜单为按钮类型时，无需 component、icon、path 属性，进行置空
		if (MenuTypeEnum.BUTTON.getType().equals(menu.getType())) {
			menu.setComponent("");
			menu.setIcon("");
			menu.setPath("");
		}
	}

}
