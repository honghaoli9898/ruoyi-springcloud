package com.sdps.module.system.service.dept;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import cn.hutool.core.collection.CollUtil;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.sdps.common.enums.CommonStatusEnum;
import com.sdps.common.exception.util.ServiceExceptionUtil;
import com.sdps.common.tenant.core.aop.TenantIgnore;
import com.sdps.common.util.collection.CollectionUtils;
import com.sdps.module.system.dal.dataobject.dept.DeptDO;
import com.sdps.module.system.dal.mapper.dept.SysDeptMapper;
import com.sdps.module.system.enums.ErrorCodeConstants;

/**
 * 部门 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
@Slf4j
public class SysDeptServiceImpl implements SysDeptService {

	/**
	 * 定时执行 {@link #schedulePeriodicRefresh()} 的周期 因为已经通过 Redis Pub/Sub
	 * 机制，所以频率不需要高
	 */
	private static final long SCHEDULER_PERIOD = 5 * 60 * 1000L;

	/**
	 * 部门缓存 key：部门编号 {@link DeptDO#getId()}
	 *
	 * 这里声明 volatile 修饰的原因是，每次刷新时，直接修改指向
	 */
	@SuppressWarnings("unused")
	private volatile Map<Long, DeptDO> deptCache;

	/**
	 * 父部门缓存 key：部门编号 {@link DeptDO#getParentId()} value: 直接子部门列表
	 *
	 * 这里声明 volatile 修饰的原因是，每次刷新时，直接修改指向
	 */
	private volatile Multimap<Long, DeptDO> parentDeptCache;
	/**
	 * 缓存部门的最大更新时间，用于后续的增量轮询，判断是否有更新
	 */
	private volatile Date maxUpdateTime;

	@Autowired
	private SysDeptMapper deptMapper;

	@Autowired
	@Lazy
	// 注入自己，所以延迟加载
	private SysDeptService self;

	@Override
	@PostConstruct
	@TenantIgnore
	// 初始化缓存，无需租户过滤
	public synchronized void initLocalCache() {
		// 获取部门列表，如果有更新
		List<DeptDO> deptList = loadDeptIfUpdate(maxUpdateTime);
		if (CollUtil.isEmpty(deptList)) {
			return;
		}

		// 构建缓存
		ImmutableMap.Builder<Long, DeptDO> builder = ImmutableMap.builder();
		ImmutableMultimap.Builder<Long, DeptDO> parentBuilder = ImmutableMultimap
				.builder();
		deptList.forEach(sysRoleDO -> {
			builder.put(sysRoleDO.getId(), sysRoleDO);
			parentBuilder.put(sysRoleDO.getParentId(), sysRoleDO);
		});
		// 设置缓存
		deptCache = builder.build();
		parentDeptCache = parentBuilder.build();
		maxUpdateTime = CollectionUtils.getMaxValue(deptList,
				DeptDO::getUpdateTime);
		log.info("[initLocalCache][初始化 Dept 数量为 {}]", deptList.size());
	}

	@Scheduled(fixedDelay = SCHEDULER_PERIOD, initialDelay = SCHEDULER_PERIOD)
	public void schedulePeriodicRefresh() {
		self.initLocalCache();
	}

	/**
	 * 如果部门发生变化，从数据库中获取最新的全量部门。 如果未发生变化，则返回空
	 *
	 * @param maxUpdateTime
	 *            当前部门的最大更新时间
	 * @return 部门列表
	 */
	protected List<DeptDO> loadDeptIfUpdate(Date maxUpdateTime) {
		// 第一步，判断是否要更新。
		if (maxUpdateTime == null) { // 如果更新时间为空，说明 DB 一定有新数据
			log.info("[loadMenuIfUpdate][首次加载全量部门]");
		} else { // 判断数据库中是否有更新的部门
			if (deptMapper.selectCountByUpdateTimeGt(maxUpdateTime) == 0) {
				return null;
			}
			log.info("[loadMenuIfUpdate][增量加载全量部门]");
		}
		// 第二步，如果有更新，则从数据库加载所有部门
		return deptMapper.selectList();
	}

	@Override
	public List<DeptDO> getDeptsByParentIdFromCache(Long parentId,
			boolean recursive) {
		if (parentId == null) {
			return Collections.emptyList();
		}
		List<DeptDO> result = new ArrayList<>(); // TODO 芋艿：待优化，新增缓存，避免每次遍历的计算
		// 递归，简单粗暴
		this.getDeptsByParentIdFromCache(result, parentId,
				recursive ? Integer.MAX_VALUE : 1, // 如果递归获取，则无限；否则，只递归 1 次
				parentDeptCache);
		return result;
	}

	/**
	 * 递归获取所有的子部门，添加到 result 结果
	 *
	 * @param result
	 *            结果
	 * @param parentId
	 *            父编号
	 * @param recursiveCount
	 *            递归次数
	 * @param parentDeptMap
	 *            父部门 Map，使用缓存，避免变化
	 */
	private void getDeptsByParentIdFromCache(List<DeptDO> result,
			Long parentId, int recursiveCount,
			Multimap<Long, DeptDO> parentDeptMap) {
		// 递归次数为 0，结束！
		if (recursiveCount == 0) {
			return;
		}
		// 获得子部门
		Collection<DeptDO> depts = parentDeptMap.get(parentId);
		if (CollUtil.isEmpty(depts)) {
			return;
		}
		result.addAll(depts);
		// 继续递归
		depts.forEach(dept -> getDeptsByParentIdFromCache(result, dept.getId(),
				recursiveCount - 1, parentDeptMap));
	}

	@Override
	public List<DeptDO> getDepts(Collection<Long> ids) {
		return deptMapper.selectBatchIds(ids);
	}

	@Override
	public DeptDO getDept(Long id) {
		return deptMapper.selectById(id);
	}

	@Override
	public void validDepts(Collection<Long> ids) {
		if (CollUtil.isEmpty(ids)) {
			return;
		}
		// 获得科室信息
		List<DeptDO> depts = deptMapper.selectBatchIds(ids);
		Map<Long, DeptDO> deptMap = CollectionUtils.convertMap(depts,
				DeptDO::getId);
		// 校验
		ids.forEach(id -> {
			DeptDO dept = deptMap.get(id);
			if (dept == null) {
				throw ServiceExceptionUtil
						.exception(ErrorCodeConstants.DEPT_NOT_FOUND);
			}
			if (!CommonStatusEnum.ENABLE.getStatus().equals(dept.getStatus())) {
				throw ServiceExceptionUtil.exception(
						ErrorCodeConstants.DEPT_NOT_ENABLE, dept.getName());
			}
		});
	}

}
