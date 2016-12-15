package com.skp.payment.p2plending.backoffice.crowd;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/crowd/{userId}")
public class CrowdController {

    @Autowired
    private CrowdService crowdService;

    @RequestMapping(value="/authenticate", method = RequestMethod.POST)
    CrowdModel.Result authentication(@PathVariable String userId, @RequestBody CrowdModel.Requset requset ){

        CrowdModel.Result result = crowdService.authenticate(requset);

        return result;


    }

    @RequestMapping(value="/info", method = RequestMethod.POST)
    CrowdModel.Result info(@PathVariable String userId, @RequestBody CrowdModel.Requset requset ){

        CrowdModel.Result result = crowdService.info(requset);

        return result;


    }

}
