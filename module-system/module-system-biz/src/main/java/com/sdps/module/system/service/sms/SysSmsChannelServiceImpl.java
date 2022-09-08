package com.sdps.module.system.service.sms;

import cn.hutool.core.collection.CollUtil;
import com.sdps.common.sms.core.client.SmsClientFactory;
import com.sdps.common.sms.core.property.SmsChannelProperties;
import com.sdps.common.util.collection.CollectionUtils;
import com.sdps.module.system.convert.sms.SysSmsChannelConvert;
import com.sdps.module.system.dal.dataobject.sms.SmsChannelDO;
import com.sdps.module.system.dal.mapper.sms.SysSmsChannelMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 短信渠道Service实现类
 *
 * @author zzf
 * @date 2021/1/25 9:25
 */
@Service
@Slf4j
public class SysSmsChannelServiceImpl implements SysSmsChannelService {

    /**
     * 定时执行 {@link #schedulePeriodicRefresh()} 的周期
     * 因为已经通过 Redis Pub/Sub 机制，所以频率不需要高
     */
    private static final long SCHEDULER_PERIOD = 5 * 60 * 1000L;

    /**
     * 缓存菜单的最大更新时间，用于后续的增量轮询，判断是否有更新
     */
    private volatile Date maxUpdateTime;

    @Resource
    private SmsClientFactory smsClientFactory;

    @Resource
    private SysSmsChannelMapper sysSmsChannelMapper;


    @Override
    @PostConstruct
    public void initSmsClients() {
        // 获取短信渠道，如果有更新
        List<SmsChannelDO> smsChannels = this.loadSmsChannelIfUpdate(maxUpdateTime);
        if (CollUtil.isEmpty(smsChannels)) {
            return;
        }

        // 创建或更新短信 Client
        List<SmsChannelProperties> propertiesList = SysSmsChannelConvert.INSTANCE.convertList02(smsChannels);
        propertiesList.forEach(properties -> smsClientFactory.createOrUpdateSmsClient(properties));

        // 写入缓存
        maxUpdateTime = CollectionUtils.getMaxValue(smsChannels, SmsChannelDO::getUpdateTime);
        log.info("[initSmsClients][初始化 SmsChannel 数量为 {}]", smsChannels.size());
    }

    @Scheduled(fixedDelay = SCHEDULER_PERIOD, initialDelay = SCHEDULER_PERIOD)
    public void schedulePeriodicRefresh() {
        initSmsClients();
    }

    /**
     * 如果短信渠道发生变化，从数据库中获取最新的全量短信渠道。
     * 如果未发生变化，则返回空
     *
     * @param maxUpdateTime 当前短信渠道的最大更新时间
     * @return 短信渠道列表
     */
    private List<SmsChannelDO> loadSmsChannelIfUpdate(Date maxUpdateTime) {
        // 第一步，判断是否要更新。
        if (maxUpdateTime == null) { // 如果更新时间为空，说明 DB 一定有新数据
            log.info("[loadSmsChannelIfUpdate][首次加载全量短信渠道]");
        } else { // 判断数据库中是否有更新的短信渠道
            if (sysSmsChannelMapper.selectCountByUpdateTimeGt(maxUpdateTime) == 0) {
                return null;
            }
            log.info("[loadSmsChannelIfUpdate][增量加载全量短信渠道]");
        }
        // 第二步，如果有更新，则从数据库加载所有短信渠道
        return sysSmsChannelMapper.selectList();
    }

}
