package com.sdps.module.user.controller.admin.permission;

import static com.sdps.common.pojo.CommonResult.success;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sdps.common.enums.CommonStatusEnum;
import com.sdps.common.model.dataobject.permission.RoleDO;
import com.sdps.common.pojo.CommonResult;
import com.sdps.common.pojo.PageResult;
import com.sdps.common.util.excel.ExcelUtils;
import com.sdps.module.user.controller.admin.permission.vo.role.RoleCreateReqVO;
import com.sdps.module.user.controller.admin.permission.vo.role.RoleExcelVO;
import com.sdps.module.user.controller.admin.permission.vo.role.RoleExportReqVO;
import com.sdps.module.user.controller.admin.permission.vo.role.RolePageReqVO;
import com.sdps.module.user.controller.admin.permission.vo.role.RoleRespVO;
import com.sdps.module.user.controller.admin.permission.vo.role.RoleSimpleRespVO;
import com.sdps.module.user.controller.admin.permission.vo.role.RoleUpdateReqVO;
import com.sdps.module.user.controller.admin.permission.vo.role.RoleUpdateStatusReqVO;
import com.sdps.module.user.convert.permission.RoleConvert;
import com.sdps.module.user.service.permission.RoleService;

@Api(tags = "???????????? - ??????")
@RestController
@RequestMapping("/system/role")
@Validated
public class RoleController {

	@Resource
	private RoleService roleService;

	@PostMapping("/create")
	@ApiOperation("????????????")
	@PreAuthorize("@ss.hasPermission('system:role:create')")
	public CommonResult<Long> createRole(
			@Valid @RequestBody RoleCreateReqVO reqVO) {
		return success(roleService.createRole(reqVO, null));
	}

	@PutMapping("/update")
	@ApiOperation("????????????")
	@PreAuthorize("@ss.hasPermission('system:role:update')")
	public CommonResult<Boolean> updateRole(
			@Valid @RequestBody RoleUpdateReqVO reqVO) {
		roleService.updateRole(reqVO);
		return success(true);
	}

	@PutMapping("/update-status")
	@ApiOperation("??????????????????")
	@PreAuthorize("@ss.hasPermission('system:role:update')")
	public CommonResult<Boolean> updateRoleStatus(
			@Valid @RequestBody RoleUpdateStatusReqVO reqVO) {
		roleService.updateRoleStatus(reqVO.getId(), reqVO.getStatus());
		return success(true);
	}

	@DeleteMapping("/delete")
	@ApiOperation("????????????")
	@ApiImplicitParam(name = "id", value = "????????????", required = true, example = "1024", dataTypeClass = Long.class)
	@PreAuthorize("@ss.hasPermission('system:role:delete')")
	public CommonResult<Boolean> deleteRole(@RequestParam("id") Long id) {
		roleService.deleteRole(id);
		return success(true);
	}

	@GetMapping("/get")
	@ApiOperation("??????????????????")
	@PreAuthorize("@ss.hasPermission('system:role:query')")
	public CommonResult<RoleRespVO> getRole(@RequestParam("id") Long id) {
		RoleDO role = roleService.getRole(id);
		return success(RoleConvert.INSTANCE.convert(role));
	}

	@GetMapping("/page")
	@ApiOperation("??????????????????")
	@PreAuthorize("@ss.hasPermission('system:role:query')")
	public CommonResult<PageResult<RoleDO>> getRolePage(RolePageReqVO reqVO) {
		return success(roleService.getRolePage(reqVO));
	}

	@GetMapping("/list-all-simple")
	@ApiOperation(value = "??????????????????????????????", notes = "???????????????????????????????????????????????????????????????")
	public CommonResult<List<RoleSimpleRespVO>> getSimpleRoles() {
		// ??????????????????????????????????????????
		List<RoleDO> list = roleService.getRoles(Collections
				.singleton(CommonStatusEnum.ENABLE.getStatus()));
		// ???????????????????????????
		list.sort(Comparator.comparing(RoleDO::getSort));
		return success(RoleConvert.INSTANCE.convertList02(list));
	}

	@GetMapping("/export")
	@PreAuthorize("@ss.hasPermission('system:role:export')")
	public void export(HttpServletResponse response,
			@Validated RoleExportReqVO reqVO) throws IOException {
		List<RoleDO> list = roleService.getRoleList(reqVO);
		List<RoleExcelVO> data = RoleConvert.INSTANCE.convertList03(list);
		// ??????
		ExcelUtils.write(response, "????????????.xls", "????????????", RoleExcelVO.class, data);
	}

}
