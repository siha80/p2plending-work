package com.skp.fintech.p2plending.backoffice.menu.ui;

import com.skp.fintech.p2plending.backoffice.menu.application.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/v1/menu")
public class MenuController {

    @Autowired
    MenuService menuService;

    @CrossOrigin(maxAge = 3600)
    @RequestMapping(value = "/user/list", method = RequestMethod.POST, produces = {"application/json"})
    public ResponseEntity getUserMenuList(@RequestBody @Valid MenuEvent.UserMenuRequest request) throws Exception {
        MenuEvent.UserMenuResponse res = menuService.getUserMenuList(request);

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @CrossOrigin(maxAge = 3600)
    @RequestMapping(value = "/system/list", method = RequestMethod.POST, produces = {"application/json"})
    public ResponseEntity getSystemMenuList(@RequestBody @Valid MenuEvent.SystemMenuRequest request) throws Exception {
        MenuEvent.SystemMenuResponse res = menuService.getSystemMenuList(request);

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

}
