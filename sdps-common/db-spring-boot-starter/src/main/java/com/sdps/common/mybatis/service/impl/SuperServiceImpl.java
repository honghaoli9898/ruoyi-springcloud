package com.sdps.common.mybatis.service.impl;

import static com.sdps.common.exception.enums.GlobalErrorCodeConstants.IDEMPOTENCY_EXCEPTION;
import static com.sdps.common.exception.enums.GlobalErrorCodeConstants.LOCKED_IS_NULL;
import static com.sdps.common.exception.enums.GlobalErrorCodeConstants.LOCKKEY_IS_NULL;
import static com.sdps.common.exception.enums.GlobalErrorCodeConstants.LOCK_WAIT_TIMEOUT;

import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import cn.hutool.core.util.StrUtil;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.ExceptionUtils;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sdps.common.exception.util.ServiceExceptionUtil;
import com.sdps.common.lock.DistributedLock;
import com.sdps.common.lock.ZLock;
import com.sdps.common.mybatis.service.ISuperService;

/**
 * service实现父类
 *
 * @author zlt
 * @date 2019/1/10
 *       <p>
 *       Blog: https://zlt2000.gitee.io Github: https://github.com/zlt2000
 */
public class SuperServiceImpl<M extends BaseMapper<T>, T> extends
		ServiceImpl<M, T> implements ISuperService<T> {
	@Override
	public boolean saveIdempotency(T entity, DistributedLock locker,
			String lockKey, Wrapper<T> countWrapper, String msg)
			throws Exception {
		if (locker == null) {
			throw ServiceExceptionUtil.exception(LOCKED_IS_NULL);
		}
		if (StrUtil.isEmpty(lockKey)) {
			throw ServiceExceptionUtil.exception(LOCKKEY_IS_NULL);
		}
		try (ZLock lock = locker.tryLock(lockKey, 10, 60, TimeUnit.SECONDS);) {
			if (lock != null) {
				// 判断记录是否已存在
				long count = super.count(countWrapper);
				if (count == 0) {
					return super.save(entity);
				} else {
					if (StrUtil.isEmpty(msg)) {
						msg = "已存在";
					}
					throw ServiceExceptionUtil.exception(IDEMPOTENCY_EXCEPTION);
				}
			} else {
				throw ServiceExceptionUtil.exception(LOCK_WAIT_TIMEOUT);
			}
		}
	}

	@Override
	public boolean saveIdempotency(T entity, DistributedLock lock,
			String lockKey, Wrapper<T> countWrapper) throws Exception {
		return saveIdempotency(entity, lock, lockKey, countWrapper, null);
	}

	@Override
	public boolean saveOrUpdateIdempotency(T entity, DistributedLock lock,
			String lockKey, Wrapper<T> countWrapper, String msg)
			throws Exception {
		if (null != entity) {
			Class<?> cls = entity.getClass();
			TableInfo tableInfo = TableInfoHelper.getTableInfo(cls);
			if (null != tableInfo
					&& StrUtil.isNotEmpty(tableInfo.getKeyProperty())) {
				Object idVal = ReflectionKit.getFieldValue(entity,
						tableInfo.getKeyProperty());
				if (StringUtils.checkValNull(idVal)
						|| Objects.isNull(getById((Serializable) idVal))) {
					if (StrUtil.isEmpty(msg)) {
						msg = "已存在";
					}
					return this.saveIdempotency(entity, lock, lockKey,
							countWrapper, msg);
				} else {
					return updateById(entity);
				}
			} else {
				throw ExceptionUtils
						.mpe("Error:  Can not execute. Could not find @TableId.");
			}
		}
		return false;
	}

	@Override
	public boolean saveOrUpdateIdempotency(T entity, DistributedLock lock,
			String lockKey, Wrapper<T> countWrapper) throws Exception {
		return this.saveOrUpdateIdempotency(entity, lock, lockKey,
				countWrapper, null);
	}
}
