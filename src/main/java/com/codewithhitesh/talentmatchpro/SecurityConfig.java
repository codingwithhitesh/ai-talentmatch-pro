package com.codewithhitesh.talentmatchpro;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Disable CSRF for local REST API testing & H2 Console
                .csrf(csrf -> csrf.disable())

                // 2. Allow all requests during development
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                )

                // 3. Allow frames for H2 Console UI
                .headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }

    // 💡 THIS STOPS SPRING BOOT FROM GENERATING A PASSWORD
    @Bean
    public UserDetailsService userDetailsService() {
        return new InMemoryUserDetailsManager();
    }
}




/*package com.codewithhitesh.talentmatchpro;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Disable CSRF (useful for local REST API testing & H2 Console)
                .csrf(csrf -> csrf.disable())

                // 2. Configure access rules
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/index.html", "/static/**", "/css/**", "/js/**").permitAll() // Allow static assets
                        .requestMatchers("/h2-console/**").permitAll() // Allow H2 database UI
                        .requestMatchers("/api/**").permitAll()        // Allow your backend API endpoints
                        .anyRequest().permitAll()                      // Permits all requests during development
                )

                // 3. Allow frames so the H2 Console UI works properly
                .headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }
}



/*
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }
}

 */