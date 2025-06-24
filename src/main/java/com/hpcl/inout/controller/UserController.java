package com.hpcl.inout.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hpcl.inout.dto.LoginDto;
import com.hpcl.inout.entity.Inscan;
import com.hpcl.inout.entity.License;
import com.hpcl.inout.entity.Officer;
import com.hpcl.inout.entity.User;
import com.hpcl.inout.logout.BlackList;
import com.hpcl.inout.response.LoginResponse;
import com.hpcl.inout.service.UserService;
import com.hpcl.inout.service.JWTService;
import com.hpcl.inout.service.LicenseService;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

	@Autowired private UserService userService;
	@Autowired private JWTService jwtService;
	@Autowired private BlackList blackList;
	@Autowired private LicenseService licenseService;

	@PostMapping("/signup")
	public ResponseEntity<User> registerUser(@RequestBody User user) {
		System.out.println(user.getUsername());
		return ResponseEntity.ok(userService.signup(user));
	}

	@PostMapping("/login")
	public ResponseEntity<Map<String, Object>> loginUser(@RequestBody LoginDto loginDto) {
	    User user = userService.loginUser(loginDto);  // Authenticate user
	    String token = jwtService.generateToken(new HashMap<>(), user);  // Generate JWT token

	    Map<String, Object> response = new HashMap<>();
	    response.put("token", token);
	    response.put("tokenExpireTime", jwtService.getExpirationTime());
	    response.put("role", user.getRole());
	    response.put("username", user.getUsername());

	    return ResponseEntity.ok(response);
	}

	@GetMapping("/getUsers")
	public Map<String, Object> fetchAllUsers() {
		Map<String, Object> response = new HashMap<>();

        List<User> userDetails = userService.getAllUsers();
        
        response.put("user details", userDetails);

        return response;
	}
	
	
	@PutMapping("/updateUser")
    public ResponseEntity<?> updateUser(@RequestBody Map<String, Object> body, Authentication authentication) {
        String currentUsername = authentication.getName();
        Integer userId = (int)(body.get("id"));
        String accessName = (String) body.get("userName");
        String accessPass = (String) body.get("password");
        String accessRole = (String) body.get("role");

        return userService.updateUser(currentUsername, userId, accessName, accessPass, accessRole);
    }

    @DeleteMapping("/deleteUser/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable int userId, Authentication authentication) {
        String currentUsername = authentication.getName();
        return userService.deleteUser(currentUsername, userId);
    }

	@PostMapping("/logout")
	public ResponseEntity<?> logout(HttpServletRequest request) {
		String authHeader = request.getHeader("Authorization");
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			String token = authHeader.substring(7);
			blackList.blacklistToken(token);
			return ResponseEntity.ok("Logout successful.");
		}
		return ResponseEntity.badRequest().body("Authorization header missing or invalid.");
	}

	@PostMapping("/updatePassword")
	public ResponseEntity<String> updatePassword(@RequestBody HashMap<String, String> body, Authentication authentication) {
		String userName = body.get("userName");
		String oldPassword = body.get("oldPassword");
		String newPassword = body.get("newPassword");

		String message = userService.updatePassword(userName, oldPassword, newPassword);
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();

		HashMap<String, Object> claims = new HashMap<>();
		claims.put("username", userDetails.getUsername());

		String newToken = jwtService.generateToken(claims, userDetails);
		return ResponseEntity.ok("Password changed successfully. New Token: " + newToken);
	}

	@PostMapping("/addUser")
	public ResponseEntity<?> addUser(@RequestBody Map<String, String> body, Authentication authentication) {
		String currentUsername = authentication.getName(); 
		String accessName = body.get("accessName");
		String accessPass = body.get("accessPass");
		String accessRole = body.get("accessType");

		return userService.addUser(currentUsername, accessName, accessPass, accessRole);
	}

	@PostMapping("/LicenseValidate")
	public ResponseEntity<Map<String, String>> validateLicense(@RequestBody License license) {
		String licensestatus = licenseService.validateLicense(license.getLicensekey());
		Map<String, String> response = new HashMap<>();

		if (licensestatus == null) {
			response.put("status", "InvalidKey");
			response.put("message", "Invalid Key ! Please Enter a Valid Key.");
			return ResponseEntity.badRequest().body(response);
		}

		if (licensestatus.equals("Expired")) {
			response.put("status", "ExpiredKey");
			response.put("message", "Expired Key");
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
		}

		response.put("status", "Valid");
		response.put("redirect", "/login");
		return ResponseEntity.ok(response);
	}

	@GetMapping("/license/remaining-days")
	public ResponseEntity<Map<String, Object>> getLicenseRemainingDays() {
		Map<String, Object> response = new HashMap<>();

		try {
			User admin = userService.findByUsername("admin");
			License license = admin.getLicense();

			if (license == null) {
				response.put("status", "error");
				response.put("message", "No license found");
				return ResponseEntity.badRequest().body(response);
			}

			long remainingDays = java.time.temporal.ChronoUnit.DAYS.between(
					java.time.LocalDate.now(), license.getExpirydate()
			);

			if (remainingDays <= 0) {
				response.put("status", "expired");
				response.put("remainingDays", 0);
			} else {
				response.put("status", "valid");
				response.put("remainingDays", remainingDays);
			}

			return ResponseEntity.ok(response);
		} catch (Exception e) {
			response.put("status", "error");
			response.put("message", "Something went wrong");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}
}
