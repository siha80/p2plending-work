package com.skp.fintech.p2plending.backoffice.menu.application;

import com.skp.fintech.p2plending.backoffice.menu.ui.MenuEvent;

public interface MenuService {

    MenuEvent.SystemMenuResponse getSystemMenuList(MenuEvent.SystemMenuRequest request) throws Exception;

    MenuEvent.UserMenuResponse getUserMenuList(MenuEvent.UserMenuRequest request) throws Exception;

}
