package com.sdps.module.system.mq.message.permission;

import lombok.Data;
import lombok.EqualsAndHashCode;

import com.sdps.common.mq.core.pubsub.AbstractChannelMessage;

/**
 * 用户与角色的数据刷新 Message
 *
 * @author 芋道源码
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserRoleRefreshMessage extends AbstractChannelMessage {

    @Override
    public String getChannel() {
        return "system.user-role.refresh";
    }

}
