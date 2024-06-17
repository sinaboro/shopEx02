package com.shop.config;

import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@Log4j2
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("--------------------securityFilterChain----------------------------");

        http.formLogin()
                        .loginPage("/members/login")
                        .defaultSuccessUrl("/")
                        .usernameParameter("email")  //로그인시 username으로 로그인 id일 때는 생략가능
                        .failureUrl("/members/login/error")
                        .and()
                        .logout()
                        .logoutRequestMatcher(new AntPathRequestMatcher("/members/logout"))
                        .logoutSuccessUrl("/");

        http.authorizeRequests()
                        .mvcMatchers("/css/**", "/js/**", "/img/**").permitAll()
                        .mvcMatchers("/", "/members/**", "/item/**", "/images/**").permitAll()
                        .mvcMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated();

        http.exceptionHandling()
                        .authenticationEntryPoint(new CustomAuthticationEntryPoint());

        http.csrf().disable();

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
