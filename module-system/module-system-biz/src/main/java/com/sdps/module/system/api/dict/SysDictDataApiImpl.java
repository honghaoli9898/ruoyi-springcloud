package com.sdps.module.system.api.dict;

import com.sdps.module.system.api.dict.dto.DictDataRespDTO;
import com.sdps.module.system.convert.dict.SysDictDataConvert;
import com.sdps.module.system.dal.dataobject.dict.DictDataDO;
import com.sdps.module.system.service.dict.SysDictDataService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;

/**
 * 字典数据 API 实现类
 *
 * @author 芋道源码
 */
@Service
public class SysDictDataApiImpl implements DictDataApi {

    @Resource
    private SysDictDataService dictDataService;

    @Override
    public void validDictDatas(String dictType, Collection<String> values) {
        dictDataService.validDictDatas(dictType, values);
    }

    @Override
    public DictDataRespDTO getDictData(String dictType, String value) {
        DictDataDO dictData = dictDataService.getDictData(dictType, value);
        return SysDictDataConvert.INSTANCE.convert02(dictData);
    }

    @Override
    public DictDataRespDTO parseDictData(String dictType, String label) {
        DictDataDO dictData = dictDataService.parseDictData(dictType, label);
        return SysDictDataConvert.INSTANCE.convert02(dictData);
    }

}
