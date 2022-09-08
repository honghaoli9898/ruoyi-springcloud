package com.sdps.module.system.service.dict;

import com.sdps.module.system.dal.dataobject.dict.DictDataDO;
import java.util.Collection;


/**
 * 字典数据 Service 接口
 *
 * @author ruoyi
 */
public interface SysDictDataService {

    /**
     * 校验字典数据们是否有效。如下情况，视为无效：
     * 1. 字典数据不存在
     * 2. 字典数据被禁用
     *
     * @param dictType 字典类型
     * @param values 字典数据值的数组
     */
    void validDictDatas(String dictType, Collection<String> values);

    /**
     * 获得指定的字典数据
     *
     * @param dictType 字典类型
     * @param value 字典数据值
     * @return 字典数据
     */
    DictDataDO getDictData(String dictType, String value);

    /**
     * 解析获得指定的字典数据，从缓存中
     *
     * @param dictType 字典类型
     * @param label 字典数据标签
     * @return 字典数据
     */
    DictDataDO parseDictData(String dictType, String label);
}
