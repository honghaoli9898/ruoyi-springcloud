package com.sdps.module.system.dal.mapper.errorcode;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.sdps.common.mybatis.core.mapper.BaseMapperX;
import com.sdps.common.mybatis.core.query.LambdaQueryWrapperX;
import com.sdps.module.system.dal.dataobject.errorcode.ErrorCodeDO;

@Mapper
public interface SysErrorCodeMapper extends BaseMapperX<ErrorCodeDO> {

    default List<ErrorCodeDO> selectListByCodes(Collection<Integer> codes) {
        return selectList(new LambdaQueryWrapperX<ErrorCodeDO>().in(ErrorCodeDO::getCode, codes));
    }


    default List<ErrorCodeDO> selectListByApplicationNameAndUpdateTimeGt(String applicationName, Date minUpdateTime) {
        return selectList(new LambdaQueryWrapperX<ErrorCodeDO>().eq(ErrorCodeDO::getApplicationName, applicationName)
                .gtIfPresent(ErrorCodeDO::getUpdateTime, minUpdateTime));
    }

}
