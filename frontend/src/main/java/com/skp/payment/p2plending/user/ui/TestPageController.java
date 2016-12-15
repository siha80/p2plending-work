package com.skp.payment.p2plending.user.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skp.payment.p2plending.launcher.domain.UserAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by 1002375 on 16. 11. 3..
 */
@Controller
public class TestPageController {
    @Autowired
    ObjectMapper objectMapper;

    @RequestMapping("/hello")
    @ResponseBody
    String testHello() {
        return "Hello, World";
    }

    @RequestMapping("/test")
    public String testPageHello(ModelAndView modelAndView) {
        return "test";
    }

    @RequestMapping("/pages/v1/testtoken")
    public @ResponseBody String testToken(UserAuthenticationToken userAuthenticationToken) throws Exception {
        return objectMapper.writeValueAsString(userAuthenticationToken);
    }
}
