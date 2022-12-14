package com.sdps.module.user.controller.admin.user;

import static com.sdps.common.pojo.CommonResult.success;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import cn.hutool.core.collection.CollUtil;

import com.sdps.common.annotation.LoginUser;
import com.sdps.common.enums.CommonStatusEnum;
import com.sdps.common.model.dataobject.user.AdminUserDO;
import com.sdps.common.model.user.LoginAppUser;
import com.sdps.common.pojo.CommonResult;
import com.sdps.common.pojo.PageResult;
import com.sdps.common.util.collection.CollectionUtils;
import com.sdps.common.util.collection.MapUtils;
import com.sdps.common.util.excel.ExcelUtils;
import com.sdps.module.system.dal.dataobject.dept.DeptDO;
import com.sdps.module.system.enums.common.SexEnum;
import com.sdps.module.system.service.user.SysAdminUserService;
import com.sdps.module.user.controller.admin.user.vo.user.UserCreateReqVO;
import com.sdps.module.user.controller.admin.user.vo.user.UserExcelVO;
import com.sdps.module.user.controller.admin.user.vo.user.UserExportReqVO;
import com.sdps.module.user.controller.admin.user.vo.user.UserImportExcelVO;
import com.sdps.module.user.controller.admin.user.vo.user.UserImportRespVO;
import com.sdps.module.user.controller.admin.user.vo.user.UserPageItemRespVO;
import com.sdps.module.user.controller.admin.user.vo.user.UserPageReqVO;
import com.sdps.module.user.controller.admin.user.vo.user.UserRespVO;
import com.sdps.module.user.controller.admin.user.vo.user.UserSimpleRespVO;
import com.sdps.module.user.controller.admin.user.vo.user.UserUpdatePasswordReqVO;
import com.sdps.module.user.controller.admin.user.vo.user.UserUpdateReqVO;
import com.sdps.module.user.controller.admin.user.vo.user.UserUpdateStatusReqVO;
import com.sdps.module.user.convert.user.UserConvert;
import com.sdps.module.user.service.dept.DeptService;
import com.sdps.module.user.service.user.AdminUserService;

@Api(tags = "???????????? - ??????")
@RestController
@RequestMapping("/system/user")
@Validated
public class UserController {

	@Autowired
	private AdminUserService userService;
	@Autowired
	private SysAdminUserService sysUserService;
	@Autowired
	private DeptService deptService;

	@PostMapping("/create")
	@ApiOperation("????????????")
	@PreAuthorize("@ss.hasPermission('system:user:create')")
	public CommonResult<Long> createUser(
			@Valid @RequestBody UserCreateReqVO reqVO) {
		Long id = userService.createUser(reqVO);
		return CommonResult.success(id);
	}

	@PutMapping("update")
	@ApiOperation("????????????")
	@PreAuthorize("@ss.hasPermission('system:user:update')")
	public CommonResult<Boolean> updateUser(
			@Valid @RequestBody UserUpdateReqVO reqVO) {
		userService.updateUser(reqVO);
		return success(true);
	}

	@DeleteMapping("/delete")
	@ApiOperation("????????????")
	@ApiImplicitParam(name = "id", value = "??????", required = true, example = "1024", dataTypeClass = Long.class)
	@PreAuthorize("@ss.hasPermission('system:user:delete')")
	public CommonResult<Boolean> deleteUser(@RequestParam("id") Long id) {
		userService.deleteUser(id);
		return success(true);
	}

	@PutMapping("/update-password")
	@ApiOperation("??????????????????")
	@PreAuthorize("@ss.hasPermission('system:user:update-password')")
	public CommonResult<Boolean> updateUserPassword(
			@Valid @RequestBody UserUpdatePasswordReqVO reqVO) {
		userService.updateUserPassword(reqVO.getId(), reqVO.getPassword());
		return success(true);
	}

	@PutMapping("/update-status")
	@ApiOperation("??????????????????")
	@PreAuthorize("@ss.hasPermission('system:user:update')")
	public CommonResult<Boolean> updateUserStatus(
			@Valid @RequestBody UserUpdateStatusReqVO reqVO) {
		userService.updateUserStatus(reqVO.getId(), reqVO.getStatus());
		return success(true);
	}

	@GetMapping("/page")
	@ApiOperation("????????????????????????")
	@PreAuthorize("@ss.hasPermission('system:user:list')")
	public CommonResult<PageResult<UserPageItemRespVO>> getUserPage(
			@Valid UserPageReqVO reqVO) {
		// ????????????????????????
		PageResult<AdminUserDO> pageResult = userService.getUserPage(reqVO);
		if (CollUtil.isEmpty(pageResult.getList())) {
			return success(new PageResult<>(pageResult.getTotal())); // ?????????
		}

		// ???????????????????????????
		Collection<Long> deptIds = CollectionUtils.convertList(
				pageResult.getList(), AdminUserDO::getDeptId);
		Map<Long, DeptDO> deptMap = deptService.getDeptMap(deptIds);
		// ??????????????????
		List<UserPageItemRespVO> userList = new ArrayList<>(pageResult
				.getList().size());
		pageResult.getList().forEach(
				user -> {
					UserPageItemRespVO respVO = UserConvert.INSTANCE
							.convert(user);
					respVO.setDept(UserConvert.INSTANCE.convert(deptMap
							.get(user.getDeptId())));
					userList.add(respVO);
				});
		return success(new PageResult<>(userList, pageResult.getTotal()));
	}

	@GetMapping("/list-all-simple")
	@ApiOperation(value = "??????????????????????????????", notes = "???????????????????????????????????????????????????????????????")
	public CommonResult<List<UserSimpleRespVO>> getSimpleUsers() {
		// ??????????????????????????????????????????
		List<AdminUserDO> list = userService
				.getUsersByStatus(CommonStatusEnum.ENABLE.getStatus());
		// ???????????????????????????
		return success(UserConvert.INSTANCE.convertList04(list));
	}

	@GetMapping("/get")
	@ApiOperation("??????????????????")
	@ApiImplicitParam(name = "id", value = "??????", required = true, example = "1024", dataTypeClass = Long.class)
	@PreAuthorize("@ss.hasPermission('system:user:query')")
	public CommonResult<UserRespVO> getInfo(@RequestParam("id") Long id) {
		return success(UserConvert.INSTANCE.convert(sysUserService.getUser(id)));
	}

	@GetMapping("/export")
	@ApiOperation("????????????")
	@PreAuthorize("@ss.hasPermission('system:user:export')")
	public void exportUsers(@Validated UserExportReqVO reqVO,
			HttpServletResponse response) throws IOException {
		// ??????????????????
		List<AdminUserDO> users = userService.getUsers(reqVO);

		// ???????????????????????????
		Collection<Long> deptIds = CollectionUtils.convertList(users,
				AdminUserDO::getDeptId);
		Map<Long, DeptDO> deptMap = deptService.getDeptMap(deptIds);
		Map<Long, AdminUserDO> deptLeaderUserMap = userService
				.getUserMap(CollectionUtils.convertSet(deptMap.values(),
						DeptDO::getLeaderUserId));
		// ????????????
		List<UserExcelVO> excelUsers = new ArrayList<>(users.size());
		users.forEach(user -> {
			UserExcelVO excelVO = UserConvert.INSTANCE.convert02(user);
			// ????????????
			MapUtils.findAndThen(deptMap, user.getDeptId(),
					dept -> {
						excelVO.setDeptName(dept.getName());
						// ??????????????????????????????
					MapUtils.findAndThen(deptLeaderUserMap, dept
							.getLeaderUserId(),
							deptLeaderUser -> excelVO
									.setDeptLeaderNickname(deptLeaderUser
											.getNickname()));
				});
			excelUsers.add(excelVO);
		});

		// ??????
		ExcelUtils.write(response, "????????????.xls", "????????????", UserExcelVO.class,
				excelUsers);
	}

	@GetMapping("/get-import-template")
	@ApiOperation("????????????????????????")
	public void importTemplate(HttpServletResponse response) throws IOException {
		// ?????????????????? demo
		List<UserImportExcelVO> list = Arrays.asList(
				UserImportExcelVO.builder().username("yunai").deptId(1L)
						.email("yunai@iocoder.cn").mobile("15601691300")
						.nickname("??????")
						.status(CommonStatusEnum.ENABLE.getStatus())
						.sex(SexEnum.MALE.getSex()).build(),
				UserImportExcelVO.builder().username("yuanma").deptId(2L)
						.email("yuanma@iocoder.cn").mobile("15601701300")
						.nickname("??????")
						.status(CommonStatusEnum.DISABLE.getStatus())
						.sex(SexEnum.FEMALE.getSex()).build());

		// ??????
		ExcelUtils.write(response, "??????????????????.xls", "????????????",
				UserImportExcelVO.class, list);
	}

	@PostMapping("/import")
	@ApiOperation("????????????")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "file", value = "Excel ??????", required = true, dataTypeClass = MultipartFile.class),
			@ApiImplicitParam(name = "updateSupport", value = "?????????????????????????????? false", example = "true", dataTypeClass = Boolean.class) })
	@PreAuthorize("@ss.hasPermission('system:user:import')")
	public CommonResult<UserImportRespVO> importExcel(
			@RequestParam("file") MultipartFile file,
			@RequestParam(value = "updateSupport", required = false, defaultValue = "false") Boolean updateSupport)
			throws Exception {
		List<UserImportExcelVO> list = ExcelUtils.read(file,
				UserImportExcelVO.class);
		return success(userService.importUsers(list, updateSupport));
	}

	@GetMapping(value = "/login", params = "username")
	public LoginAppUser findByUsername(String username) {
		return userService.findByUsername(username);
	}

	/**
	 * ?????????????????? LoginAppUser
	 *
	 * @return
	 */
	@GetMapping("/current")
	public CommonResult<LoginAppUser> getLoginAppUser(
			@LoginUser(isFull = true) AdminUserDO user) {
		LoginAppUser loginAppUser = userService.getLoginAppUser(user);
		loginAppUser.setPassword(null);
		return CommonResult.success(loginAppUser);
	}

	@GetMapping(value = "/name/{username}")
	public AdminUserDO getUserByUsername(
			@PathVariable("username") String username) {
		AdminUserDO adminUserDO = userService.getUserByUsername(username);
		adminUserDO.setPassword(null);
		return adminUserDO;
	}

	@GetMapping(value = "/mobile")
	public AdminUserDO findByMobile(@RequestParam("mobile") String mobile) {
		AdminUserDO adminUserDO = userService.getUserByMobile(mobile);
		return adminUserDO;
	}

	@GetMapping(value = "/name", params = "userId")
	public AdminUserDO selectByUserId(@RequestParam("userId") String userId) {
		AdminUserDO adminUserDO = userService.selectByUserId(userId);
		adminUserDO.setPassword(null);
		return adminUserDO;
	}
}
