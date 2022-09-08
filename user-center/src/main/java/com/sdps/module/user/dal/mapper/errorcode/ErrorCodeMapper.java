package com.sdps.module.user.dal.mapper.errorcode;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.sdps.common.mybatis.core.mapper.BaseMapperX;
import com.sdps.common.mybatis.core.query.LambdaQueryWrapperX;
import com.sdps.common.pojo.PageResult;
import com.sdps.module.system.dal.dataobject.errorcode.ErrorCodeDO;
import com.sdps.module.user.controller.admin.errorcode.vo.ErrorCodeExportReqVO;
import com.sdps.module.user.controller.admin.errorcode.vo.ErrorCodePageReqVO;

@Mapper
public interface ErrorCodeMapper extends BaseMapperX<ErrorCodeDO> {

	default PageResult<ErrorCodeDO> selectPage(ErrorCodePageReqVO reqVO) {
		return selectPage(
				reqVO,
				new LambdaQueryWrapperX<ErrorCodeDO>()
						.eqIfPresent(ErrorCodeDO::getType, reqVO.getType())
						.likeIfPresent(ErrorCodeDO::getApplicationName,
								reqVO.getApplicationName())
						.eqIfPresent(ErrorCodeDO::getCode, reqVO.getCode())
						.likeIfPresent(ErrorCodeDO::getMessage,
								reqVO.getMessage())
						.betweenIfPresent(ErrorCodeDO::getCreateTime,
								reqVO.getCreateTime())
						.orderByDesc(ErrorCodeDO::getCode));
	}

	default List<ErrorCodeDO> selectList(ErrorCodeExportReqVO reqVO) {
		return selectList(new LambdaQueryWrapperX<ErrorCodeDO>()
				.eqIfPresent(ErrorCodeDO::getType, reqVO.getType())
				.likeIfPresent(ErrorCodeDO::getApplicationName,
						reqVO.getApplicationName())
				.eqIfPresent(ErrorCodeDO::getCode, reqVO.getCode())
				.likeIfPresent(ErrorCodeDO::getMessage, reqVO.getMessage())
				.betweenIfPresent(ErrorCodeDO::getCreateTime,
						reqVO.getCreateTime())
				.orderByDesc(ErrorCodeDO::getCode));
	}

	default ErrorCodeDO selectByCode(Integer code) {
		return selectOne(new LambdaQueryWrapperX<ErrorCodeDO>().eq(
				ErrorCodeDO::getCode, code));
	}

}
