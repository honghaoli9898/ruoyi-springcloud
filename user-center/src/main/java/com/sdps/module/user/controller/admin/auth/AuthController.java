package com.sdps.module.user.controller.admin.auth;


import com.sdps.common.enums.CommonStatusEnum;
import com.sdps.common.model.dataobject.permission.MenuDO;
import com.sdps.common.model.dataobject.permission.RoleDO;
import com.sdps.common.model.dataobject.user.AdminUserDO;
import com.sdps.common.pojo.CommonResult;
import com.sdps.common.tenant.core.aop.TenantIgnore;
import com.sdps.common.util.collection.SetUtils;
import com.sdps.common.util.web.WebFrameworkUtils;
import com.sdps.module.system.enums.permission.MenuTypeEnum;
import com.sdps.module.system.service.permission.SysPermissionService;
import com.sdps.module.system.service.permission.SysRoleService;
import com.sdps.module.system.service.user.SysAdminUserService;
import com.sdps.module.user.controller.admin.auth.vo.AuthMenuRespVO;
import com.sdps.module.user.controller.admin.auth.vo.AuthPermissionInfoRespVO;
import com.sdps.module.user.controller.admin.auth.vo.AuthSmsSendReqVO;
import com.sdps.module.user.convert.auth.AuthConvert;
import com.sdps.module.user.service.auth.AdminAuthService;
import com.sdps.module.user.service.permission.PermissionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.validation.Valid;
import java.util.List;
import java.util.Set;

import static java.util.Collections.singleton;

@Api(tags = "管理后台 - 认证")
@RestController
@RequestMapping("/system/auth")
@Validated
@Slf4j
public class AuthController {

    @Resource
    private SysAdminUserService sysUserService;
    @Resource
    private SysRoleService sysRoleService;
    @Resource
    private SysPermissionService sysPermissionService;
    @Resource
    private PermissionService permissionService;
    @Resource
    private AdminAuthService authService;


    @GetMapping("/get-permission-info")
    @ApiOperation("获取登录用户的权限信息")
    public CommonResult<AuthPermissionInfoRespVO> getPermissionInfo() {
        // 获得用户信息
        AdminUserDO user = sysUserService.getUser(WebFrameworkUtils.getLoginUserId());
        if (user == null) {
            return null;
        }
        // 获得角色列表
        Set<Long> roleIds = sysPermissionService.getUserRoleIdsFromCache(WebFrameworkUtils.getLoginUserId(), singleton(CommonStatusEnum.ENABLE.getStatus()));
        List<RoleDO> roleList = sysRoleService.getRolesFromCache(roleIds);
        // 获得菜单列表
        List<MenuDO> menuList = permissionService.getRoleMenuListFromCache(roleIds,
                SetUtils.asSet(MenuTypeEnum.DIR.getType(), MenuTypeEnum.MENU.getType(), MenuTypeEnum.BUTTON.getType()),
                singleton(CommonStatusEnum.ENABLE.getStatus())); // 只要开启的
        // 拼接结果返回
        return CommonResult.success(AuthConvert.INSTANCE.convert(user, roleList, menuList));
    }

    @GetMapping("/list-menus")
    @ApiOperation("获得登录用户的菜单列表")
    public CommonResult<List<AuthMenuRespVO>> getMenus() {
        // 获得角色列表
        Set<Long> roleIds = sysPermissionService.getUserRoleIdsFromCache(WebFrameworkUtils.getLoginUserId(), singleton(CommonStatusEnum.ENABLE.getStatus()));
        // 获得用户拥有的菜单列表
        List<MenuDO> menuList = permissionService.getRoleMenuListFromCache(roleIds,
                SetUtils.asSet(MenuTypeEnum.DIR.getType(), MenuTypeEnum.MENU.getType()), // 只要目录和菜单类型
                singleton(CommonStatusEnum.ENABLE.getStatus())); // 只要开启的
        // 转换成 Tree 结构返回
        return CommonResult.success(AuthConvert.INSTANCE.buildMenuTree(menuList));
    }

    @PostMapping("/send-sms-code")
    @TenantIgnore
    @PermitAll
    @ApiOperation(value = "发送手机验证码")
    public CommonResult<Boolean> sendLoginSmsCode(@RequestBody @Valid AuthSmsSendReqVO reqVO) {
        authService.sendSmsCode(reqVO);
        return CommonResult.success(true);
    }

    @GetMapping("/sms-login")
    @PermitAll
    @TenantIgnore
    @ApiOperation(value = "手机验证码登录")
    public CommonResult<AdminUserDO> smsLogin(@RequestParam("mobile") String mobile, @RequestParam("code") String code) {
        return CommonResult.success(authService.smsLogin(mobile, code));
    }
}
