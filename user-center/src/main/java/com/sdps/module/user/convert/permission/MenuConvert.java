package com.sdps.module.user.convert.permission;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.sdps.common.model.dataobject.permission.MenuDO;
import com.sdps.module.user.controller.admin.permission.vo.menu.MenuCreateReqVO;
import com.sdps.module.user.controller.admin.permission.vo.menu.MenuRespVO;
import com.sdps.module.user.controller.admin.permission.vo.menu.MenuSimpleRespVO;
import com.sdps.module.user.controller.admin.permission.vo.menu.MenuUpdateReqVO;

@Mapper
public interface MenuConvert {

    MenuConvert INSTANCE = Mappers.getMapper(MenuConvert.class);

    List<MenuRespVO> convertList(List<MenuDO> list);

    MenuDO convert(MenuCreateReqVO bean);

    MenuDO convert(MenuUpdateReqVO bean);

    MenuRespVO convert(MenuDO bean);

    List<MenuSimpleRespVO> convertList02(List<MenuDO> list);

}
