package com.sdps.module.uaa.oauth.service;

import java.util.Map;

import com.sdps.common.pojo.PageResult;
import com.sdps.module.uaa.oauth.model.TokenVo;

/**
 * @author zlt
 */
public interface ITokensService {
    /**
     * 查询token列表
     * @param params 请求参数
     * @param clientId 应用id
     */
    PageResult<TokenVo> listTokens(Map<String, Object> params, String clientId);
}
