package org.hcmiu.submission_system.spring.config;


import javax.sql.DataSource;

import org.hcmiu.submission_system.spring.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private UserDetailsServiceImpl userDetailsService;

	@Autowired
	private DataSource dataSource;
	
	@Autowired 
	private AppConfig appConfig;
	

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {

		// set up service for looking user in database.
		// setup PasswordEncoder.
		appConfig =new AppConfig();
		auth.userDetailsService(userDetailsService).passwordEncoder(appConfig.passwordEncoder());

	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.csrf().disable();

		// pages with login require
		http.authorizeRequests().antMatchers("/", "/login", "/logout").permitAll();
		
		//
		http.authorizeRequests().antMatchers("/verifyLogin").access("hasAnyRole('ROLE_AUTHOR', 'ROLE_EDITOR', 'ROLE_REVIEWER')");
		
		//only user role author can access
		http.authorizeRequests().antMatchers("/userInfo").access("hasAnyRole('ROLE_AUTHOR')");

		// only editor can access this page
		http.authorizeRequests().antMatchers("/editor").access("hasRole('ROLE_EDITOR')");
		
		//only reviewer can access this page
		http.authorizeRequests().antMatchers("/reviewer").access("hasRole('ROLE_REVIEWER')");

		//throw exception if login with wrong role
		http.authorizeRequests().and().exceptionHandling().accessDeniedPage("/403");

		// set up for login form.
		http.authorizeRequests().and().formLogin()//
				// Submit URL for login page
				.loginProcessingUrl("/j_spring_security_check") // Submit URL
				.loginPage("/login")//
				.successForwardUrl("/verifyLogin")
				.defaultSuccessUrl("/verifyLogin",true)//
				.failureUrl("/login?error=true")//
				.usernameParameter("username")//
				.passwordParameter("password")
				// set up for logout page.
				.and().logout().logoutUrl("/logout").logoutSuccessUrl("/logoutSuccessful");

		// set up for remember me.
		http.authorizeRequests().and() //
				.rememberMe().tokenRepository(this.persistentTokenRepository()) //
				.tokenValiditySeconds(1 * 24 * 60 * 60); // 24h

	}
	
	@Bean
	public PersistentTokenRepository persistentTokenRepository() {
		JdbcTokenRepositoryImpl db = new JdbcTokenRepositoryImpl();
		db.setDataSource(dataSource);
		return db;
	}

}
