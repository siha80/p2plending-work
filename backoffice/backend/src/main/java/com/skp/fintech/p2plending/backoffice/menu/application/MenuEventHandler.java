package com.skp.fintech.p2plending.backoffice.menu.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skp.fintech.p2plending.backoffice.common.util.FileUtils;
import com.skp.fintech.p2plending.backoffice.common.util.HttpClientManager;
import com.skp.fintech.p2plending.backoffice.infra.token.Token;
import com.skp.fintech.p2plending.backoffice.infra.token.TokenHandler;
import com.skp.fintech.p2plending.backoffice.mapper.plpdb.MenuMapper;
import com.skp.fintech.p2plending.backoffice.menu.ui.MenuEvent;

import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@Service
public class MenuEventHandler implements MenuService{

    private static Logger logger = LoggerFactory.getLogger(MenuEventHandler.class);

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    MenuMapper menuMapper;

    @Autowired
    TokenHandler tokenHandler;


    @Override
    @Transactional(value="transactionManagerForMybatisPlpdb", readOnly = true)
    public MenuEvent.SystemMenuResponse getSystemMenuList(MenuEvent.SystemMenuRequest request){

        MenuEvent.SystemMenuResponse res = new MenuEvent.SystemMenuResponse();

        try{

            MenuInformation menuInformation = modelMapper.map(request, MenuInformation.class);

            List<MenuInformation> menuList = menuMapper.selectSystemMenuList(menuInformation);

            res.setMenuList(modelMapper.map(menuList, List.class));

        } catch(Exception e) {
            e.printStackTrace();
        }

        return res;
    }

    @Override
    @Transactional(value="transactionManagerForMybatisPlpdb", readOnly = true)
    public MenuEvent.UserMenuResponse getUserMenuList(MenuEvent.UserMenuRequest request){

        MenuEvent.UserMenuResponse res = new MenuEvent.UserMenuResponse();
        try{

            Token token = tokenHandler.getToken(HttpClientManager.getRequest().getHeader("Authorization"));

            List<MenuInformation> menuList = menuMapper.selectUserMenuList(token.getUserId());

            String menu = convertUserMenuListToJson(menuList);

            res.setMenuInfoJsonString(menu);
        } catch(Exception e) {
            e.printStackTrace();
        }

        return res;
    }

    private String convertUserMenuListToJson(List<MenuInformation> menuList){
        String result = "";

        List<HashMap> treeList = new ArrayList<>();
        HashMap level1 = new HashMap();
        List<HashMap> children = new ArrayList<>();
        int childNodeCnt = 0;

        for(MenuInformation menuInfo: menuList){

            if(menuInfo.getLevel() == 1 ){
                childNodeCnt = menuInfo.getChildNodeCnt();

                HashMap menu = new HashMap();
                menu.put("title",menuInfo.getMenuNm());
                menu.put("icon",menuInfo.getIcon());
                menu.put("selected",false);
                menu.put("expanded",false);

                HashMap data = new HashMap();
                data.put("menu",menu);

                level1 = new HashMap();
                level1.put("path",menuInfo.getPath());
                level1.put("data",data);

                children = new ArrayList<>();
            } else {
                HashMap menu = new HashMap();
                menu.put("title",menuInfo.getMenuNm());

                HashMap data = new HashMap();
                data.put("menu",menu);

                HashMap level2 = new HashMap();
                level2.put("path", menuInfo.getPath());
                level2.put("data", data);

                children.add(level2);
            }

            if(childNodeCnt == children.size()){

                treeList.add(level1);
                if(children.size()>0){
                    level1.put("children",children);
                }
            }
        }

        HashMap menu = new HashMap();
        menu.put("path","pages");
        menu.put("children",treeList);

        try {
            result = objectMapper.writeValueAsString(menu);
        } catch (Exception e){

        }

        return result;
    }
}
