package com.sdps.module.user.controller.admin.permission;

import static com.sdps.common.pojo.CommonResult.success;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

import java.util.Comparator;
import java.util.List;

import javax.annotation.Resource;
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
import com.sdps.common.model.dataobject.permission.MenuDO;
import com.sdps.common.pojo.CommonResult;
import com.sdps.module.user.controller.admin.permission.vo.menu.MenuCreateReqVO;
import com.sdps.module.user.controller.admin.permission.vo.menu.MenuListReqVO;
import com.sdps.module.user.controller.admin.permission.vo.menu.MenuRespVO;
import com.sdps.module.user.controller.admin.permission.vo.menu.MenuSimpleRespVO;
import com.sdps.module.user.controller.admin.permission.vo.menu.MenuUpdateReqVO;
import com.sdps.module.user.convert.permission.MenuConvert;
import com.sdps.module.user.service.permission.MenuService;
import com.sdps.module.user.service.tenant.TenantService;

@Api(tags = "管理后台 - 菜单")
@RestController
@RequestMapping("/system/menu")
@Validated
public class MenuController {

	@Resource
	private MenuService menuService;
	@Resource
	private TenantService tenantService;

	@PostMapping("/create")
	@ApiOperation("创建菜单")
	@PreAuthorize("@ss.hasPermission('system:menu:create')")
	public CommonResult<Long> createMenu(
			@Valid @RequestBody MenuCreateReqVO reqVO) {
		Long menuId = menuService.createMenu(reqVO);
		return success(menuId);
	}

	@PutMapping("/update")
	@ApiOperation("修改菜单")
	@PreAuthorize("@ss.hasPermission('system:menu:update')")
	public CommonResult<Boolean> updateMenu(
			@Valid @RequestBody MenuUpdateReqVO reqVO) {
		menuService.updateMenu(reqVO);
		return success(true);
	}

	@DeleteMapping("/delete")
	@ApiOperation("删除菜单")
	@ApiImplicitParam(name = "id", value = "角色编号", required = true, example = "1024", dataTypeClass = Long.class)
	@PreAuthorize("@ss.hasPermission('system:menu:delete')")
	public CommonResult<Boolean> deleteMenu(@RequestParam("id") Long id) {
		menuService.deleteMenu(id);
		return success(true);
	}

	@GetMapping("/list")
	@ApiOperation(value = "获取菜单列表", notes = "用于【菜单管理】界面")
	@PreAuthorize("@ss.hasPermission('system:menu:query')")
	public CommonResult<List<MenuRespVO>> getMenus(MenuListReqVO reqVO) {
		List<MenuDO> list = menuService.getMenus(reqVO);
		list.sort(Comparator.comparing(MenuDO::getSort));
		return success(MenuConvert.INSTANCE.convertList(list));
	}

	@GetMapping("/list-all-simple")
	@ApiOperation(value = "获取菜单精简信息列表", notes = "只包含被开启的菜单，用于【角色分配菜单】功能的选项。"
			+ "在多租户的场景下，会只返回租户所在套餐有的菜单")
	public CommonResult<List<MenuSimpleRespVO>> getSimpleMenus() {
		// 获得菜单列表，只要开启状态的
		MenuListReqVO reqVO = new MenuListReqVO();
		reqVO.setStatus(CommonStatusEnum.ENABLE.getStatus());
		List<MenuDO> list = menuService.getTenantMenus(reqVO);
		// 排序后，返回给前端
		list.sort(Comparator.comparing(MenuDO::getSort));
		return success(MenuConvert.INSTANCE.convertList02(list));
	}

	@GetMapping("/get")
	@ApiOperation("获取菜单信息")
	@PreAuthorize("@ss.hasPermission('system:menu:query')")
	public CommonResult<MenuRespVO> getMenu(Long id) {
		MenuDO menu = menuService.getMenu(id);
		return success(MenuConvert.INSTANCE.convert(menu));
	}

}
