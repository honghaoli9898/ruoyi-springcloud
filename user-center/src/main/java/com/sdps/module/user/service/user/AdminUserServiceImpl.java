package com.sdps.module.user.service.user;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;

import com.google.common.annotations.VisibleForTesting;
import com.sdps.common.enums.CommonStatusEnum;
import com.sdps.common.exception.ServiceException;
import com.sdps.common.exception.util.ServiceExceptionUtil;
import com.sdps.common.model.dataobject.permission.RoleDO;
import com.sdps.common.model.dataobject.user.AdminUserDO;
import com.sdps.common.model.user.LoginAppUser;
import com.sdps.common.pojo.PageResult;
import com.sdps.common.util.collection.CollectionUtils;
import com.sdps.module.system.dal.dataobject.dept.DeptDO;
import com.sdps.module.system.dal.dataobject.dept.UserPostDO;
import com.sdps.module.system.dal.dataobject.permission.UserRoleDO;
import com.sdps.module.system.dal.mapper.dept.SysUserPostMapper;
import com.sdps.module.system.dal.mapper.user.SysAdminUserMapper;
import com.sdps.module.system.enums.ErrorCodeConstants;
import com.sdps.module.system.service.dept.SysDeptService;
import com.sdps.module.system.service.dept.SysPostService;
import com.sdps.module.user.controller.admin.user.vo.profile.UserProfileUpdatePasswordReqVO;
import com.sdps.module.user.controller.admin.user.vo.profile.UserProfileUpdateReqVO;
import com.sdps.module.user.controller.admin.user.vo.user.UserCreateReqVO;
import com.sdps.module.user.controller.admin.user.vo.user.UserExportReqVO;
import com.sdps.module.user.controller.admin.user.vo.user.UserImportExcelVO;
import com.sdps.module.user.controller.admin.user.vo.user.UserImportRespVO;
import com.sdps.module.user.controller.admin.user.vo.user.UserPageReqVO;
import com.sdps.module.user.controller.admin.user.vo.user.UserUpdateReqVO;
import com.sdps.module.user.convert.user.UserConvert;
import com.sdps.module.user.dal.mapper.dept.UserPostMapper;
import com.sdps.module.user.dal.mapper.permission.MenuMapper;
import com.sdps.module.user.dal.mapper.permission.RoleMapper;
import com.sdps.module.user.dal.mapper.permission.RoleMenuMapper;
import com.sdps.module.user.dal.mapper.permission.UserRoleMapper;
import com.sdps.module.user.dal.mapper.user.AdminUserMapper;
import com.sdps.module.user.service.dept.DeptService;
import com.sdps.module.user.service.dept.PostService;
import com.sdps.module.user.service.permission.PermissionService;
import com.sdps.module.user.service.tenant.TenantService;

/**
 * ???????????? Service ?????????
 * 
 * @author ????????????
 */
@Service
public class AdminUserServiceImpl implements AdminUserService {

	@Value("${sys.user.init-password:yudaoyuanma}")
	private String userInitPassword;
	@Autowired
	private AdminUserMapper userMapper;
	@Autowired
	private SysDeptService sysDeptService;
	@Autowired
	private DeptService deptService;
	@Autowired
	private PostService postService;
	@Autowired
	private SysPostService sysPostService;
	@Autowired
	private PermissionService permissionService;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	@Lazy
	private TenantService tenantService;
	@Autowired
	private UserRoleMapper userRoleMapper;
	@Autowired
	private RoleMapper roleMapper;
	@Autowired
	private UserPostMapper userPostMapper;
	@Autowired
	private RoleMenuMapper roleMenuMapper;
	@Autowired
	private MenuMapper menuMapper;
	@Autowired
	private SysAdminUserMapper sysUserMapper;
	@Autowired
	private SysUserPostMapper sysUserPostMapper;

	// @Resource
	// private FileApi fileApi;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Long createUser(UserCreateReqVO reqVO) {
		// ??????????????????
		tenantService.handleTenantInfo(tenant -> {
			long count = userMapper.selectCount();
			if (count >= tenant.getAccountCount()) {
				throw ServiceExceptionUtil.exception(
						ErrorCodeConstants.USER_COUNT_MAX,
						tenant.getAccountCount());
			}
		});
		// ???????????????
		checkCreateOrUpdate(null, reqVO.getUsername(), reqVO.getMobile(),
				reqVO.getEmail(), reqVO.getDeptId(), reqVO.getPostIds());
		// ????????????
		AdminUserDO user = UserConvert.INSTANCE.convert(reqVO);
		user.setStatus(CommonStatusEnum.ENABLE.getStatus()); // ????????????
		user.setPassword(encodePassword(reqVO.getPassword())); // ????????????
		userMapper.insert(user);
		// ??????????????????
		if (CollectionUtil.isNotEmpty(user.getPostIds())) {
			userPostMapper.insertBatch(CollectionUtils.convertList(
					user.getPostIds(), postId -> {
						UserPostDO userPostDO = new UserPostDO();
						userPostDO.setUserId(user.getId());
						userPostDO.setPostId(postId);
						return userPostDO;
					}));
		}
		return user.getId();
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateUser(UserUpdateReqVO reqVO) {
		// ???????????????
		checkCreateOrUpdate(reqVO.getId(), reqVO.getUsername(),
				reqVO.getMobile(), reqVO.getEmail(), reqVO.getDeptId(),
				reqVO.getPostIds());
		// ????????????
		AdminUserDO updateObj = UserConvert.INSTANCE.convert(reqVO);
		userMapper.updateById(updateObj);
		// ????????????
		updateUserPost(reqVO, updateObj);
	}

	private void updateUserPost(UserUpdateReqVO reqVO, AdminUserDO updateObj) {
		Long userId = reqVO.getId();
		Set<Long> dbPostIds = CollectionUtils.convertSet(
				userPostMapper.selectListByUserId(userId),
				UserPostDO::getPostId);
		// ????????????????????????????????????
		Set<Long> postIds = updateObj.getPostIds();
		Collection<Long> createPostIds = CollUtil.subtract(postIds, dbPostIds);
		Collection<Long> deletePostIds = CollUtil.subtract(dbPostIds, postIds);
		// ???????????????????????????????????????????????????????????????????????????
		if (!CollectionUtil.isEmpty(createPostIds)) {
			userPostMapper.insertBatch(CollectionUtils.convertList(
					createPostIds, postId -> {
						UserPostDO userPostDO = new UserPostDO();
						userPostDO.setUserId(userId);
						userPostDO.setPostId(postId);
						return userPostDO;
					}));
		}
		if (!CollectionUtil.isEmpty(deletePostIds)) {
			userPostMapper.deleteByUserIdAndPostId(userId, deletePostIds);
		}
	}

	@Override
	public void updateUserLogin(Long id, String loginIp) {
		AdminUserDO adminUserDO = new AdminUserDO();
		adminUserDO.setId(id);
		adminUserDO.setLoginIp(loginIp);
		adminUserDO.setLoginDate(new Date());
		userMapper.updateById(adminUserDO);
	}

	@Override
	public void updateUserProfile(Long id, UserProfileUpdateReqVO reqVO) {
		// ???????????????
		checkUserExists(id);
		checkEmailUnique(id, reqVO.getEmail());
		checkMobileUnique(id, reqVO.getMobile());
		// ????????????
		AdminUserDO adminUserDO = UserConvert.INSTANCE.convert(reqVO);
		adminUserDO.setId(id);
		userMapper.updateById(adminUserDO);
	}

	@Override
	public void updateUserPassword(Long id, UserProfileUpdatePasswordReqVO reqVO) {
		// ?????????????????????
		checkOldPassword(id, reqVO.getOldPassword());
		// ????????????
		AdminUserDO updateObj = new AdminUserDO();
		updateObj.setId(id);
		updateObj.setPassword(encodePassword(reqVO.getNewPassword())); // ????????????
		userMapper.updateById(updateObj);
	}

	@Override
	public String updateUserAvatar(Long id, InputStream avatarFile)
			throws Exception {
		checkUserExists(id);
		// ????????????
		String avatar = "";// fileApi.createFile(IoUtil.readBytes(avatarFile));
		// ????????????
		AdminUserDO sysUserDO = new AdminUserDO();
		sysUserDO.setId(id);
		sysUserDO.setAvatar(avatar);
		userMapper.updateById(sysUserDO);
		return avatar;
	}

	@Override
	public void updateUserPassword(Long id, String password) {
		// ??????????????????
		checkUserExists(id);
		// ????????????
		AdminUserDO updateObj = new AdminUserDO();
		updateObj.setId(id);
		updateObj.setPassword(encodePassword(password)); // ????????????
		userMapper.updateById(updateObj);
	}

	@Override
	public void updateUserStatus(Long id, Integer status) {
		// ??????????????????
		checkUserExists(id);
		// ????????????
		AdminUserDO updateObj = new AdminUserDO();
		updateObj.setId(id);
		updateObj.setStatus(status);
		userMapper.updateById(updateObj);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteUser(Long id) {
		// ??????????????????
		checkUserExists(id);
		// ????????????
		userMapper.deleteById(id);
		// ????????????????????????
		permissionService.processUserDeleted(id);
		// ??????????????????
		userPostMapper.deleteByUserId(id);
	}

	@Override
	public AdminUserDO getUserByUsername(String username) {
		return userMapper.selectByUsername(username);
	}

	@Override
	public AdminUserDO getUserByMobile(String mobile) {
		return userMapper.selectByMobile(mobile);
	}

	@Override
	public PageResult<AdminUserDO> getUserPage(UserPageReqVO reqVO) {
		return userMapper
				.selectPage(reqVO, getDeptCondition(reqVO.getDeptId()));
	}

	@Override
	public List<AdminUserDO> getUsersByDeptIds(Collection<Long> deptIds) {
		if (CollUtil.isEmpty(deptIds)) {
			return Collections.emptyList();
		}
		return sysUserMapper.selectListByDeptIds(deptIds);
	}

	@Override
	public List<AdminUserDO> getUsersByPostIds(Collection<Long> postIds) {
		if (CollUtil.isEmpty(postIds)) {
			return Collections.emptyList();
		}
		Set<Long> userIds = CollectionUtils.convertSet(
				sysUserPostMapper.selectListByPostIds(postIds),
				UserPostDO::getUserId);
		if (CollUtil.isEmpty(userIds)) {
			return Collections.emptyList();
		}
		return userMapper.selectBatchIds(userIds);
	}

	@Override
	public List<AdminUserDO> getUsers(Collection<Long> ids) {
		if (CollUtil.isEmpty(ids)) {
			return Collections.emptyList();
		}
		return userMapper.selectBatchIds(ids);
	}

	@Override
	public void validUsers(Set<Long> ids) {
		if (CollUtil.isEmpty(ids)) {
			return;
		}
		// ??????????????????
		List<AdminUserDO> users = userMapper.selectBatchIds(ids);
		Map<Long, AdminUserDO> userMap = CollectionUtils.convertMap(users,
				AdminUserDO::getId);
		// ??????
		ids.forEach(id -> {
			AdminUserDO user = userMap.get(id);
			if (user == null) {
				throw ServiceExceptionUtil
						.exception(ErrorCodeConstants.USER_NOT_EXISTS);
			}
			if (!CommonStatusEnum.ENABLE.getStatus().equals(user.getStatus())) {
				throw ServiceExceptionUtil.exception(
						ErrorCodeConstants.USER_IS_DISABLE, user.getNickname());
			}
		});
	}

	@Override
	public List<AdminUserDO> getUsers(UserExportReqVO reqVO) {
		return userMapper
				.selectList(reqVO, getDeptCondition(reqVO.getDeptId()));
	}

	@Override
	public List<AdminUserDO> getUsersByNickname(String nickname) {
		return userMapper.selectListByNickname(nickname);
	}

	@Override
	public List<AdminUserDO> getUsersByUsername(String username) {
		return userMapper.selectListByUsername(username);
	}

	/**
	 * ???????????????????????????????????????????????????????????????????????????
	 * 
	 * @param deptId
	 *            ????????????
	 * @return ??????????????????
	 */
	private Set<Long> getDeptCondition(Long deptId) {
		if (deptId == null) {
			return Collections.emptySet();
		}
		Set<Long> deptIds = CollectionUtils.convertSet(
				sysDeptService.getDeptsByParentIdFromCache(deptId, true),
				DeptDO::getId);
		deptIds.add(deptId); // ????????????
		return deptIds;
	}

	private void checkCreateOrUpdate(Long id, String username, String mobile,
			String email, Long deptId, Set<Long> postIds) {
		// ??????????????????
		checkUserExists(id);
		// ?????????????????????
		checkUsernameUnique(id, username);
		// ?????????????????????
		checkMobileUnique(id, mobile);
		// ??????????????????
		checkEmailUnique(id, email);
		// ??????????????????????????????
		sysDeptService.validDepts(CollectionUtils.singleton(deptId));
		// ??????????????????????????????
		sysPostService.validPosts(postIds);
	}

	@VisibleForTesting
	public void checkUserExists(Long id) {
		if (id == null) {
			return;
		}
		AdminUserDO user = userMapper.selectById(id);
		if (user == null) {
			throw ServiceExceptionUtil
					.exception(ErrorCodeConstants.USER_NOT_EXISTS);
		}
	}

	@VisibleForTesting
	public void checkUsernameUnique(Long id, String username) {
		if (StrUtil.isBlank(username)) {
			return;
		}
		AdminUserDO user = userMapper.selectByUsername(username);
		if (user == null) {
			return;
		}
		// ?????? id ?????????????????????????????????????????? id ?????????
		if (id == null) {
			throw ServiceExceptionUtil
					.exception(ErrorCodeConstants.USER_USERNAME_EXISTS);
		}
		if (!user.getId().equals(id)) {
			throw ServiceExceptionUtil
					.exception(ErrorCodeConstants.USER_USERNAME_EXISTS);
		}
	}

	@VisibleForTesting
	public void checkEmailUnique(Long id, String email) {
		if (StrUtil.isBlank(email)) {
			return;
		}
		AdminUserDO user = userMapper.selectByEmail(email);
		if (user == null) {
			return;
		}
		// ?????? id ?????????????????????????????????????????? id ?????????
		if (id == null) {
			throw ServiceExceptionUtil
					.exception(ErrorCodeConstants.USER_EMAIL_EXISTS);
		}
		if (!user.getId().equals(id)) {
			throw ServiceExceptionUtil
					.exception(ErrorCodeConstants.USER_EMAIL_EXISTS);
		}
	}

	@VisibleForTesting
	public void checkMobileUnique(Long id, String mobile) {
		if (StrUtil.isBlank(mobile)) {
			return;
		}
		AdminUserDO user = userMapper.selectByMobile(mobile);
		if (user == null) {
			return;
		}
		// ?????? id ?????????????????????????????????????????? id ?????????
		if (id == null) {
			throw ServiceExceptionUtil
					.exception(ErrorCodeConstants.USER_MOBILE_EXISTS);
		}
		if (!user.getId().equals(id)) {
			throw ServiceExceptionUtil
					.exception(ErrorCodeConstants.USER_MOBILE_EXISTS);
		}
	}

	/**
	 * ???????????????
	 * 
	 * @param id
	 *            ?????? id
	 * @param oldPassword
	 *            ?????????
	 */
	@VisibleForTesting
	public void checkOldPassword(Long id, String oldPassword) {
		AdminUserDO user = userMapper.selectById(id);
		if (user == null) {
			throw ServiceExceptionUtil
					.exception(ErrorCodeConstants.USER_NOT_EXISTS);
		}
		if (!isPasswordMatch(oldPassword, user.getPassword())) {
			throw ServiceExceptionUtil
					.exception(ErrorCodeConstants.USER_PASSWORD_FAILED);
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	// ??????????????????????????????????????????
	public UserImportRespVO importUsers(List<UserImportExcelVO> importUsers,
			boolean isUpdateSupport) {
		if (CollUtil.isEmpty(importUsers)) {
			throw ServiceExceptionUtil
					.exception(ErrorCodeConstants.USER_IMPORT_LIST_IS_EMPTY);
		}
		UserImportRespVO respVO = UserImportRespVO.builder()
				.createUsernames(new ArrayList<>())
				.updateUsernames(new ArrayList<>())
				.failureUsernames(new LinkedHashMap<>()).build();
		importUsers
				.forEach(importUser -> {
					// ??????????????????????????????????????????
					try {
						checkCreateOrUpdate(null, null, importUser.getMobile(),
								importUser.getEmail(), importUser.getDeptId(),
								null);
					} catch (ServiceException ex) {
						respVO.getFailureUsernames().put(
								importUser.getUsername(), ex.getMessage());
						return;
					}
					// ???????????????????????????????????????
					AdminUserDO existUser = userMapper
							.selectByUsername(importUser.getUsername());
					if (existUser == null) {
						AdminUserDO adminUserDO = UserConvert.INSTANCE
								.convert(importUser);
						adminUserDO
								.setPassword(encodePassword(userInitPassword));
						userMapper.insert(adminUserDO); // ??????????????????
						respVO.getCreateUsernames().add(
								importUser.getUsername());
						return;
					}
					// ???????????????????????????????????????
					if (!isUpdateSupport) {
						respVO.getFailureUsernames().put(
								importUser.getUsername(),
								ErrorCodeConstants.USER_USERNAME_EXISTS
										.getMsg());
						return;
					}
					AdminUserDO updateUser = UserConvert.INSTANCE
							.convert(importUser);
					updateUser.setId(existUser.getId());
					userMapper.updateById(updateUser);
					respVO.getUpdateUsernames().add(importUser.getUsername());
				});
		return respVO;
	}

	@Override
	public List<AdminUserDO> getUsersByStatus(Integer status) {
		return userMapper.selectListByStatus(status);
	}

	@Override
	public boolean isPasswordMatch(String rawPassword, String encodedPassword) {
		return passwordEncoder.matches(rawPassword, encodedPassword);
	}

	/**
	 * ?????????????????????
	 *
	 * @param password
	 *            ??????
	 * @return ??????????????????
	 */
	private String encodePassword(String password) {
		return passwordEncoder.encode(password);
	}

	@Override
	public LoginAppUser findByUsername(String username) {
		AdminUserDO adminUserDO = getUserByUsername(username);
		return getLoginAppUser(adminUserDO);
	}

	public LoginAppUser getLoginAppUser(AdminUserDO sysUser) {
		if (sysUser != null) {
			LoginAppUser loginAppUser = new LoginAppUser();
			BeanUtil.copyProperties(sysUser, loginAppUser);

			List<UserRoleDO> sysRoles = userRoleMapper
					.selectListByUserId(sysUser.getId());
			Set<Long> roleIds = sysRoles.stream().map(UserRoleDO::getRoleId)
					.collect(Collectors.toSet());
			List<RoleDO> roleDOs = roleMapper.selectBatchIds(roleIds);
			// ????????????
			loginAppUser.setRoles(roleDOs);

			// List<RoleMenuDO> menus = roleMenuMapper
			// .selectListByRoleIds(roleIds);
			// if (CollUtil.isNotEmpty(menus)) {
			// Set<Long> permissions = menus.stream()
			// .map(RoleMenuDO::getMenuId).collect(Collectors.toSet());
			// List<MenuDO> menuDOs = menuMapper.selectBatchIds(permissions);
			// // ??????????????????
			// loginAppUser
			// .setPermissions(menuDOs
			// .stream()
			// .filter(menuDO -> {
			// boolean isButton = menuDO.getType().equals(MenuTypeEnum.BUTTON
			// .getType());
			// return isButton;
			// }
			// ).map(MenuDO::getPermission)
			// .collect(Collectors.toSet()));
			// }
			return loginAppUser;
		}
		return null;
	}

	@Override
	public AdminUserDO selectByUserId(String userId) {
		return userMapper.selectById(userId);
	}

}
