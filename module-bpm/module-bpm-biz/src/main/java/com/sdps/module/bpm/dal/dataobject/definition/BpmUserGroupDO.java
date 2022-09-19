package com.sdps.module.bpm.dal.dataobject.definition;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sdps.common.enums.CommonStatusEnum;
import com.sdps.common.model.BaseDO;
import com.sdps.common.mybatis.type.JsonLongSetTypeHandler;
import lombok.*;

import java.util.Set;

/**
 * Bpm 用户组
 *
 * @author 芋道源码
 */
@TableName(value = "bpm_user_group", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BpmUserGroupDO extends BaseDO {

	private static final long serialVersionUID = -6204309176996866839L;
	/**
     * 编号，自增
     */
    @TableId
    private Long id;
    /**
     * 组名
     */
    private String name;
    /**
     * 描述
     */
    private String description;
    /**
     * 状态
     *
     * 枚举 {@link CommonStatusEnum}
     */
    private Integer status;
    /**
     * 成员用户编号数组
     */
    @TableField(typeHandler = JsonLongSetTypeHandler.class)
    private Set<Long> memberUserIds;

}
