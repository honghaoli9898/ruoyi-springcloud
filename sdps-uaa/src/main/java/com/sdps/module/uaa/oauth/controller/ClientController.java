package com.sdps.module.uaa.oauth.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Maps;
import com.sdps.common.pojo.CommonResult;
import com.sdps.common.pojo.PageResult;
import com.sdps.module.uaa.oauth.dto.ClientDto;
import com.sdps.module.uaa.oauth.model.Client;
import com.sdps.module.uaa.oauth.service.IClientService;

/**
 * 应用相关接口
 *
 * @author zlt
 *         <p>
 *         Blog: https://zlt2000.gitee.io Github: https://github.com/zlt2000
 */

@RestController
@RequestMapping("/clients")
public class ClientController {
	@Autowired
	private IClientService clientService;

	@GetMapping("/list")
	public PageResult<Client> list(@RequestParam Map<String, Object> params) {
		return clientService.listClient(params, true);
	}

	@GetMapping("/{id}")
	public Client get(@PathVariable Long id) {
		return clientService.getById(id);
	}

	@GetMapping("/all")
	public CommonResult<List<Client>> allClient() {
		PageResult<Client> page = clientService.listClient(Maps.newHashMap(),
				false);
		return CommonResult.success(page.getList());
	}

	@DeleteMapping("/{id}")
	public void delete(@PathVariable Long id) {
		clientService.delClient(id);
	}

	@SuppressWarnings("rawtypes")
	@PostMapping("/saveOrUpdate")
	public CommonResult saveOrUpdate(@RequestBody ClientDto clientDto)
			throws Exception {
		return clientService.saveClient(clientDto);
	}
}
