// UserInitializer class (Optional coordinator)
package tn.esprit.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserInitializer implements ApplicationRunner {

    private final AdminUserBuilder adminUserBuilder;
    private final RegularUserBuilder regularUserBuilder;

    @Override
    public void run(ApplicationArguments args) {
        // Delegate to specialized builders
        adminUserBuilder.run(args);
        regularUserBuilder.run(args);

        log.info("All users initialized successfully");
    }
}