package com.sdps.module.user.dal.mapper.dept;

import java.util.Collection;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sdps.common.mybatis.core.mapper.BaseMapperX;
import com.sdps.common.mybatis.core.query.QueryWrapperX;
import com.sdps.common.pojo.PageResult;
import com.sdps.module.user.controller.admin.dept.vo.post.PostExportReqVO;
import com.sdps.module.user.controller.admin.dept.vo.post.PostPageReqVO;
import com.sdps.module.user.dal.dataobject.dept.PostDO;

@Mapper
public interface PostMapper extends BaseMapperX<PostDO> {

    default List<PostDO> selectList(Collection<Long> ids, Collection<Integer> statuses) {
        return selectList(new QueryWrapperX<PostDO>().inIfPresent("id", ids)
                .inIfPresent("status", statuses));
    }

    default PageResult<PostDO> selectPage(PostPageReqVO reqVO) {
        return selectPage(reqVO, new QueryWrapperX<PostDO>()
                .likeIfPresent("code", reqVO.getCode())
                .likeIfPresent("name", reqVO.getName())
                .eqIfPresent("status", reqVO.getStatus())
                .orderByDesc("id"));
    }

    default List<PostDO> selectList(PostExportReqVO reqVO) {
        return selectList(new QueryWrapperX<PostDO>()
                .likeIfPresent("code", reqVO.getCode())
                .likeIfPresent("name", reqVO.getName())
                .eqIfPresent("status", reqVO.getStatus()));
    }

    default PostDO selectByName(String name) {
        return selectOne(new QueryWrapper<PostDO>().eq("name", name));
    }

    default PostDO selectByCode(String code) {
        return selectOne(new QueryWrapper<PostDO>().eq("code", code));
    }

}
