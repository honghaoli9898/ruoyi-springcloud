package com.sdps.module.system.service.permission;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;

import com.sdps.common.model.dataobject.permission.RoleDO;
import com.sdps.common.tenant.core.aop.TenantIgnore;
import com.sdps.common.util.collection.CollectionUtils;
import com.sdps.module.system.dal.mapper.permission.SysRoleMapper;
import com.sdps.module.system.enums.permission.RoleCodeEnum;

/**
 * 角色 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Slf4j
public class SysRoleServiceImpl implements SysRoleService {

	/**
	 * 定时执行 {@link #schedulePeriodicRefresh()} 的周期 因为已经通过 Redis Pub/Sub
	 * 机制，所以频率不需要高
	 */
	private static final long SCHEDULER_PERIOD = 5 * 60 * 1000L;

	/**
	 * 角色缓存 key：角色编号 {@link RoleDO#getId()}
	 *
	 * 这里声明 volatile 修饰的原因是，每次刷新时，直接修改指向
	 */
	@Getter
	private volatile Map<Long, RoleDO> roleCache;
	/**
	 * 缓存角色的最大更新时间，用于后续的增量轮询，判断是否有更新
	 */
	@Getter
	private volatile Date maxUpdateTime;

	@Autowired
	private SysRoleMapper roleMapper;

	@Autowired
	@Lazy
	// 注入自己，所以延迟加载
	private SysRoleService self;

	/**
	 * 初始化 {@link #roleCache} 缓存
	 */
	@Override
	@PostConstruct
	@TenantIgnore
	// 忽略自动多租户，全局初始化缓存
	public void initLocalCache() {
		// 获取角色列表，如果有更新
		List<RoleDO> roleList = loadRoleIfUpdate(maxUpdateTime);
		if (CollUtil.isEmpty(roleList)) {
			return;
		}

		// 写入缓存
		roleCache = CollectionUtils.convertMap(roleList, RoleDO::getId);
		maxUpdateTime = CollectionUtils.getMaxValue(roleList,
				RoleDO::getUpdateTime);
		log.info("[initLocalCache][初始化 Role 数量为 {}]", roleList.size());
	}

	@Scheduled(fixedDelay = SCHEDULER_PERIOD, initialDelay = SCHEDULER_PERIOD)
	public void schedulePeriodicRefresh() {
		self.initLocalCache();
	}

	/**
	 * 如果角色发生变化，从数据库中获取最新的全量角色。 如果未发生变化，则返回空
	 *
	 * @param maxUpdateTime
	 *            当前角色的最大更新时间
	 * @return 角色列表
	 */
	private List<RoleDO> loadRoleIfUpdate(Date maxUpdateTime) {
		// 第一步，判断是否要更新。
		if (maxUpdateTime == null) { // 如果更新时间为空，说明 DB 一定有新数据
			log.info("[loadRoleIfUpdate][首次加载全量角色]");
		} else { // 判断数据库中是否有更新的角色
			if (roleMapper.selectCountByUpdateTimeGt(maxUpdateTime) == 0) {
				return null;
			}
			log.info("[loadRoleIfUpdate][增量加载全量角色]");
		}
		// 第二步，如果有更新，则从数据库加载所有角色
		return roleMapper.selectList();
	}

	@Override
	public List<RoleDO> getRolesFromCache(Collection<Long> ids) {
		if (CollectionUtil.isEmpty(ids)) {
			return Collections.emptyList();
		}
		return roleCache.values().stream()
				.filter(roleDO -> ids.contains(roleDO.getId()))
				.collect(Collectors.toList());
	}

	@Override
	public boolean hasAnySuperAdmin(Collection<RoleDO> roleList) {
		if (CollectionUtil.isEmpty(roleList)) {
			return false;
		}
		return roleList.stream().anyMatch(
				role -> RoleCodeEnum.isSuperAdmin(role.getCode()));
	}

	@Override
	public RoleDO getRoleFromCache(Long id) {
		return roleCache.get(id);
	}
}
