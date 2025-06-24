package com.hpcl.inout.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.hpcl.inout.logout.BlackList;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JWTService {
	
	@Autowired
	BlackList blackList;
	
	private static String secreatekey= "YUhSMGNITTZMeTkzWldKaGRHVXRhRzkzZEM1amIyMGlhWE1pTkdjdE9TNWpiMjA9";
	private static long jwtExpiration=86400000;
	
	
	public static <T> T extractClaims(String token,Function<Claims,T> claimsResolver) {
		final Claims claims=extractAllClaims(token);
		return claimsResolver.apply(claims);
	}
	
	private static Claims extractAllClaims(String token) {
		
		return Jwts.parserBuilder()
				.setSigningKey(getSignKey())
				.build()
				.parseClaimsJws(token)
				.getBody();
	}

	public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
		return buildToken(extraClaims,userDetails,jwtExpiration);
	}
	
	public String buildToken(Map<String, Object> extraClaims,UserDetails userDetails, long expiration) {
		
		return Jwts.builder()
				.setClaims(extraClaims)
				.setSubject(userDetails.getUsername())
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis()+expiration))
				.signWith(getSignKey(),SignatureAlgorithm.HS256)
				.compact();
	}

	public static Key getSignKey() {
		byte [] keyBytes = Decoders.BASE64.decode(secreatekey); 
		return Keys.hmacShaKeyFor(keyBytes);
	}
	
	public Date extractExpiration(String token) {
		return extractClaims(token, Claims::getExpiration);
	}
	
	public boolean isTokenValid(String token, UserDetails userDetails) {
        if (blackList.isBlacklisted(token)) {
            throw new IllegalStateException("Token is blacklisted. Please login again.");
        }

        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }


    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
	
	public long getExpirationTime() {
		return jwtExpiration;
	}
	
	public static String extractUsername(String token) {
		 return extractClaims(token, Claims::getSubject);
	 }
}
