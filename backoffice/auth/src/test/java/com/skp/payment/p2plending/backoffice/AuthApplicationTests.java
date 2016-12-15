package com.skp.payment.p2plending.backoffice;

import com.skp.payment.p2plending.backoffice.crowd.CrowdService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AuthApplicationTests {

	@MockBean
	private CrowdService crowdService;

	@Test
	public void contextLoads() {

	//	crowdService.getUser("skp_lpf", "skp_lpf");
	}

}
