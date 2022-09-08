package com.sdps.module.system.convert.dict;

import com.sdps.module.system.api.dict.dto.DictDataRespDTO;
import com.sdps.module.system.dal.dataobject.dict.DictDataDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SysDictDataConvert {

    SysDictDataConvert INSTANCE = Mappers.getMapper(SysDictDataConvert.class);

    DictDataRespDTO convert02(DictDataDO bean);

}
