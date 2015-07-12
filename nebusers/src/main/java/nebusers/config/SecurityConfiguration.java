package nebusers.config;

import javax.inject.Inject;

import nebusers.security.AjaxAuthenticationFailureHandler;
import nebusers.security.AjaxAuthenticationSuccessHandler;
import nebusers.security.AjaxLogoutSuccessHandler;
import nebusers.security.Http401UnauthorizedEntryPoint;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.data.repository.query.SecurityEvaluationContextExtension;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Inject
    private AjaxAuthenticationSuccessHandler ajaxAuthenticationSuccessHandler;

    @Inject
    private AjaxAuthenticationFailureHandler ajaxAuthenticationFailureHandler;

    @Inject
    private AjaxLogoutSuccessHandler ajaxLogoutSuccessHandler;

    @Inject
    private Http401UnauthorizedEntryPoint authenticationEntryPoint;	
	
    @Inject
    private UserDetailsService userDetailsService;	

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Inject
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
            .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
    }    
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
    	
    	
    	http.csrf().disable();    	
    	
        http
        .exceptionHandling()
        .authenticationEntryPoint(authenticationEntryPoint)
    .and()
        .formLogin()
        .loginProcessingUrl("/api/authentication")
        .successHandler(ajaxAuthenticationSuccessHandler)
        .failureHandler(ajaxAuthenticationFailureHandler)
        .usernameParameter("login")
        .passwordParameter("password")
        .permitAll()
    .and()
        .logout()
        .logoutUrl("/api/logout")
        .logoutSuccessHandler(ajaxLogoutSuccessHandler)
        .deleteCookies("JSESSIONID")
        .permitAll()
    .and()
        .headers()
        .frameOptions()
        .disable()
    .and()
        .authorizeRequests()
        .antMatchers("/api/**").permitAll()
        ;
    }
    
    @Bean
    public SecurityEvaluationContextExtension securityEvaluationContextExtension() {
        return new SecurityEvaluationContextExtension();
    }
    
}
