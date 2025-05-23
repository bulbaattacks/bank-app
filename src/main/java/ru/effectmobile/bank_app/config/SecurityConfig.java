package ru.effectmobile.bank_app.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.http.HttpMethod.*;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;
import static ru.effectmobile.bank_app.entity.User.Role.ADMIN;
import static ru.effectmobile.bank_app.entity.User.Role.USER;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String[] WHITE_LIST_URL = {
            "/auth/**",
            "/error",
            "/account",
            "/v2/api-docs",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui/**",
            "/webjars/**",
            "/swagger-ui.html"
    };

    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req ->
                        req.requestMatchers(WHITE_LIST_URL)
                                .permitAll()
                                .requestMatchers("/user/**").hasAnyRole(ADMIN.name(), USER.name())
                                .requestMatchers(GET, "/cards").hasAnyRole(ADMIN.name())
                                .requestMatchers(GET, "/cards/{id}").hasAnyRole(ADMIN.name(), USER.name())
                                .requestMatchers(POST, "/cards/**").hasAnyRole(ADMIN.name())
                                .requestMatchers(PATCH, "/cards/**").hasAnyRole(ADMIN.name())
                                .requestMatchers(DELETE, "/cards/**").hasAnyRole(ADMIN.name())
                                .requestMatchers("/card_to_block/block").hasAnyRole(ADMIN.name())
                                .requestMatchers("/card_to_block/add").hasAnyRole(ADMIN.name(), USER.name())
                                .requestMatchers(GET, "/transaction_history").hasAnyRole(ADMIN.name(), USER.name())
                                .requestMatchers(POST, "/transaction").hasAnyRole(ADMIN.name(), USER.name())
                                .requestMatchers(POST, "/deposit").hasAnyRole(ADMIN.name(), USER.name())
                                .requestMatchers(POST, "/add_limit").hasAnyRole(ADMIN.name())
                                .anyRequest()
                                .authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
