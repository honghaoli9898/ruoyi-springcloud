package com.sdps.module.user.convert.dept;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.sdps.module.system.api.dept.dto.DeptRespDTO;
import com.sdps.module.system.dal.dataobject.dept.DeptDO;
import com.sdps.module.user.controller.admin.dept.vo.dept.DeptCreateReqVO;
import com.sdps.module.user.controller.admin.dept.vo.dept.DeptRespVO;
import com.sdps.module.user.controller.admin.dept.vo.dept.DeptSimpleRespVO;
import com.sdps.module.user.controller.admin.dept.vo.dept.DeptUpdateReqVO;

@Mapper
public interface DeptConvert {

    DeptConvert INSTANCE = Mappers.getMapper(DeptConvert.class);

    List<DeptRespVO> convertList(List<DeptDO> list);

    List<DeptSimpleRespVO> convertList02(List<DeptDO> list);

    DeptRespVO convert(DeptDO bean);

    DeptDO convert(DeptCreateReqVO bean);

    DeptDO convert(DeptUpdateReqVO bean);

    List<DeptRespDTO> convertList03(List<DeptDO> list);

}
