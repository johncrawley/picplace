package com.jac.picplace.config;



import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;


@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
//public class WebSecurityConfig extends WebMvcAutoConfiguration{

	private static final String USER= "USER";
	private static final String ADMIN = "ADMIN";
	
	@Autowired
	DataSource dataSource;
	
	//Enable jdbc authentication
    @Autowired
    public void configAuthentication(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication().dataSource(dataSource).passwordEncoder(passwordEncoder());
    }

    
    @Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/resources/**").antMatchers("/static/**").antMatchers("/images/**").antMatchers("/css/**");
		
	}   

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		//http.httpBasic().disable();
		//http.addFilterBefore(new AccessDeniedFilter(),FilterSecurityInterceptor.class);
		http.authorizeRequests()
		.antMatchers("/svc/v1/public/temp").hasAuthority(USER).and().httpBasic();
		
		
		http.authorizeRequests()
		.antMatchers("/").permitAll()
		.antMatchers("/register").permitAll()
		.antMatchers("/notes").permitAll()
		.antMatchers("/welcome").hasAnyRole(USER, ADMIN)
		.antMatchers("/getEmployees").hasAnyRole(USER, ADMIN)
		.antMatchers("/photo").hasAnyAuthority(USER, ADMIN)
		.antMatchers("/secure/members").hasAnyRole(ADMIN).and().formLogin();
		//.anyRequest().authenticated().and().formLogin();//.and().httpBasic();
		//.loginPage("/login").permitAll()
		//		.and().logout().permitAll();
		//.anyRequest().authenticated().and().formLogin();

		http.csrf().disable();
		http.formLogin().successHandler(new AuthenticationSuccessHandler() {
		    //@Override
		    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
		       //do nothing
		    }
		});
	}
/*
	@Bean
	public PasswordEncoder passwordEncoder() {
		PasswordEncoder passwordEncoder = NoOpPasswordEncoder.getInstance();
		return passwordEncoder;
	}
	*/
	@Bean
	public PasswordEncoder passwordEncoder(){
		PasswordEncoder encoder = new BCryptPasswordEncoder();
		return encoder;
	}
	
}