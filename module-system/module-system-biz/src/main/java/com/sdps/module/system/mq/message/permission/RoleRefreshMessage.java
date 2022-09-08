package com.sdps.module.system.mq.message.permission;

import lombok.Data;
import lombok.EqualsAndHashCode;

import com.sdps.common.mq.core.pubsub.AbstractChannelMessage;

/**
 * 角色数据刷新 Message
 *
 * @author 芋道源码
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RoleRefreshMessage extends AbstractChannelMessage {

    @Override
    public String getChannel() {
        return "system.role.refresh";
    }

}
