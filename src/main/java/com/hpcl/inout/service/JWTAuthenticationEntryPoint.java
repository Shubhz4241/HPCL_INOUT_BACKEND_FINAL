package com.hpcl.inout.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.hpcl.inout.logout.BlackList;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JWTAuthenticationEntryPoint extends OncePerRequestFilter{

	@Autowired
	JWTService jwtService;
	
	@Autowired
	UserDetailsService userDetailsService;
	
	@Autowired
	private HandlerExceptionResolver handlerExceptionResolver;
	
	@Autowired
	BlackList blackList;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
	        throws ServletException, IOException {
	    
	    final String authHeader = request.getHeader("Authorization");
	    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
	        filterChain.doFilter(request, response);
	        return;
	    }

	    try {
	        final String jwtToken = authHeader.substring(7);
	        
	        if (blackList.isBlacklisted(jwtToken)) {
	            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	            response.getWriter().write("Token is blacklisted. Please login again.");
	            return;
	        }

	        final String adminEmail = JWTService.extractUsername(jwtToken);
	        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

	        if (adminEmail != null && authentication == null) {
	            UserDetails userDetails = userDetailsService.loadUserByUsername(adminEmail);

	            if (jwtService.isTokenValid(jwtToken, userDetails)) {
	                UsernamePasswordAuthenticationToken authenticationToken =
	                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
	                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
	                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
	            }
	        }

	        filterChain.doFilter(request, response);
	    } catch (Exception e) {
	        handlerExceptionResolver.resolveException(request, response, null, e);
	    }
	}


}
