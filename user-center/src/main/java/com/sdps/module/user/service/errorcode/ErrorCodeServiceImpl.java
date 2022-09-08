package com.sdps.module.user.service.errorcode;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.google.common.annotations.VisibleForTesting;
import com.sdps.common.exception.util.ServiceExceptionUtil;
import com.sdps.common.pojo.PageResult;
import com.sdps.module.system.dal.dataobject.errorcode.ErrorCodeDO;
import com.sdps.module.system.enums.ErrorCodeConstants;
import com.sdps.module.system.enums.errorcode.ErrorCodeTypeEnum;
import com.sdps.module.user.controller.admin.errorcode.vo.ErrorCodeCreateReqVO;
import com.sdps.module.user.controller.admin.errorcode.vo.ErrorCodeExportReqVO;
import com.sdps.module.user.controller.admin.errorcode.vo.ErrorCodePageReqVO;
import com.sdps.module.user.controller.admin.errorcode.vo.ErrorCodeUpdateReqVO;
import com.sdps.module.user.convert.errorcode.ErrorCodeConvert;
import com.sdps.module.user.dal.mapper.errorcode.ErrorCodeMapper;

/**
 * 错误码 Service 实现类
 *
 * @author dlyan
 */
@Service
@Validated
public class ErrorCodeServiceImpl implements ErrorCodeService {

	@Autowired
	private ErrorCodeMapper errorCodeMapper;

	@Override
	public Long createErrorCode(ErrorCodeCreateReqVO createReqVO) {
		// 校验 code 重复
		validateCodeDuplicate(createReqVO.getCode(), null);

		// 插入
		ErrorCodeDO errorCodeDO = ErrorCodeConvert.INSTANCE
				.convert(createReqVO);
		errorCodeDO.setType(ErrorCodeTypeEnum.MANUAL_OPERATION.getType());
		errorCodeMapper.insert(errorCodeDO);
		// 返回
		return errorCodeDO.getId();
	}

	@Override
	public void updateErrorCode(ErrorCodeUpdateReqVO updateReqVO) {
		// 校验存在
		this.validateErrorCodeExists(updateReqVO.getId());
		// 校验 code 重复
		validateCodeDuplicate(updateReqVO.getCode(), updateReqVO.getId());

		// 更新
		ErrorCodeDO updateObj = ErrorCodeConvert.INSTANCE.convert(updateReqVO);
		updateObj.setType(ErrorCodeTypeEnum.MANUAL_OPERATION.getType());
		errorCodeMapper.updateById(updateObj);
	}

	@Override
	public void deleteErrorCode(Long id) {
		// 校验存在
		this.validateErrorCodeExists(id);
		// 删除
		errorCodeMapper.deleteById(id);
	}

	/**
	 * 校验错误码的唯一字段是否重复
	 *
	 * 是否存在相同编码的错误码
	 *
	 * @param code
	 *            错误码编码
	 * @param id
	 *            错误码编号
	 */
	@VisibleForTesting
	public void validateCodeDuplicate(Integer code, Long id) {
		ErrorCodeDO errorCodeDO = errorCodeMapper.selectByCode(code);
		if (errorCodeDO == null) {
			return;
		}
		// 如果 id 为空，说明不用比较是否为相同 id 的错误码
		if (id == null) {
			throw ServiceExceptionUtil
					.exception(ErrorCodeConstants.ERROR_CODE_DUPLICATE);
		}
		if (!errorCodeDO.getId().equals(id)) {
			throw ServiceExceptionUtil
					.exception(ErrorCodeConstants.ERROR_CODE_DUPLICATE);
		}
	}

	@VisibleForTesting
	public void validateErrorCodeExists(Long id) {
		if (errorCodeMapper.selectById(id) == null) {
			throw ServiceExceptionUtil
					.exception(ErrorCodeConstants.ERROR_CODE_NOT_EXISTS);
		}
	}

	@Override
	public ErrorCodeDO getErrorCode(Long id) {
		return errorCodeMapper.selectById(id);
	}

	@Override
	public PageResult<ErrorCodeDO> getErrorCodePage(ErrorCodePageReqVO pageReqVO) {
		return errorCodeMapper.selectPage(pageReqVO);
	}

	@Override
	public List<ErrorCodeDO> getErrorCodeList(ErrorCodeExportReqVO exportReqVO) {
		return errorCodeMapper.selectList(exportReqVO);
	}


}
