package com.example.securityex;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = {"spring.datasource.url=jdbc:h2:mem:sampledb;MODE=MySQL;DB_CLOSE_DELAY=-1"})
public class SpringSecurityAppTests {

	@Test
	public void contextLoads() {
	}

}
