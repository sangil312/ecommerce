package com.dev.core.ecommerce;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("local-test")
@SpringBootTest(classes = CoreApiApplication.class)
public abstract class IntegrationTestSupport {
}
