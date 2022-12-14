package com.sdps.module.user.controller.admin.user;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.List;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import cn.hutool.core.collection.CollUtil;

import com.sdps.common.datapermission.core.annotation.DataPermission;
import com.sdps.common.exception.util.ServiceExceptionUtil;
import com.sdps.common.model.dataobject.permission.RoleDO;
import com.sdps.common.model.dataobject.user.AdminUserDO;
import com.sdps.common.pojo.CommonResult;
import com.sdps.common.util.web.WebFrameworkUtils;
import com.sdps.module.system.dal.dataobject.dept.DeptDO;
import com.sdps.module.system.dal.dataobject.dept.PostDO;
import com.sdps.module.system.enums.ErrorCodeConstants;
import com.sdps.module.system.service.dept.SysDeptService;
import com.sdps.module.system.service.permission.SysRoleService;
import com.sdps.module.system.service.user.SysAdminUserService;
import com.sdps.module.user.controller.admin.user.vo.profile.UserProfileRespVO;
import com.sdps.module.user.controller.admin.user.vo.profile.UserProfileUpdatePasswordReqVO;
import com.sdps.module.user.controller.admin.user.vo.profile.UserProfileUpdateReqVO;
import com.sdps.module.user.convert.user.UserConvert;
import com.sdps.module.user.service.dept.DeptService;
import com.sdps.module.user.service.dept.PostService;
import com.sdps.module.user.service.permission.PermissionService;
import com.sdps.module.user.service.permission.RoleService;
import com.sdps.module.user.service.user.AdminUserService;

@Api(tags = "???????????? - ??????????????????")
@RestController
@RequestMapping("/system/user/profile")
@Validated
public class UserProfileController {

	@Resource
	private AdminUserService userService;
	@Resource
	private SysAdminUserService sysUserService;
	@Resource
	private DeptService deptService;
	@Resource
	private SysDeptService sysDeptService;
	@Resource
	private PostService postService;
	@Resource
	private PermissionService permissionService;
	@Resource
	private RoleService roleService;
	@Resource
	private SysRoleService sysRoleService;

	@GetMapping("/get")
	@ApiOperation("????????????????????????")
	@DataPermission(enable = false)
	// ?????????????????????????????????????????????????????????????????????
	public CommonResult<UserProfileRespVO> profile() {
		// ????????????????????????
		AdminUserDO user = sysUserService.getUser(WebFrameworkUtils
				.getLoginUserId());
		UserProfileRespVO resp = UserConvert.INSTANCE.convert03(user);
		// ??????????????????
		List<RoleDO> userRoles = sysRoleService
				.getRolesFromCache(permissionService
						.getUserRoleIdListByUserId(user.getId()));
		resp.setRoles(UserConvert.INSTANCE.convertList(userRoles));
		// ??????????????????
		if (user.getDeptId() != null) {
			DeptDO dept = sysDeptService.getDept(user.getDeptId());
			resp.setDept(UserConvert.INSTANCE.convert02(dept));
		}
		// ??????????????????
		if (CollUtil.isNotEmpty(user.getPostIds())) {
			List<PostDO> posts = postService.getPosts(user.getPostIds());
			resp.setPosts(UserConvert.INSTANCE.convertList02(posts));
		}
		// ????????????????????????
		// List<SocialUserDO> socialUsers =
		// socialService.getSocialUserList(user.getId(),
		// UserTypeEnum.ADMIN.getValue());
		// resp.setSocialUsers(UserConvert.INSTANCE.convertList03(socialUsers));
		return CommonResult.success(resp);
	}

	@PutMapping("/update")
	@ApiOperation("????????????????????????")
	public CommonResult<Boolean> updateUserProfile(
			@Valid @RequestBody UserProfileUpdateReqVO reqVO) {
		userService
				.updateUserProfile(WebFrameworkUtils.getLoginUserId(), reqVO);
		return CommonResult.success(true);
	}

	@PutMapping("/update-password")
	@ApiOperation("????????????????????????")
	public CommonResult<Boolean> updateUserProfilePassword(
			@Valid @RequestBody UserProfileUpdatePasswordReqVO reqVO) {
		userService.updateUserPassword(WebFrameworkUtils.getLoginUserId(),
				reqVO);
		return CommonResult.success(true);
	}

	@RequestMapping(value = "/update-avatar", method = { RequestMethod.POST,
			RequestMethod.PUT })
	// ?????? uni-app ????????? Put ?????????????????????
	@ApiOperation("????????????????????????")
	public CommonResult<String> updateUserAvatar(
			@RequestParam("avatarFile") MultipartFile file) throws Exception {
		if (file.isEmpty()) {
			throw ServiceExceptionUtil
					.exception(ErrorCodeConstants.FILE_IS_EMPTY);
		}
		String avatar = userService.updateUserAvatar(
				WebFrameworkUtils.getLoginUserId(), file.getInputStream());
		return CommonResult.success(avatar);
	}

}
