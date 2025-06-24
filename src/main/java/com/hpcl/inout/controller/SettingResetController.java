package com.hpcl.inout.controller;

import com.hpcl.inout.entity.PasswordReset;
import com.hpcl.inout.entity.User;
import com.hpcl.inout.repository.UserRepository;
import com.hpcl.inout.service.InscanService;
import com.hpcl.inout.service.LicenseGateService;
import com.hpcl.inout.service.PasswordResetService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/setting")
@CrossOrigin(origins = "http://localhost:5173")
public class SettingResetController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private PasswordResetService passwordResetService;

	@Autowired
	private InscanService inscanService;

	@Autowired
	private LicenseGateService licenseGateService;

	// ✅ Get current user profile
	@GetMapping("/profile")
	public ResponseEntity<?> getUserProfile(Principal principal) {
		String username = principal.getName();
		User user = userRepository.findByUserName(username);
		if (user != null) {
			return ResponseEntity.ok(user);
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
	}

//	// ✅ Update password
//	@PostMapping("/update-password")
//	public ResponseEntity<?> updatePassword(@RequestBody Map<String, String> body, Principal principal) {
//		String oldPassword = body.get("oldpass");
//		String newPassword = body.get("newpass");
//		String username = principal.getName();
//
//		User currentUser = userRepository.findByUserName(username);
//		if (currentUser == null || !passwordEncoder.matches(oldPassword, currentUser.getPassword())) {
//			return ResponseEntity.badRequest().body("Invalid old password.");
//		}
//
//		currentUser.setPassword(passwordEncoder.encode(newPassword));
//		userRepository.save(currentUser);
//		return ResponseEntity.ok("Password changed successfully.");
//	}

	// ✅ Add new user (Access Management)
//	@PostMapping("/add-user")
//	public ResponseEntity<?> addUser(@RequestBody Map<String, String> body) {
//		String name = body.get("accessName");
//		String password = body.get("accessPass");
//		String role = body.get("accessType");
//
//		if (userRepository.findByUserName(name) != null) {
//			return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists.");
//		}
//
//		User newUser = new User();
//		newUser.setUserName(name);
//		newUser.setPassword(passwordEncoder.encode(password));
//		newUser.setRole(role);
//
//		userRepository.save(newUser);
//		return ResponseEntity.ok("User added successfully.");
//	}

	// ✅ Get all users (role-based access)
	@GetMapping("/users")
	public ResponseEntity<?> getAllUsers(Principal principal) {
		User currentUser = userRepository.findByUserName(principal.getName());
		if (currentUser == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
		}

		if (currentUser.getRole().contains("security")) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You don't have permission to view this.");
		}

		List<User> users;
		if (currentUser.getRole().contains("officer")) {
			users = userRepository.findByRole("officer");
		} else {
			users = userRepository.findAll();
		}

		return ResponseEntity.ok(users);
	}

	// ✅ Get reset password entity (if needed for frontend context)
	@GetMapping("/reset-info")
	public ResponseEntity<?> getPasswordResetEntity() {
		PasswordReset resetEntity = passwordResetService.getPasswordResetEntity();
		return ResponseEntity.ok(resetEntity);
	}

	// ✅ Perform password reset
	@PostMapping("/reset-password")
	public ResponseEntity<?> resetPassword(@RequestBody PasswordReset resetEntity) {
		passwordResetService.resetPassword(resetEntity, resetEntity.getResetPassword());
		return ResponseEntity.ok("Password reset successfully.");
	}

}
