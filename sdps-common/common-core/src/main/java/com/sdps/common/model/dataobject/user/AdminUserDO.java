package com.sdps.common.model.dataobject.user;

import java.util.Date;
import java.util.List;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sdps.common.enums.CommonStatusEnum;
import com.sdps.common.model.TenantBaseDO;
import com.sdps.common.model.dataobject.permission.RoleDO;
import com.sdps.common.mybatis.type.JsonLongSetTypeHandler;

/**
 * 管理后台的用户 DO
 *
 * @author 芋道源码
 */
@TableName(value = "system_users", autoResultMap = true)
// 由于 SQL Server 的 system_user 是关键字，所以使用 system_users
@KeySequence("system_user_seq")
// 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserDO extends TenantBaseDO {

	private static final long serialVersionUID = 6100937135869534278L;
	/**
	 * 用户ID
	 */
	@TableId
	private Long id;
	/**
	 * 用户账号
	 */
	private String username;
	/**
	 * 加密后的密码
	 *
	 * 因为目前使用 {@link BCryptPasswordEncoder} 加密器，所以无需自己处理 salt 盐
	 */
	private String password;
	/**
	 * 用户昵称
	 */
	private String nickname;
	/**
	 * 备注
	 */
	private String remark;
	/**
	 * 部门 ID
	 */
	private Long deptId;
	/**
	 * 岗位编号数组
	 */
	@TableField(typeHandler = JsonLongSetTypeHandler.class)
	private Set<Long> postIds;
	/**
	 * 用户邮箱
	 */
	private String email;
	/**
	 * 手机号码
	 */
	private String mobile;
	/**
	 * 用户性别
	 *
	 * 枚举类 {@link SexEnum}
	 */
	private Integer sex;
	/**
	 * 用户头像
	 */
	private String avatar;
	/**
	 * 帐号状态
	 *
	 * 枚举 {@link CommonStatusEnum}
	 */
	private Integer status;
	/**
	 * 最后登录IP
	 */
	private String loginIp;
	/**
	 * 最后登录时间
	 */
	private Date loginDate;

	@TableField(exist=false)
	private List<RoleDO> roles;
	
}
