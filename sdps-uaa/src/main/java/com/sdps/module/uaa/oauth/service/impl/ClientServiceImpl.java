package com.sdps.module.uaa.oauth.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sdps.common.constant.SecurityConstants;
import com.sdps.common.lock.DistributedLock;
import com.sdps.common.mybatis.service.impl.SuperServiceImpl;
import com.sdps.common.pojo.CommonResult;
import com.sdps.common.pojo.PageResult;
import com.sdps.common.redis.template.RedisRepository;
import com.sdps.module.uaa.oauth.mapper.ClientMapper;
import com.sdps.module.uaa.oauth.model.Client;
import com.sdps.module.uaa.oauth.service.IClientService;

@Service
public class ClientServiceImpl extends SuperServiceImpl<ClientMapper, Client>
		implements IClientService {
	private final static String LOCK_KEY_CLIENTID = "clientId:";

	@Autowired
	private RedisRepository redisRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private DistributedLock lock;

	@Override
	public CommonResult saveClient(Client client) throws Exception {
		client.setClientSecret(passwordEncoder.encode(client
				.getClientSecretStr()));
		String clientId = client.getClientId();
		super.saveOrUpdateIdempotency(client, lock, LOCK_KEY_CLIENTID
				+ clientId,
				new QueryWrapper<Client>().eq("client_id", clientId), clientId
						+ "已存在");
		return CommonResult.success("操作成功");
	}

	@Override
	public PageResult<Client> listClient(Map<String, Object> params,
			boolean isPage) {
		Page<Client> page;
		if (isPage) {
			page = new Page<Client>(MapUtils.getInteger(params, "page"),
					MapUtils.getInteger(params, "limit"));
		} else {
			page = new Page<>(1, -1);
		}
		List<Client> list = baseMapper.findList(page, params);
		page.setRecords(list);
		return PageResult.<Client> builder().list(list).total(page.getTotal())
				.build();
	}

	@Override
	public void delClient(long id) {
		String clientId = baseMapper.selectById(id).getClientId();
		baseMapper.deleteById(id);
		redisRepository.del(clientRedisKey(clientId));
	}

	@Override
	public Client loadClientByClientId(String clientId) {
		QueryWrapper<Client> wrapper = Wrappers.query();
		wrapper.eq("client_id", clientId);
		return this.getOne(wrapper);
	}

	private String clientRedisKey(String clientId) {
		return SecurityConstants.CACHE_CLIENT_KEY + ":" + clientId;
	}
}
