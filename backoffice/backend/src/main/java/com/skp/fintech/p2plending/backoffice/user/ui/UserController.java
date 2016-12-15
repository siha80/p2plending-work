package com.skp.fintech.p2plending.backoffice.user.ui;

import com.skp.fintech.p2plending.backoffice.user.application.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/v1/users")
public class UserController {

    @Autowired
    UserService userService;

    @CrossOrigin(maxAge = 3600)
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity login(@RequestBody @Valid LoginEvent.Request request) throws Exception {
        LoginEvent.Response res = userService.login(request);
        HttpStatus s = HttpStatus.OK;
        if(res.expected()) {
            s = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(res, s);
    }

    @CrossOrigin(maxAge = 3600)
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ResponseEntity add(@RequestBody @Valid LoginEvent.Request request) throws Exception {
        LoginEvent.AddUserResponse res = userService.addUser(request);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
}
