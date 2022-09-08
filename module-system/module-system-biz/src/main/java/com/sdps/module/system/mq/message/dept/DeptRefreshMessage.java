package com.sdps.module.system.mq.message.dept;

import lombok.Data;
import lombok.EqualsAndHashCode;

import com.sdps.common.mq.core.pubsub.AbstractChannelMessage;

/**
 * 部门数据刷新 Message
 *
 * @author 芋道源码
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DeptRefreshMessage extends AbstractChannelMessage {

    @Override
    public String getChannel() {
        return "system.dept.refresh";
    }

}
