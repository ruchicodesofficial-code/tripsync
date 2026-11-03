package com.tripsync.config;

import com.tripsync.user.entity.AccountStatus;
import com.tripsync.user.entity.Role;
import com.tripsync.user.entity.User;
import com.tripsync.user.repository.UserRepository;
import com.tripsync.user.service.AdminBootstrapService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@Configuration
public class AdminBootstrapConfiguration {
    private static final Logger log = LoggerFactory.getLogger(AdminBootstrapConfiguration.class);

    @Bean
    @ConditionalOnProperty(prefix = "app.admin-bootstrap",name = "enabled",havingValue = "true")
    CommandLineRunner createInitialAdmin(AdminBootstrapService adminBootstrapService,
                                         @Value("${app.admin-bootstrap.email:}")String email,
                                         @Value("${app.admin-bootstrap.password:}")String password,
                                         @Value("${app.admin-bootstrap.full-name:TripSync Admin}")String fullName
                                         ){
        return args -> adminBootstrapService.createInitialAdmin(email,password,fullName);

    }

}
