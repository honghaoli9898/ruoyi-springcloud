package com.sdps.common.redis.lock;

import static com.sdps.common.exception.enums.GlobalErrorCodeConstants.LOCK_TYPE_ERROR;

import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import com.sdps.common.constant.CommonConstant;
import com.sdps.common.exception.util.ServiceExceptionUtil;
import com.sdps.common.lock.DistributedLock;
import com.sdps.common.lock.ZLock;

/**
 * redisson分布式锁实现，基本锁功能的抽象实现 本接口能满足绝大部分的需求，高级的锁功能，请自行扩展或直接使用原生api
 *
 */
@ConditionalOnClass(RedissonClient.class)
@ConditionalOnProperty(prefix = "sdps.lock", name = "lockerType", havingValue = "REDIS", matchIfMissing = true)
public class RedissonDistributedLock implements DistributedLock {
	@Autowired
	private RedissonClient redisson;

	private ZLock getLock(String key, boolean isFair) {
		RLock lock;
		if (isFair) {
			lock = redisson.getFairLock(CommonConstant.LOCK_KEY_PREFIX + ":"
					+ key);
		} else {
			lock = redisson.getLock(CommonConstant.LOCK_KEY_PREFIX + ":" + key);
		}
		return new ZLock(lock, this);
	}

	@Override
	public ZLock lock(String key, long leaseTime, TimeUnit unit, boolean isFair) {
		ZLock zLock = getLock(key, isFair);
		RLock lock = (RLock) zLock.getLock();
		lock.lock(leaseTime, unit);
		return zLock;
	}

	@Override
	public ZLock tryLock(String key, long waitTime, long leaseTime,
			TimeUnit unit, boolean isFair) throws InterruptedException {
		ZLock zLock = getLock(key, isFair);
		RLock lock = (RLock) zLock.getLock();
		if (lock.tryLock(waitTime, leaseTime, unit)) {
			return zLock;
		}
		return null;
	}

	@Override
	public void unlock(Object lock) {
		if (lock != null) {
			if (lock instanceof RLock) {
				RLock rLock = (RLock) lock;
				if (rLock.isLocked()) {
					rLock.unlock();
				}
			} else {
				throw ServiceExceptionUtil.exception(LOCK_TYPE_ERROR);
			}
		}
	}
}