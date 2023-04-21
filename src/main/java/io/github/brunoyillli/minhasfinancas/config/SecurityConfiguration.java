package io.github.brunoyillli.minhasfinancas.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import io.github.brunoyillli.minhasfinancas.service.JwtService;
import io.github.brunoyillli.minhasfinancas.service.impl.SecurityUserDetailService;

@SuppressWarnings("deprecation")
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Autowired
	private SecurityUserDetailService userDetailService;
	
	@Autowired
	private JwtService jwtService;
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailService)
		.passwordEncoder(passwordEncoder());
	}

	@Bean
	public JwtTokenFilter jwtTokenFilter() {
		return new JwtTokenFilter(jwtService, userDetailService);
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.csrf().disable()
			.authorizeRequests()
				.antMatchers(HttpMethod.POST, "/api/usuarios/autenticar").permitAll()
				.antMatchers(HttpMethod.POST, "/api/usuarios").permitAll()
				.anyRequest().authenticated()
		.and()
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
		.and()
			.addFilterBefore(jwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		PasswordEncoder encoder = new BCryptPasswordEncoder();
		return encoder;
	}
}
