package com.skp.fintech.p2plending.backoffice.menu.application;

import lombok.Data;

import java.util.List;


@Data
public class MenuTree {

    private String path;
    private Data data;
    private List<MenuTree> children;

    @lombok.Data
    private class Data{
        private Menu menu;
    }

    @lombok.Data
    private class Menu {
        private String title;
        private String icon;
        private boolean selected;
        private boolean expanded;
        private int order;
    }

}
