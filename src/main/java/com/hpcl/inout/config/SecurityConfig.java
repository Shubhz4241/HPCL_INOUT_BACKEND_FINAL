package com.hpcl.inout.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.hpcl.inout.service.JWTAuthenticationEntryPoint;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Autowired
	private AuthenticationProvider authenticationProvider;

	@Autowired
	private JWTAuthenticationEntryPoint jwtAuthenticationEntryPoint;

	@Bean
	public SecurityFilterChain setFilterChain(HttpSecurity httpSecurity) throws Exception {
		return httpSecurity
				.cors(cors -> {})
				.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/auth/**").permitAll()
						.requestMatchers("/mainGate/**").permitAll()
						.requestMatchers("/img/**").permitAll()
						.requestMatchers("/visitors/**").permitAll()
						.requestMatchers("/drivers/**").permitAll()
						.requestMatchers("/dashboard/**").permitAll()
						.requestMatchers("/setting/**").permitAll()
						.requestMatchers("/projects/**").permitAll()
						.requestMatchers("/dashboard/**").permitAll()
						.anyRequest().authenticated()  
						)
				.sessionManagement(session -> session
						.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
						)
				.authenticationProvider(authenticationProvider)
				.addFilterBefore(jwtAuthenticationEntryPoint, UsernamePasswordAuthenticationFilter.class)  
				.build();
	}


}
