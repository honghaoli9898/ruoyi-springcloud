package com.sdps.module.system.dal.mapper.logger;

import org.apache.ibatis.annotations.Mapper;

import com.sdps.common.mybatis.core.mapper.BaseMapperX;
import com.sdps.module.system.dal.dataobject.logger.ApiAccessLogDO;

/**
 * API 访问日志 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface SysApiAccessLogMapper extends BaseMapperX<ApiAccessLogDO> {

}
