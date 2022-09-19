package com.sdps.module.user.dal.mapper.dept;

import java.util.Collection;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.sdps.common.mybatis.core.mapper.BaseMapperX;
import com.sdps.common.mybatis.core.query.LambdaQueryWrapperX;
import com.sdps.module.system.dal.dataobject.dept.UserPostDO;

@Mapper
public interface UserPostMapper extends BaseMapperX<UserPostDO> {

    default List<UserPostDO> selectListByUserId(Long userId) {
        return selectList(new LambdaQueryWrapperX<UserPostDO>()
                .eq(UserPostDO::getUserId, userId));
    }

    default void deleteByUserIdAndPostId(Long userId, Collection<Long> postIds) {
        delete(new LambdaQueryWrapperX<UserPostDO>()
                .eq(UserPostDO::getUserId, userId)
                .in(UserPostDO::getPostId, postIds));
    }

    default void deleteByUserId(Long userId){
        delete(Wrappers.lambdaUpdate(UserPostDO.class).eq(UserPostDO::getUserId, userId));
    }
}
