package com.hpcl.inout.service;

import com.hpcl.inout.dto.LoginDto;
import com.hpcl.inout.entity.User;
import com.hpcl.inout.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

	@Autowired private UserRepository userRepository;
	@Autowired private PasswordEncoder passwordEncoder;
	@Autowired private AuthenticationManager authenticationManager;

	// Register user
	public User signup(User user) {
		if (userRepository.findByUserName(user.getUsername()) != null) {
			throw new RuntimeException("Username already exists!");
		}
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		return userRepository.save(user);
	}

	// Login user
	public User loginUser(LoginDto loginDto) {
		try {
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginDto.getUserName(), loginDto.getPassword())
					);
		} catch (BadCredentialsException e) {
			throw new RuntimeException("Invalid credentials!");
		}
		return userRepository.findByUserName(loginDto.getUserName());
	}

	// Fetch all users
	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	// Update user information
	public ResponseEntity<?> updateUser(String currentUsername, int userId, String username, 
			String password, String role) {
		User currentUser = userRepository.findByUserName(currentUsername);

		// Check if current user has permission (ADMIN or OFFICER)
		if (currentUser == null || 
				(!"ADMIN".equalsIgnoreCase(currentUser.getRole()) && 
						!"OFFICER".equalsIgnoreCase(currentUser.getRole()))) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body("Access denied. Only ADMIN or OFFICER can update users.");
		}

		User userToUpdate = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

		// Check if username is being changed to one that already exists
		if (!userToUpdate.getUsername().equals(username) && 
				userRepository.findByUserName(username) != null) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body("Username already exists.");
		}

		// Update user details
		userToUpdate.setUserName(username);
		if (password != null && !password.isEmpty()) {
			userToUpdate.setPassword(passwordEncoder.encode(password));
		}
		userToUpdate.setRole(role);

		userRepository.save(userToUpdate);
		return ResponseEntity.ok("User updated successfully.");
	}

	// Update password
	public String updatePassword(String userName, String oldPassword, String newPassword) {
		User user = userRepository.findByUserName(userName);

		if (user == null) {
			throw new RuntimeException("User not found");
		}

		if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
			throw new RuntimeException("Old password is incorrect");
		}

		user.setPassword(passwordEncoder.encode(newPassword));
		userRepository.save(user);

		return "Password updated successfully.";
	}

	// Add user (ADMIN or OFFICER only)
	public ResponseEntity<?> addUser(String currentUsername, String accessName, String accessPass, String accessRole) {
		User currentUser = userRepository.findByUserName(currentUsername);

		if (currentUser == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Current user not found.");
		}

		String currentUserRole = currentUser.getRole();
		if (!"ADMIN".equalsIgnoreCase(currentUserRole) && !"OFFICER".equalsIgnoreCase(currentUserRole)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied. Only ADMIN or OFFICER can add users.");
		}

		if (userRepository.findByUserName(accessName) != null) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists.");
		}

		User newUser = new User();
		newUser.setUserName(accessName);
		newUser.setPassword(passwordEncoder.encode(accessPass));
		newUser.setRole(accessRole);

		userRepository.save(newUser);
		return ResponseEntity.ok("User added successfully.");
	}

	// Delete user (ADMIN only)
	public ResponseEntity<?> deleteUser(String currentUsername, int userId) {
		User currentUser = userRepository.findByUserName(currentUsername);

		// Only ADMIN can delete users
		if (currentUser == null || !"ADMIN".equalsIgnoreCase(currentUser.getRole())) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body("Access denied. Only ADMIN can delete users.");
		}

		// Prevent self-deletion
		if (currentUser.getUserId() == userId) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("Cannot delete your own account.");
		}

		User userToDelete = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

		userRepository.delete(userToDelete);
		return ResponseEntity.ok("User deleted successfully.");
	}

	// Get user by username
	public User findByUsername(String username) {
		return userRepository.findByUserName(username);
	}
}