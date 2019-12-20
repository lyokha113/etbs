package fpt.capstone.etbs.config;

import fpt.capstone.etbs.component.HttpCookieOAuth2AuthorizationRequest;
import fpt.capstone.etbs.component.JwtAuthenticationEntryPoint;
import fpt.capstone.etbs.component.OAuth2AuthenticationFailureHandler;
import fpt.capstone.etbs.component.OAuth2AuthenticationSuccessHandler;
import fpt.capstone.etbs.constant.RoleEnum;
import fpt.capstone.etbs.filter.JwtAuthenticationFilter;
import fpt.capstone.etbs.service.impl.CustomUserDetailsServiceImpl;
import fpt.capstone.etbs.service.impl.OAuth2UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  private CustomUserDetailsServiceImpl customUserDetailsService;

  @Autowired
  private OAuth2UserServiceImpl customOAuth2UserService;

  @Autowired
  private OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

  @Autowired
  private OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

  @Autowired
  private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

  @Bean
  public HttpCookieOAuth2AuthorizationRequest cookieAuthorizationRequestRepository() {
    return new HttpCookieOAuth2AuthorizationRequest();
  }

  @Bean
  public JwtAuthenticationFilter jwtAuthenticationFilter() {
    return new JwtAuthenticationFilter();
  }

  @Override
  public void configure(AuthenticationManagerBuilder authenticationManagerBuilder)
      throws Exception {
    authenticationManagerBuilder
        .userDetailsService(customUserDetailsService)
        .passwordEncoder(passwordEncoder());
  }

  @Bean(BeanIds.AUTHENTICATION_MANAGER)
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.cors()
        .and()
        .csrf()
        .disable()
        .formLogin()
        .disable()
        .httpBasic()
        .disable()
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .exceptionHandling()
        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
        .and()
        .oauth2Login()
        .authorizationEndpoint()
        .baseUri("/oauth2/authorize")
        .authorizationRequestRepository(cookieAuthorizationRequestRepository())
        .and()
        .redirectionEndpoint()
        .baseUri("/oauth2/callback/*")
        .and()
        .userInfoEndpoint()
        .userService(customOAuth2UserService)
        .and()
        .successHandler(oAuth2AuthenticationSuccessHandler)
        .failureHandler(oAuth2AuthenticationFailureHandler)
        .and()
        .authorizeRequests()
        // Swagger
        .antMatchers("/v2/api-docs", "/configuration/**", "/swagger*/**", "/webjars/**").permitAll()
        // All
        .antMatchers(HttpMethod.GET, "/category", "/template", "/template/*", "/tutorial",
            "/tutorial/*", "/ws-publish/*", "/ws-publish/**")
        .permitAll()
        .antMatchers(HttpMethod.POST, "/login", "/register")
        .permitAll()
        // User
        .antMatchers(HttpMethod.GET, "/rating", "/workspace", "/raw/*")
        .hasRole(RoleEnum.USER.getName())
        .antMatchers(HttpMethod.POST, "/rating", "/template", "/workspace", "/email/send/",
            "/rate", "/raw", "/version", "/publish")
        .hasRole(RoleEnum.USER.getName())
        .antMatchers(HttpMethod.PUT, "/rating", "/workspace/*", "/raw/*", "/version/*")
        .hasRole(RoleEnum.USER.getName())
        .antMatchers(HttpMethod.PATCH, "/raw/*")
        .hasRole(RoleEnum.USER.getName())
        .antMatchers(HttpMethod.DELETE, "/rating", "/workspace/*", "/raw/*",
            "/version/*")
        .hasRole(RoleEnum.USER.getName())
        // Administrator
        .antMatchers(HttpMethod.GET, "/account", "/editor/file")
        .hasRole(RoleEnum.ADMINISTRATOR.getName())
        .antMatchers(HttpMethod.POST, "/category", "/account", "/tutorial", "/template",
            "/editor/file")
        .hasRole(RoleEnum.ADMINISTRATOR.getName())
        .antMatchers(HttpMethod.PUT, "/category/*", "/template/*", "/account/*", "/tutorial/*",
            "/publish/approve/*", "/publish/deny/*")
        .hasRole(RoleEnum.ADMINISTRATOR.getName())
        .antMatchers(HttpMethod.PATCH, "/tutorial/*")
        .hasRole(RoleEnum.ADMINISTRATOR.getName())
        .antMatchers(HttpMethod.DELETE, "/template/*", "/editor/file")
        .hasRole(RoleEnum.ADMINISTRATOR.getName())
        // Logged
        .antMatchers(HttpMethod.GET, "/user", "/file", "/publish")
        .authenticated()
        .antMatchers(HttpMethod.POST, "/file")
        .authenticated()
        .antMatchers(HttpMethod.PUT, "/user", "/file/*")
        .authenticated()
//        .antMatchers(HttpMethod.DELETE, "")
//        .authenticated()
        .anyRequest()
        .denyAll();

    http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
  }
}
