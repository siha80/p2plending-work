package com.skp.fintech.p2plending.backoffice.menu.ui;

import com.skp.fintech.p2plending.backoffice.menu.application.MenuInformation;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

public class MenuEvent {

    @Data
    @EqualsAndHashCode(callSuper=false)
    public static class UserMenuRequest{

    }

    @Data
    @EqualsAndHashCode(callSuper=false)
    public static class UserMenuResponse{
        private String resultCd;
        private String resultMsg;
        private String menuInfoJsonString;
    }

    @Data
    @EqualsAndHashCode(callSuper=false)
    public static class SystemMenuRequest{
        private String menuId;
        private String menuNm;
        private String path;
    }

    @Data
    @EqualsAndHashCode(callSuper=false)
    public static class SystemMenuResponse {
        private String resultCd;
        private String resultMsg;
        private List<MenuInfo> menuList;
    }

    public static class MenuInfo extends MenuInformation{

    }

}
