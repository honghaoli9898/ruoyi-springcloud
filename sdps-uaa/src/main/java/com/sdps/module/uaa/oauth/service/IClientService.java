package com.sdps.module.uaa.oauth.service;

import java.util.Map;

import com.sdps.common.mybatis.service.ISuperService;
import com.sdps.common.pojo.CommonResult;
import com.sdps.common.pojo.PageResult;
import com.sdps.module.uaa.oauth.model.Client;

/**
 * @author zlt
 *         <p>
 *         Blog: https://zlt2000.gitee.io Github: https://github.com/zlt2000
 */
public interface IClientService extends ISuperService<Client> {
	CommonResult saveClient(Client clientDto) throws Exception;

	/**
	 * 查询应用列表
	 * 
	 * @param params
	 * @param isPage
	 *            是否分页
	 */
	PageResult<Client> listClient(Map<String, Object> params, boolean isPage);

	void delClient(long id);

	Client loadClientByClientId(String clientId);

}
