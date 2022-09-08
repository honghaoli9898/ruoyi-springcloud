package com.sdps.module.system.service.permission;

import java.util.List;
import java.util.Map;

import com.sdps.common.model.dataobject.permission.MenuDO;

/**
 * 菜单 Service 接口
 *
 * @author 芋道源码
 */
public interface SysMenuService {

	/**
	 * 初始化菜单的本地缓存
	 */
	void initLocalCache();

	/**
	 * 获得权限对应的菜单数组
	 *
	 * @param permission
	 *            权限标识
	 * @return 数组
	 */
	List<MenuDO> getMenuListByPermissionFromCache(String permission);

	Map<Long, MenuDO> getMenuCache();

}
