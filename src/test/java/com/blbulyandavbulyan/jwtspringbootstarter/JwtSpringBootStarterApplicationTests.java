package com.blbulyandavbulyan.jwtspringbootstarter;

import com.blbulyandavbulyan.jwtspringbootstarter.configs.JwtConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = JwtConfiguration.class)
class JwtSpringBootStarterApplicationTests {

    @Test
    void contextLoads() {
    }

}
