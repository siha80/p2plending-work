package com.skp.fintech.p2plending.backoffice.mapper.plpdb;

import com.skp.fintech.p2plending.backoffice.menu.application.MenuInformation;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MenuMapper {

    List<MenuInformation> selectUserMenuList(String userId);

    List<MenuInformation> selectSystemMenuList(MenuInformation menuInformation);

}
