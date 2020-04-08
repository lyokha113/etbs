package fpt.capstone.etbs.config;

import static fpt.capstone.etbs.constant.RoleEnum.ADMINISTRATOR;
import static fpt.capstone.etbs.constant.RoleEnum.USER;

import fpt.capstone.etbs.component.JwtAuthenticationEntryPoint;
import fpt.capstone.etbs.filter.JwtAuthenticationFilter;
import fpt.capstone.etbs.service.impl.CustomUserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
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
@Order(1)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  private CustomUserDetailsServiceImpl customUserDetailsService;

  @Autowired
  private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

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
    http.cors().and().csrf()
        .disable().formLogin()
        .disable().httpBasic()
        .disable().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .exceptionHandling()
        .authenticationEntryPoint(jwtAuthenticationEntryPoint);

    // All
    http.authorizeRequests()
        .antMatchers(HttpMethod.GET, "/", "/category", "/template", "/template/*",
            "/template/author/*",
            "/tutorial", "/tutorial/*", "/confirm/*", "/ws/*", "/ws/**")
        .permitAll()
        .antMatchers(HttpMethod.POST, "/login", "/google/login", "/register", "/email/confirm")
        .permitAll()
        .antMatchers("/email/draft/gmail") // Redirect URI from Google
        .permitAll();

    // User
    http.authorizeRequests()
        .antMatchers(HttpMethod.GET, "/rating", "/workspace", "/raw/*", "/user", "/useremail",
            "/userblock", "/userblock/*", "/session/raw/*", "/session/user", "/session/user/*",
            "/google/authorize")
        .hasRole(USER.getName())
        .antMatchers(HttpMethod.POST, "/rating", "/template", "/workspace", "/rate", "/raw",
            "/email/send", "/email/confirm/*", "/email/draft/*", "/publish", "/useremail",
            "/userblock", "/userblock/sync", "/session/raw")
        .hasRole(USER.getName())
        .antMatchers(HttpMethod.PUT, "/rating", "/workspace/*", "/raw/*", "/user", "/user/invite",
            "/userblock/*",
            "/session/raw/**", "/session/user/**")
        .hasRole(USER.getName())
        .antMatchers(HttpMethod.PATCH, "/raw/**", "/userblock/*", "/file/*")
        .hasRole(USER.getName())
        .antMatchers(HttpMethod.DELETE, "/workspace/*", "/raw/*", "/useremail/*",
            "/userblock/*")
        .hasRole(USER.getName());

    // Administrator
    http.authorizeRequests().antMatchers(HttpMethod.GET, "/account", "/editor/file")
        .hasRole(ADMINISTRATOR.getName())
        .antMatchers(HttpMethod.POST, "/category", "/account", "/tutorial", "/template",
            "/editor/file")
        .hasRole(ADMINISTRATOR.getName())
        .antMatchers(HttpMethod.PUT, "/category/*", "/template/*", "/account/*", "/tutorial/*",
            "/publish/approve/*", "/publish/deny/*")
        .hasRole(ADMINISTRATOR.getName())
        .antMatchers(HttpMethod.PATCH, "/account/*", "/tutorial/*")
        .hasRole(ADMINISTRATOR.getName())
        .antMatchers(HttpMethod.DELETE, "/template/*", "/editor/file")
        .hasRole(ADMINISTRATOR.getName());

    // Logged
    http.authorizeRequests()
        .antMatchers(HttpMethod.GET, "/user", "/file", "/publish", "/notification",
            "/notification/all")
        .authenticated()
        .antMatchers(HttpMethod.POST, "/file", "/notification/*")
        .authenticated()
        .antMatchers(HttpMethod.PUT, "/user", "/file/*")
        .authenticated();

    // Swagger
    http.authorizeRequests()
        .antMatchers("/v2/api-docs", "/configuration/**", "/swagger*/**", "/webjars/**")
        .permitAll();

    http.authorizeRequests().anyRequest().denyAll();
    http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
  }
}
