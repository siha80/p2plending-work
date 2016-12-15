package com.skp.payment.p2plending.user.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skp.payment.p2plending.user.application.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class UserController {
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    UserService userService;

    @RequestMapping(value = "/v1/users/signup", method = RequestMethod.POST)
    public ResponseEntity singUp(@RequestBody @Valid SignUpEvent.Request request) throws Exception {
        SignUpEvent.Response res = userService.singup(request);
        HttpStatus s = HttpStatus.CREATED;
        if(res.expected()) {
            s = res.getError().getHttpStatus();
        }
        return new ResponseEntity<>(res, s);
    }


    @RequestMapping(value = "/v1/users/authentication", method = RequestMethod.POST)
    public ResponseEntity loginFromUser(@RequestBody @Valid LoginEvent.Request request) throws Exception {
        LoginEvent.Response.ToUser res = userService.loginFromUser(request);
        HttpStatus s = HttpStatus.OK;
        if(res.expected()) {
            s = res.getError().getHttpStatus();
        }
        return new ResponseEntity<>(res, s);
    }

}
