package com.sdps.module.user.service.sms;

import cn.hutool.core.collection.CollUtil;
import com.sdps.common.exception.util.ServiceExceptionUtil;
import com.sdps.common.pojo.PageResult;
import com.sdps.common.sms.core.property.SmsChannelProperties;
import com.sdps.common.util.collection.CollectionUtils;
import com.sdps.module.system.enums.ErrorCodeConstants;
import com.sdps.module.user.controller.admin.sms.vo.channel.SmsChannelCreateReqVO;
import com.sdps.module.user.controller.admin.sms.vo.channel.SmsChannelPageReqVO;
import com.sdps.module.user.controller.admin.sms.vo.channel.SmsChannelUpdateReqVO;
import com.sdps.module.user.convert.sms.SmsChannelConvert;
import com.sdps.module.system.dal.dataobject.sms.SmsChannelDO;
import com.sdps.module.user.dal.mapper.sms.SmsChannelMapper;
import com.sdps.module.user.mq.producer.sms.SmsProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Collection;
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
public class SmsChannelServiceImpl implements SmsChannelService {

    @Resource
    private SmsChannelMapper smsChannelMapper;

    @Resource
    private SmsTemplateService smsTemplateService;

    @Resource
    private SmsProducer smsProducer;

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
            if (smsChannelMapper.selectCountByUpdateTimeGt(maxUpdateTime) == 0) {
                return null;
            }
            log.info("[loadSmsChannelIfUpdate][增量加载全量短信渠道]");
        }
        // 第二步，如果有更新，则从数据库加载所有短信渠道
        return smsChannelMapper.selectList();
    }

    @Override
    public Long createSmsChannel(SmsChannelCreateReqVO createReqVO) {
        // 插入
        SmsChannelDO smsChannel = SmsChannelConvert.INSTANCE.convert(createReqVO);
        smsChannelMapper.insert(smsChannel);
        // 发送刷新消息
        smsProducer.sendSmsChannelRefreshMessage();
        // 返回
        return smsChannel.getId();
    }

    @Override
    public void updateSmsChannel(SmsChannelUpdateReqVO updateReqVO) {
        // 校验存在
        this.validateSmsChannelExists(updateReqVO.getId());
        // 更新
        SmsChannelDO updateObj = SmsChannelConvert.INSTANCE.convert(updateReqVO);
        smsChannelMapper.updateById(updateObj);
        // 发送刷新消息
        smsProducer.sendSmsChannelRefreshMessage();
    }

    @Override
    public void deleteSmsChannel(Long id) {
        // 校验存在
        this.validateSmsChannelExists(id);
        // 校验是否有字典数据
        if (smsTemplateService.countByChannelId(id) > 0) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.SMS_CHANNEL_HAS_CHILDREN);
        }
        // 删除
        smsChannelMapper.deleteById(id);
        // 发送刷新消息
        smsProducer.sendSmsChannelRefreshMessage();
    }

    private void validateSmsChannelExists(Long id) {
        if (smsChannelMapper.selectById(id) == null) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.SMS_CHANNEL_NOT_EXISTS);
        }
    }

    @Override
    public SmsChannelDO getSmsChannel(Long id) {
        return smsChannelMapper.selectById(id);
    }

    @Override
    public List<SmsChannelDO> getSmsChannelList(Collection<Long> ids) {
        return smsChannelMapper.selectBatchIds(ids);
    }

    @Override
    public List<SmsChannelDO> getSmsChannelList() {
        return smsChannelMapper.selectList();
    }

    @Override
    public PageResult<SmsChannelDO> getSmsChannelPage(SmsChannelPageReqVO pageReqVO) {
        return smsChannelMapper.selectPage(pageReqVO);
    }

}
