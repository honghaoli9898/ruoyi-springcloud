package com.sdps.module.user.dal.dataobject.tenant;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sdps.common.enums.CommonStatusEnum;
import com.sdps.common.model.BaseDO;
import com.sdps.common.mybatis.type.JsonLongSetTypeHandler;

/**
 * 租户套餐 DO
 *
 * @author 芋道源码
 */
@TableName(value = "system_tenant_package", autoResultMap = true)
@KeySequence("system_tenant_package_seq")
// 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TenantPackageDO extends BaseDO {

	private static final long serialVersionUID = -376694294649822364L;
	/**
	 * 套餐编号，自增
	 */
	private Long id;
	/**
	 * 套餐名，唯一
	 */
	private String name;
	/**
	 * 租户状态
	 *
	 * 枚举 {@link CommonStatusEnum}
	 */
	private Integer status;
	/**
	 * 备注
	 */
	private String remark;
	/**
	 * 关联的菜单编号
	 */
	@TableField(typeHandler = JsonLongSetTypeHandler.class)
	private Set<Long> menuIds;

}
