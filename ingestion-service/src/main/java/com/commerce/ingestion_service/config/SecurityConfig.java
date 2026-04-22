package com.commerce.ingestion_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()
                .anyRequest().authenticated()
            )
            .httpBasic(withDefaults());
        
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails acmeUser = User.withUsername("admin@acme.com")
            .password("{noop}password123")
            .roles("ADMIN")
            .build();

        UserDetails globexUser = User.withUsername("admin@globex.com")
            .password("{noop}password456")
            .roles("ADMIN")
            .build();

        return new InMemoryUserDetailsManager(acmeUser, globexUser);
    }
}
