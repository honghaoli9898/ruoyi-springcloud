package com.sdps.module.user.dal.mapper.logger;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.sdps.common.mybatis.core.mapper.BaseMapperX;
import com.sdps.common.mybatis.core.query.LambdaQueryWrapperX;
import com.sdps.common.pojo.PageResult;
import com.sdps.module.system.dal.dataobject.logger.ApiErrorLogDO;
import com.sdps.module.user.controller.admin.logger.vo.apierrorlog.ApiErrorLogExportReqVO;
import com.sdps.module.user.controller.admin.logger.vo.apierrorlog.ApiErrorLogPageReqVO;

/**
 * API 错误日志 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface ApiErrorLogMapper extends BaseMapperX<ApiErrorLogDO> {

	default PageResult<ApiErrorLogDO> selectPage(ApiErrorLogPageReqVO reqVO) {
		return selectPage(
				reqVO,
				new LambdaQueryWrapperX<ApiErrorLogDO>()
						.eqIfPresent(ApiErrorLogDO::getUserId,
								reqVO.getUserId())
						.eqIfPresent(ApiErrorLogDO::getUserType,
								reqVO.getUserType())
						.eqIfPresent(ApiErrorLogDO::getApplicationName,
								reqVO.getApplicationName())
						.likeIfPresent(ApiErrorLogDO::getRequestUrl,
								reqVO.getRequestUrl())
						.betweenIfPresent(ApiErrorLogDO::getExceptionTime,
								reqVO.getExceptionTime())
						.eqIfPresent(ApiErrorLogDO::getProcessStatus,
								reqVO.getProcessStatus())
						.orderByDesc(ApiErrorLogDO::getId));
	}

	default List<ApiErrorLogDO> selectList(ApiErrorLogExportReqVO reqVO) {
		return selectList(new LambdaQueryWrapperX<ApiErrorLogDO>()
				.eqIfPresent(ApiErrorLogDO::getUserId, reqVO.getUserId())
				.eqIfPresent(ApiErrorLogDO::getUserType, reqVO.getUserType())
				.eqIfPresent(ApiErrorLogDO::getApplicationName,
						reqVO.getApplicationName())
				.likeIfPresent(ApiErrorLogDO::getRequestUrl,
						reqVO.getRequestUrl())
				.betweenIfPresent(ApiErrorLogDO::getExceptionTime,
						reqVO.getExceptionTime())
				.eqIfPresent(ApiErrorLogDO::getProcessStatus,
						reqVO.getProcessStatus())
				.orderByDesc(ApiErrorLogDO::getId));
	}

}
