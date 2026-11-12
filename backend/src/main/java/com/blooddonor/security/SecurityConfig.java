
package com.blooddonor.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    CorsConfigurationSource corsSource() {

        CorsConfiguration c = new CorsConfiguration();

        c.setAllowedOriginPatterns(List.of(
            "https://capestone-project-24-bai-70710.vercel.app",
            "https://*.vercel.app",
            "http://localhost:*",
            "http://127.0.0.1:*"
        ));

        c.setAllowedMethods(List.of(
            "GET", "POST", "PUT", "PATCH",
            "DELETE", "OPTIONS"
        ));

        c.setAllowedHeaders(List.of("*"));
        c.setAllowCredentials(false);
        c.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource src =
            new UrlBasedCorsConfigurationSource();

        src.registerCorsConfiguration("/**", c);

        return src;
    }

    @Bean
    SecurityFilterChain filterChain(
        HttpSecurity http,
        JwtUtil jwt
    ) throws Exception {

        http
            .csrf(c -> c.disable())
            .cors(Customizer.withDefaults())
            .sessionManagement(s ->
                s.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .exceptionHandling(e ->
                e.authenticationEntryPoint((req, res, ex) ->
                    res.sendError(401, "Login required")
                )
            )
            .authorizeHttpRequests(a -> a
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/donors/search",
                    "/api/banks"
                ).permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/banks/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .addFilterBefore(
                new JwtFilter(jwt),
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }
}
