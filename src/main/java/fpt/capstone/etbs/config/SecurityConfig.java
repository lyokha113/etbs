package fpt.capstone.etbs.config;

import fpt.capstone.etbs.component.HttpCookieOAuth2AuthorizationRequest;
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
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired private CustomUserDetailsServiceImpl customUserDetailsService;

  @Autowired private OAuth2UserServiceImpl customOAuth2UserService;

  @Autowired private OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

  @Autowired private OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

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
        .authenticationEntryPoint(new Http403ForbiddenEntryPoint())
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
        .antMatchers("/v2/api-docs", "/configuration/**", "/swagger*/**", "/webjars/**")
        .permitAll()
        // All
        .antMatchers(HttpMethod.POST, "/login", "/register")
        .anonymous()
        .antMatchers(HttpMethod.GET, "/category", "/template", "/template/*")
        .permitAll()
        // Logged
        .antMatchers("/user")
        .hasAnyRole(RoleEnum.ADMINISTRATOR.getName(), RoleEnum.USER.getName())
        // User
        .antMatchers(HttpMethod.GET, "/file", "file/*", "/workspace", "/workspace/*", "/raw/**")
        .permitAll()
        .antMatchers(HttpMethod.POST, "/file", "/workspace", "/email/*", "/rate", "/raw")
        .permitAll()
        .antMatchers(HttpMethod.PUT, "/file/*", "/workspace/*", "/raw/*")
        .permitAll()
        // Administrator
        .antMatchers(HttpMethod.POST, "/category", "/template")
        .permitAll()
        .antMatchers(HttpMethod.PUT, "/category/*", "/template/*")
        .permitAll()
        .anyRequest()
        .authenticated();

    http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
  }
}
