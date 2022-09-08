package com.sdps.module.dynamicroute.respository;

import com.sdps.module.dynamicroute.entity.DynamicVersion;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
@Mapper
public interface DynamicVersionMapper {
    int deleteByPrimaryKey(Long id);

    int insert(DynamicVersion record);

    int insertSelective(DynamicVersion record);

    DynamicVersion selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(DynamicVersion record);

    int updateByPrimaryKey(DynamicVersion record);

    //获取最后一次发布的版本号
    Long getLastVersion();

    //获取所有的版本发布信息
    List<DynamicVersion> listAll();
}