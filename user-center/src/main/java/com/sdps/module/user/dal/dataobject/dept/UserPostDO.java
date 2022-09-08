package com.sdps.module.user.dal.dataobject.dept;

import lombok.Data;
import lombok.EqualsAndHashCode;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sdps.common.model.BaseDO;
import com.sdps.common.model.dataobject.user.AdminUserDO;

/**
 * 用户和岗位关联
 *
 * @author ruoyi
 */
@TableName("system_user_post")
@KeySequence("system_user_post_seq")
// 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
public class UserPostDO extends BaseDO {

	private static final long serialVersionUID = 8884180263052992677L;
	/**
	 * 自增主键
	 */
	@TableId
	private Long id;
	/**
	 * 用户 ID
	 *
	 * 关联 {@link AdminUserDO#getId()}
	 */
	private Long userId;
	/**
	 * 角色 ID
	 *
	 * 关联 {@link PostDO#getId()}
	 */
	private Long postId;

}
