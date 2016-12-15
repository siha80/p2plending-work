package com.skp.fintech.p2plending.backoffice.menu.application;

import lombok.Data;

@Data
public class MenuInformation {

    private String menuId;
    private String menuNm;
    private String parentMenuId;
    private String path;
    private String fullPath;
    private String icon;
    private int level;
    private int childNodeCnt;

}
