package com.sdps.module.bpm.dal.mysql.definition;


import com.sdps.common.mybatis.core.mapper.BaseMapperX;
import com.sdps.common.mybatis.core.query.QueryWrapperX;
import com.sdps.module.bpm.controller.admin.definition.vo.form.BpmFormPageReqVO;
import com.sdps.module.bpm.dal.dataobject.definition.BpmFormDO;
import com.sdps.common.pojo.PageResult;
import org.apache.ibatis.annotations.Mapper;

/**
 * 动态表单 Mapper
 *
 * @author 风里雾里
 */
@Mapper
public interface BpmFormMapper extends BaseMapperX<BpmFormDO> {

    default PageResult<BpmFormDO> selectPage(BpmFormPageReqVO reqVO) {
        return selectPage(reqVO, new QueryWrapperX<BpmFormDO>()
                .likeIfPresent("name", reqVO.getName())
                .orderByDesc("id"));
    }

}
