package com.example.CVRUK_backend.authentication.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.CVRUK_backend.authentication.security.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class securityConfig {

        private final JwtAuthenticationFilter jwtAuthenticationFilter;
        private final UserDetailsService userDetailsService;
        private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
        private final RestAccessDeniedHandler restAccessDeniedHandler;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .cors(cors -> {})
                                .csrf(AbstractHttpConfigurer::disable)
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .exceptionHandling(ex -> ex
                                                .authenticationEntryPoint(restAuthenticationEntryPoint)
                                                .accessDeniedHandler(restAccessDeniedHandler))
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(
                                                                "/api/auth/**",
                                                                "/v3/api-docs/**",
                                                                "/swagger-ui/**",
                                                                "/swagger-ui.html")
                                                .permitAll()
                                                .requestMatchers(HttpMethod.GET,
                                                                "/api/v1/events/**",
                                                                "/api/v1/news-latest/**",
                                                                "/api/v1/courses/**", //courses 
                                                                "/api/v1/sports/**")
                                                .permitAll()
                                                .requestMatchers(HttpMethod.POST,
                                                                "/api/v1/events/**",
                                                                "/api/v1/news-latest/**",
                                                                "/api/v1/courses/**", //courses 
                                                                "/api/v1/sports/**")
                                                .hasRole("ADMIN")
                                                .requestMatchers(HttpMethod.PUT,
                                                                "/api/v1/events/**",
                                                                "/api/v1/news-latest/**",
                                                                "/api/v1/courses/**", //courses 
                                                                "/api/v1/sports/**")
                                                .hasRole("ADMIN")
                                                .requestMatchers(HttpMethod.DELETE,
                                                                "/api/v1/events/**",                                                
                                                                "/api/v1/news-latest/**",
                                                                "/api/v1/courses/**", //courses 
                                                                "/api/v1/sports/**")
                                                .hasRole("ADMIN")
                                                .anyRequest().authenticated())
                                .authenticationProvider(authenticationProvider())
                                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

        @Bean

        public DaoAuthenticationProvider authenticationProvider() {

                DaoAuthenticationProvider provider = new DaoAuthenticationProvider();

                provider.setUserDetailsService(userDetailsService);
                provider.setPasswordEncoder(passwordEncoder());

                return provider;
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
                return config.getAuthenticationManager();
        }
}
