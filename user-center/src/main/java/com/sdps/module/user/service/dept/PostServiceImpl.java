package com.sdps.module.user.service.dept;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.sdps.common.exception.util.ServiceExceptionUtil;
import com.sdps.common.pojo.PageResult;
import com.sdps.module.system.dal.dataobject.dept.PostDO;
import com.sdps.module.system.enums.ErrorCodeConstants;
import com.sdps.module.user.controller.admin.dept.vo.post.PostCreateReqVO;
import com.sdps.module.user.controller.admin.dept.vo.post.PostExportReqVO;
import com.sdps.module.user.controller.admin.dept.vo.post.PostPageReqVO;
import com.sdps.module.user.controller.admin.dept.vo.post.PostUpdateReqVO;
import com.sdps.module.user.convert.dept.PostConvert;
import com.sdps.module.user.dal.mapper.dept.PostMapper;

/**
 * 岗位 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class PostServiceImpl implements PostService {

	@Autowired
	private PostMapper postMapper;

	@Override
	public Long createPost(PostCreateReqVO reqVO) {
		// 校验正确性
		this.checkCreateOrUpdate(null, reqVO.getName(), reqVO.getCode());
		// 插入岗位
		PostDO post = PostConvert.INSTANCE.convert(reqVO);
		postMapper.insert(post);
		return post.getId();
	}

	@Override
	public void updatePost(PostUpdateReqVO reqVO) {
		// 校验正确性
		this.checkCreateOrUpdate(reqVO.getId(), reqVO.getName(),
				reqVO.getCode());
		// 更新岗位
		PostDO updateObj = PostConvert.INSTANCE.convert(reqVO);
		postMapper.updateById(updateObj);
	}

	@Override
	public void deletePost(Long id) {
		// 校验是否存在
		this.checkPostExists(id);
		// 删除部门
		postMapper.deleteById(id);
	}

	@Override
	public List<PostDO> getPosts(Collection<Long> ids,
			Collection<Integer> statuses) {
		return postMapper.selectList(ids, statuses);
	}

	@Override
	public PageResult<PostDO> getPostPage(PostPageReqVO reqVO) {
		return postMapper.selectPage(reqVO);
	}

	@Override
	public List<PostDO> getPosts(PostExportReqVO reqVO) {
		return postMapper.selectList(reqVO);
	}

	@Override
	public PostDO getPost(Long id) {
		return postMapper.selectById(id);
	}

	private void checkCreateOrUpdate(Long id, String name, String code) {
		// 校验自己存在
		checkPostExists(id);
		// 校验岗位名的唯一性
		checkPostNameUnique(id, name);
		// 校验岗位编码的唯一性
		checkPostCodeUnique(id, code);
	}

	private void checkPostNameUnique(Long id, String name) {
		PostDO post = postMapper.selectByName(name);
		if (post == null) {
			return;
		}
		// 如果 id 为空，说明不用比较是否为相同 id 的岗位
		if (id == null) {
			throw ServiceExceptionUtil
					.exception(ErrorCodeConstants.POST_NAME_DUPLICATE);
		}
		if (!post.getId().equals(id)) {
			throw ServiceExceptionUtil
					.exception(ErrorCodeConstants.POST_NAME_DUPLICATE);
		}
	}

	private void checkPostCodeUnique(Long id, String code) {
		PostDO post = postMapper.selectByCode(code);
		if (post == null) {
			return;
		}
		// 如果 id 为空，说明不用比较是否为相同 id 的岗位
		if (id == null) {
			throw ServiceExceptionUtil
					.exception(ErrorCodeConstants.POST_CODE_DUPLICATE);
		}
		if (!post.getId().equals(id)) {
			throw ServiceExceptionUtil
					.exception(ErrorCodeConstants.POST_CODE_DUPLICATE);
		}
	}

	private void checkPostExists(Long id) {
		if (id == null) {
			return;
		}
		PostDO post = postMapper.selectById(id);
		if (post == null) {
			throw ServiceExceptionUtil
					.exception(ErrorCodeConstants.POST_NOT_FOUND);
		}
	}

}
