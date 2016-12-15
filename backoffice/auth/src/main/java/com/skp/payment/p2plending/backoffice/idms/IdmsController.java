package com.skp.payment.p2plending.backoffice.idms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/idms/{userId}")
public class IdmsController {

    @Autowired
    private IdmsService idmsService;


    @RequestMapping(value="/authenticate",  method = RequestMethod.POST)
    IdmsModel.ResponseIdms authenticate(@PathVariable String userId, @RequestBody IdmsModel.RequestIdms requset ){

        IdmsModel.ResponseIdms response = idmsService.authenticate(requset);

        return response;
    }
}
