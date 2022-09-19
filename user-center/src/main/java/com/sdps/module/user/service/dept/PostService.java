package com.sdps.module.user.service.dept;

import java.util.Collection;
import java.util.List;

import org.springframework.lang.Nullable;

import com.sdps.common.enums.CommonStatusEnum;
import com.sdps.common.pojo.PageResult;
import com.sdps.common.util.collection.SetUtils;
import com.sdps.module.system.dal.dataobject.dept.PostDO;
import com.sdps.module.user.controller.admin.dept.vo.post.PostCreateReqVO;
import com.sdps.module.user.controller.admin.dept.vo.post.PostExportReqVO;
import com.sdps.module.user.controller.admin.dept.vo.post.PostPageReqVO;
import com.sdps.module.user.controller.admin.dept.vo.post.PostUpdateReqVO;

/**
 * 岗位 Service 接口
 *
 * @author 芋道源码
 */
public interface PostService {

    /**
     * 创建岗位
     *
     * @param reqVO 岗位信息
     * @return 岗位编号
     */
    Long createPost(PostCreateReqVO reqVO);

    /**
     * 更新岗位
     *
     * @param reqVO 岗位信息
     */
    void updatePost(PostUpdateReqVO reqVO);

    /**
     * 删除岗位信息
     *
     * @param id 岗位编号
     */
    void deletePost(Long id);

    /**
     * 获得岗位列表
     *
     * @param ids 岗位编号数组。如果为空，不进行筛选
     * @return 部门列表
     */
    default List<PostDO> getPosts(@Nullable Collection<Long> ids) {
        return getPosts(ids, SetUtils.asSet(CommonStatusEnum.ENABLE.getStatus(), CommonStatusEnum.DISABLE.getStatus()));
    }

    /**
     * 获得符合条件的岗位列表
     *
     * @param ids 岗位编号数组。如果为空，不进行筛选
     * @param statuses 状态数组。如果为空，不进行筛选
     * @return 部门列表
     */
    List<PostDO> getPosts(@Nullable Collection<Long> ids, @Nullable Collection<Integer> statuses);

    /**
     * 获得岗位分页列表
     *
     * @param reqVO 分页条件
     * @return 部门分页列表
     */
    PageResult<PostDO> getPostPage(PostPageReqVO reqVO);

    /**
     * 获得岗位列表
     *
     * @param reqVO 查询条件
     * @return 部门列表
     */
    List<PostDO> getPosts(PostExportReqVO reqVO);

    /**
     * 获得岗位信息
     *
     * @param id 岗位编号
     * @return 岗位信息
     */
    PostDO getPost(Long id);


}
