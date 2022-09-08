package com.sdps.module.dynamicroute.respository;

import com.sdps.module.dynamicroute.entity.GatewayRoutes;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
@Mapper
public interface GatewayRoutesMapper {
    int deleteByPrimaryKey(@Param("id") Long id ,@Param("isDel") boolean isDel);

    int enableById(@Param("id") Long id,@Param("isEbl") boolean isEbl);

    int insert(GatewayRoutes record);

    int insertSelective(GatewayRoutes record);

    GatewayRoutes selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(GatewayRoutes record);

    int updateByPrimaryKey(GatewayRoutes record);

    List<GatewayRoutes> getRoutes(GatewayRoutes route);
}