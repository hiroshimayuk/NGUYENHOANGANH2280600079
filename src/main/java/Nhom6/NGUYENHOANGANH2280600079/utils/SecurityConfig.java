package Nhom6.NGUYENHOANGANH2280600079.utils;

import Nhom6.NGUYENHOANGANH2280600079.services.OAuthService;
import Nhom6.NGUYENHOANGANH2280600079.services.UserService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final OAuthService oAuthService;
    private final UserService userService;
    private final ClientRegistrationRepository clientRegistrationRepository;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public UserDetailsService userDetailsService() {
        return userService;
    }

    // ĐÃ XÓA bean passwordEncoder() ở đây để tránh vòng lặp. 
    // Nó đã được chuyển sang AppConfig.java

    @Bean
    public DaoAuthenticationProvider authenticationProvider(PasswordEncoder passwordEncoder) {
        var auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(userDetailsService());
        // Sử dụng passwordEncoder được tiêm vào từ tham số hàm (lấy từ AppConfig)
        auth.setPasswordEncoder(passwordEncoder); 
        return auth;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(@NotNull HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Các đường dẫn công khai
                        .requestMatchers("/css/**", "/js/**", "/", "/oauth/**", "/register", "/error", "/login").permitAll()
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        
                        // --- QUYỀN HẠN CHO CATEGORY (Chỉ ADMIN) ---
                        .requestMatchers("/categories/**").hasAuthority("ADMIN")

                        // Quyền hạn cho Books
                        .requestMatchers("/books/edit/**", "/books/add", "/books/delete")
                        .hasAuthority("ADMIN")
                        .requestMatchers("/books", "/cart", "/cart/**")
                        .hasAnyAuthority("ADMIN", "USER")

                        // Quyền hạn cho API
                        .requestMatchers("/api/**")
                        .hasAnyAuthority("ADMIN", "USER")

                        // Các yêu cầu còn lại phải đăng nhập
                        .anyRequest().authenticated()
                )
                // Thêm Filter JWT trước UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login")
                        .deleteCookies("JSESSIONID")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .permitAll()
                )
                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/")
                        .failureUrl("/login?error")
                        .permitAll()
                )
                .oauth2Login(oauth2Login -> oauth2Login
                        .loginPage("/login")
                        .failureUrl("/login?error")
                        .authorizationEndpoint(authorization -> authorization
                                .authorizationRequestResolver(authorizationRequestResolver(clientRegistrationRepository))
                        )
                        .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint
                                .userService(oAuthService)
                        )
                        .successHandler((request, response, authentication) -> {
                            var oauth2User = (OAuth2User) authentication.getPrincipal();
                            userService.saveOauthUser(oauth2User.getAttribute("email"), oauth2User.getAttribute("name"));
                            response.sendRedirect("/");
                        })
                        .permitAll()
                )
                .rememberMe(rememberMe -> rememberMe
                        .key("hutech")
                        .rememberMeCookieName("hutech")
                        .tokenValiditySeconds(24 * 60 * 60)
                        .userDetailsService(userDetailsService())
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .accessDeniedPage("/403")
                )
                .sessionManagement(sessionManagement -> sessionManagement
                        .maximumSessions(1)
                        .expiredUrl("/login")
                )
                .httpBasic(httpBasic -> httpBasic
                        .realmName("hutech")
                )
                .build();
    }

    private OAuth2AuthorizationRequestResolver authorizationRequestResolver(ClientRegistrationRepository clientRegistrationRepository) {
        DefaultOAuth2AuthorizationRequestResolver authorizationRequestResolver =
                new DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository, "/oauth2/authorization");

        authorizationRequestResolver.setAuthorizationRequestCustomizer(
                authorizationRequest -> authorizationRequest
                        .additionalParameters(params -> params.put("prompt", "select_account")));

        return authorizationRequestResolver;
    }
}