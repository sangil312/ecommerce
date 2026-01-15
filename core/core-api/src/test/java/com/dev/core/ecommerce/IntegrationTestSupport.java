package com.dev.core.ecommerce;

import org.junit.jupiter.api.Tag;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@Tag("integration")
@ActiveProfiles("local-test")
@SpringBootTest(classes = CoreApiApplication.class)
public abstract class IntegrationTestSupport {
}
