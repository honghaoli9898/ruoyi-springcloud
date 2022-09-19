package com.sdps.module.user.convert.dept;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.sdps.common.pojo.PageResult;
import com.sdps.module.system.dal.dataobject.dept.PostDO;
import com.sdps.module.user.controller.admin.dept.vo.post.PostCreateReqVO;
import com.sdps.module.user.controller.admin.dept.vo.post.PostExcelVO;
import com.sdps.module.user.controller.admin.dept.vo.post.PostRespVO;
import com.sdps.module.user.controller.admin.dept.vo.post.PostSimpleRespVO;
import com.sdps.module.user.controller.admin.dept.vo.post.PostUpdateReqVO;

@Mapper
public interface PostConvert {

    PostConvert INSTANCE = Mappers.getMapper(PostConvert.class);

    List<PostSimpleRespVO> convertList02(List<PostDO> list);

    PageResult<PostRespVO> convertPage(PageResult<PostDO> page);

    PostRespVO convert(PostDO id);

    PostDO convert(PostCreateReqVO bean);

    PostDO convert(PostUpdateReqVO reqVO);

    List<PostExcelVO> convertList03(List<PostDO> list);

}
