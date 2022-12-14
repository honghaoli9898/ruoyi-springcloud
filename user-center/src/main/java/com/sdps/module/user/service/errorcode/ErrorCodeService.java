package com.sdps.module.user.service.errorcode;

import java.util.List;

import javax.validation.Valid;

import com.sdps.common.pojo.PageResult;
import com.sdps.module.system.dal.dataobject.errorcode.ErrorCodeDO;
import com.sdps.module.user.controller.admin.errorcode.vo.ErrorCodeCreateReqVO;
import com.sdps.module.user.controller.admin.errorcode.vo.ErrorCodeExportReqVO;
import com.sdps.module.user.controller.admin.errorcode.vo.ErrorCodePageReqVO;
import com.sdps.module.user.controller.admin.errorcode.vo.ErrorCodeUpdateReqVO;

/**
 * 错误码 Service 接口
 *
 * @author 芋道源码
 */
public interface ErrorCodeService {

    /**
     * 创建错误码
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createErrorCode(@Valid ErrorCodeCreateReqVO createReqVO);

    /**
     * 更新错误码
     *
     * @param updateReqVO 更新信息
     */
    void updateErrorCode(@Valid ErrorCodeUpdateReqVO updateReqVO);

    /**
     * 删除错误码
     *
     * @param id 编号
     */
    void deleteErrorCode(Long id);

    /**
     * 获得错误码
     *
     * @param id 编号
     * @return 错误码
     */
    ErrorCodeDO getErrorCode(Long id);

    /**
     * 获得错误码分页
     *
     * @param pageReqVO 分页查询
     * @return 错误码分页
     */
    PageResult<ErrorCodeDO> getErrorCodePage(ErrorCodePageReqVO pageReqVO);

    /**
     * 获得错误码列表, 用于 Excel 导出
     *
     * @param exportReqVO 查询条件
     * @return 错误码列表
     */
    List<ErrorCodeDO> getErrorCodeList(ErrorCodeExportReqVO exportReqVO);

}
