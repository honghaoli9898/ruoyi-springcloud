package com.sdps.module.system.service.dict;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import cn.hutool.core.collection.CollUtil;

import com.sdps.common.enums.CommonStatusEnum;
import com.sdps.common.exception.util.ServiceExceptionUtil;
import com.sdps.common.util.collection.CollectionUtils;
import com.sdps.module.system.dal.dataobject.dict.DictDataDO;
import com.sdps.module.system.dal.mapper.dict.SysDictDataMapper;
import com.sdps.module.system.enums.ErrorCodeConstants;


/**
 * 字典数据 Service 实现类
 *
 * @author ruoyi
 */
@Service
public class SysDictDataServiceImpl implements SysDictDataService {

    /**
     * 排序 dictType > sort
     */
    @SuppressWarnings("unused")
	private static final Comparator<DictDataDO> COMPARATOR_TYPE_AND_SORT = Comparator
            .comparing(DictDataDO::getDictType)
            .thenComparingInt(DictDataDO::getSort);


    @Resource
    private SysDictDataMapper sysDictDataMapper;

    @Override
    public void validDictDatas(String dictType, Collection<String> values) {
        if (CollUtil.isEmpty(values)) {
            return;
        }
        Map<String, DictDataDO> dictDataMap = CollectionUtils.convertMap(
                sysDictDataMapper.selectByDictTypeAndValues(dictType, values), DictDataDO::getValue);
        // 校验
        values.forEach(value -> {
            DictDataDO dictData = dictDataMap.get(value);
            if (dictData == null) {
                throw ServiceExceptionUtil.exception(ErrorCodeConstants.DICT_DATA_NOT_EXISTS);
            }
            if (!CommonStatusEnum.ENABLE.getStatus().equals(dictData.getStatus())) {
                throw ServiceExceptionUtil.exception(ErrorCodeConstants.DICT_DATA_NOT_ENABLE, dictData.getLabel());
            }
        });
    }

    @Override
    public DictDataDO getDictData(String dictType, String value) {
        return sysDictDataMapper.selectByDictTypeAndValue(dictType, value);
    }

    @Override
    public DictDataDO parseDictData(String dictType, String label) {
        return sysDictDataMapper.selectByDictTypeAndLabel(dictType, label);
    }

}
