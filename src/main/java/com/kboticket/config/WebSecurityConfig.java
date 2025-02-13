package com.kboticket.config;

import com.kboticket.common.filter.JwtTokenRenewalFilter;
import com.kboticket.common.filter.JwtAuthenticationFilter;
import com.kboticket.common.filter.TokenAuthenticationFilter;
import com.kboticket.config.jwt.JwtTokenProvider;
import com.kboticket.service.login.LogoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig extends SecurityConfigurerAdapter {

    private final UserDetailsService userService;
    private final LogoutService logoutService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, Object> redisTemplate;

    @Bean
    public WebSecurityCustomizer configure() {
        return (web) -> web.ignoring()
            .requestMatchers(new AntPathRequestMatcher("/static/**"));
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        JwtAuthenticationFilter jwtAuthenticationFilter =
            new JwtAuthenticationFilter(jwtTokenProvider, authenticationManager(), redisTemplate);

        return http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login",
                    "/api/user/**",
                    "/api/sms/**",
                    "/terms/**",
                    "/game/**",
                    "/seat/**", "/payment-page", "/favicon.ico",
                    "/ticket-page/**", "/game/queue-status/**"
                ).permitAll()
                .anyRequest().authenticated())
            .logout(logout -> logout
                .logoutUrl("/logout")
                .addLogoutHandler(logoutService)
                .logoutSuccessHandler(
                    (request, response, authentication) -> SecurityContextHolder.clearContext()))
            .csrf(AbstractHttpConfigurer::disable)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(new JwtTokenRenewalFilter(jwtTokenProvider, redisTemplate), JwtAuthenticationFilter.class)
            .addFilterBefore(new TokenAuthenticationFilter(jwtTokenProvider, redisTemplate), JwtTokenRenewalFilter.class)
            .build();
    }

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return new ProviderManager(authProvider);
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
