package hr.algebra.photostore.security;

import hr.algebra.photostore.exceptions.SecurityConfigException;
import hr.algebra.photostore.filter.JwtAuthFilter;
import hr.algebra.photostore.service.MyUserDetailsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String LOGIN_PAGE      = "/login";
    private static final String ROLE_ADMIN      = "ROLE_ADMIN";
    private static final String ADMIN_DASHBOARD = "/admin/dashboard";

    private final MyUserDetailsService userDetailsService;
    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(MyUserDetailsService userDetailsService,
                          JwtAuthFilter jwtAuthFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    @Order(1)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http)
            throws SecurityConfigException {
        try {
            http
                    .securityMatcher("/api/**")
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers("/api/auth/**").permitAll()
                            .requestMatchers("/api/admin/**").hasRole("ADMIN")
                            .requestMatchers("/swagger-ui/**", "/api-docs/**",
                                    "/swagger-ui.html").permitAll()
                            .requestMatchers(HttpMethod.GET, "/api/pictures/**").permitAll()
                            .anyRequest().authenticated()
                    )
                    .csrf(csrf -> csrf.disable())
                    .sessionManagement(session ->
                            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .requestCache(cache -> cache.disable())
                    .securityContext(ctx -> ctx.requireExplicitSave(true))
                    .exceptionHandling(ex -> ex
                            .authenticationEntryPoint((req, res, authEx) -> {
                                res.setContentType("application/json");
                                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                res.getWriter().write("{\"error\":\"Unauthorized\"}");
                            })
                    )
                    .addFilterBefore(jwtAuthFilter,
                            UsernamePasswordAuthenticationFilter.class);

            return http.build();
        } catch (Exception e) {
            throw new SecurityConfigException("Failed to configure API security filter chain", e);
        }
    }

    @Bean
    @Order(2)
    public SecurityFilterChain mvcSecurityFilterChain(HttpSecurity http)
            throws SecurityConfigException {
        try {
            http
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers("/", "/categories/**",
                                    "/pictures/**", "/cart/**").permitAll()
                            .requestMatchers(LOGIN_PAGE, "/register").permitAll()
                            .requestMatchers("/h2-console/**").permitAll()
                            .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                            .requestMatchers("/admin/**").hasRole("ADMIN")
                            .requestMatchers("/user/**").hasRole("USER")
                            .requestMatchers("/swagger-ui/**", "/api-docs/**",
                                    "/swagger-ui.html").permitAll()
                            .anyRequest().authenticated()
                    )
                    .formLogin(form -> form
                            .loginPage(LOGIN_PAGE)
                            .loginProcessingUrl(LOGIN_PAGE)
                            .usernameParameter("email")
                            .successHandler(this::handleLoginSuccess)
                            .failureUrl(LOGIN_PAGE + "?error")
                            .permitAll()
                    )
                    .logout(logout -> logout
                            .logoutSuccessUrl("/")
                            .permitAll()
                    )
                    .csrf(csrf -> csrf
                            .ignoringRequestMatchers("/h2-console/**")
                    )
                    .headers(headers -> headers
                            .frameOptions(frame -> frame.sameOrigin())
                    );

            return http.build();
        } catch (Exception e) {
            throw new SecurityConfigException("Failed to configure MVC security filter chain", e);
        }
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http)
            throws SecurityConfigException {
        try {
            AuthenticationManagerBuilder builder =
                    http.getSharedObject(AuthenticationManagerBuilder.class);
            builder.userDetailsService(userDetailsService)
                    .passwordEncoder(passwordEncoder());
            return builder.build();
        } catch (Exception e) {
            throw new SecurityConfigException("Failed to configure AuthenticationManager", e);
        }
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public FilterRegistrationBean<JwtAuthFilter> jwtFilterRegistration(JwtAuthFilter filter) {
        FilterRegistrationBean<JwtAuthFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }

    private void handleLoginSuccess(HttpServletRequest request,
                                    HttpServletResponse response,
                                    Authentication authentication) throws IOException {
        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(a -> ROLE_ADMIN.equals(a.getAuthority()));

        response.sendRedirect(isAdmin ? ADMIN_DASHBOARD : "/");
    }
}