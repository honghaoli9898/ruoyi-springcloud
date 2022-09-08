package com.sdps.module.system.dal.mapper.dept;

import java.util.Date;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.sdps.common.mybatis.core.mapper.BaseMapperX;
import com.sdps.module.system.dal.dataobject.dept.DeptDO;

@Mapper
public interface SysDeptMapper extends BaseMapperX<DeptDO> {

	@Select("SELECT COUNT(*) FROM system_dept WHERE update_time > #{maxUpdateTime}")
	Long selectCountByUpdateTimeGt(Date maxUpdateTime);

}
