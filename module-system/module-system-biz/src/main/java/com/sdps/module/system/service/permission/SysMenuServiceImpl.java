package com.sdps.module.system.service.permission;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.sdps.common.model.dataobject.permission.MenuDO;
import com.sdps.common.util.collection.CollectionUtils;
import com.sdps.module.system.dal.mapper.permission.SysMenuMapper;

/**
 * 菜单 Service 实现
 *
 * @author 芋道源码
 */
@Service
@Slf4j
public class SysMenuServiceImpl implements SysMenuService {

	/**
	 * 定时执行 {@link #schedulePeriodicRefresh()} 的周期 因为已经通过 Redis Pub/Sub
	 * 机制，所以频率不需要高
	 */
	private static final long SCHEDULER_PERIOD = 5 * 60 * 1000L;

	/**
	 * 菜单缓存 key：菜单编号
	 *
	 * 这里声明 volatile 修饰的原因是，每次刷新时，直接修改指向
	 */
	@Getter
	private volatile Map<Long, MenuDO> menuCache;
	/**
	 * 权限与菜单缓存 key：权限 {@link MenuDO#getPermission()} value：MenuDO
	 * 数组，因为一个权限可能对应多个 MenuDO 对象
	 *
	 * 这里声明 volatile 修饰的原因是，每次刷新时，直接修改指向
	 */
	private volatile Multimap<String, MenuDO> permissionMenuCache;
	/**
	 * 缓存菜单的最大更新时间，用于后续的增量轮询，判断是否有更新
	 */
	private volatile Date maxUpdateTime;

	@Autowired
	private SysMenuMapper menuMapper;

	/**
	 * 初始化 {@link #menuCache} 和 {@link #permissionMenuCache} 缓存
	 */
	@Override
	@PostConstruct
	public synchronized void initLocalCache() {
		// 获取菜单列表，如果有更新
		List<MenuDO> menuList = this.loadMenuIfUpdate(maxUpdateTime);
		if (CollUtil.isEmpty(menuList)) {
			return;
		}

		// 构建缓存
		ImmutableMap.Builder<Long, MenuDO> menuCacheBuilder = ImmutableMap
				.builder();
		ImmutableMultimap.Builder<String, MenuDO> permMenuCacheBuilder = ImmutableMultimap
				.builder();
		menuList.forEach(menuDO -> {
			menuCacheBuilder.put(menuDO.getId(), menuDO);
			if (StrUtil.isNotEmpty(menuDO.getPermission())) { // 会存在 permission
																// 为 null 的情况，导致
																// put 报 NPE 异常
				permMenuCacheBuilder.put(menuDO.getPermission(), menuDO);
			}
		});
		menuCache = menuCacheBuilder.build();
		permissionMenuCache = permMenuCacheBuilder.build();
		maxUpdateTime = CollectionUtils.getMaxValue(menuList,
				MenuDO::getUpdateTime);
		log.info("[initLocalCache][缓存菜单，数量为:{}]", menuList.size());
	}

	@Scheduled(fixedDelay = SCHEDULER_PERIOD, initialDelay = SCHEDULER_PERIOD)
	public void schedulePeriodicRefresh() {
		initLocalCache();
	}

	/**
	 * 如果菜单发生变化，从数据库中获取最新的全量菜单。 如果未发生变化，则返回空
	 *
	 * @param maxUpdateTime
	 *            当前菜单的最大更新时间
	 * @return 菜单列表
	 */
	private List<MenuDO> loadMenuIfUpdate(Date maxUpdateTime) {
		// 第一步，判断是否要更新。
		if (maxUpdateTime == null) { // 如果更新时间为空，说明 DB 一定有新数据
			log.info("[loadMenuIfUpdate][首次加载全量菜单]");
		} else { // 判断数据库中是否有更新的菜单
			if (menuMapper.selectCountByUpdateTimeGt(maxUpdateTime) == 0) {
				return null;
			}
			log.info("[loadMenuIfUpdate][增量加载全量菜单]");
		}
		// 第二步，如果有更新，则从数据库加载所有菜单
		return menuMapper.selectList();
	}

	@Override
	public List<MenuDO> getMenuListByPermissionFromCache(String permission) {
		return new ArrayList<>(permissionMenuCache.get(permission));
	}

}
