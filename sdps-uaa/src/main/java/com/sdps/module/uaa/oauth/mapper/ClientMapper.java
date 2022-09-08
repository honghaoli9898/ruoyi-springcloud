package com.sdps.module.uaa.oauth.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sdps.common.mybatis.core.mapper.BaseMapperX;
import com.sdps.module.uaa.oauth.model.Client;

@Mapper
public interface ClientMapper extends BaseMapperX<Client> {
    List<Client> findList(Page<Client> page, @Param("params") Map<String, Object> params );
}
