package com.ops.hunting.user.controller;

import com.ops.hunting.common.dto.UserDto;
import com.ops.hunting.common.enums.AnalystTier;
import com.ops.hunting.common.enums.UserRole;
import com.ops.hunting.common.util.ResponseWrapper;
import com.ops.hunting.user.dto.UserRegistrationDto;
import com.ops.hunting.user.dto.UserLoginDto;
import com.ops.hunting.user.dto.LoginResponseDto;
import com.ops.hunting.user.dto.ChangePasswordDto;
import com.ops.hunting.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseWrapper<UserDto>> registerUser(@Valid @RequestBody UserRegistrationDto registrationDto) {
        try {
            UserDto user = userService.createUser(registrationDto);
            return ResponseEntity.ok(ResponseWrapper.success("User registered successfully", user));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ResponseWrapper.error("Registration failed: " + e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseWrapper<LoginResponseDto>> loginUser(@Valid @RequestBody UserLoginDto loginDto) {
        try {
            LoginResponseDto response = userService.authenticateUser(loginDto);
            return ResponseEntity.ok(ResponseWrapper.success("Login successful", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ResponseWrapper.error("Login failed: " + e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<ResponseWrapper<UserDto>> getCurrentUser(Principal principal) {
        try {
            UserDto user = userService.getUserByUsername(principal.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            return ResponseEntity.ok(ResponseWrapper.success(user));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ResponseWrapper.error("Failed to get user info: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<ResponseWrapper<UserDto>> getUserById(@PathVariable String id) {
        try {
            UserDto user = userService.getUserById(id)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            return ResponseEntity.ok(ResponseWrapper.success(user));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ResponseWrapper.error("Failed to get user: " + e.getMessage()));
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseWrapper<Page<UserDto>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "username") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection,
            @RequestParam(required = false) String search) {
        
        try {
            Sort sort = sortDirection.equalsIgnoreCase("desc") ? 
                    Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            
            Page<UserDto> users = search != null && !search.trim().isEmpty() ?
                    userService.searchUsers(search, pageable) :
                    userService.getAllUsers(pageable);
            
            return ResponseEntity.ok(ResponseWrapper.success(users));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ResponseWrapper.error("Failed to get users: " + e.getMessage()));
        }
    }

    @GetMapping("/by-role/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseWrapper<List<UserDto>>> getUsersByRole(@PathVariable UserRole role) {
        try {
            List<UserDto> users = userService.getUsersByRole(role);
            return ResponseEntity.ok(ResponseWrapper.success(users));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ResponseWrapper.error("Failed to get users by role: " + e.getMessage()));
        }
    }

    @GetMapping("/by-tier/{tier}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PRODUCTION_STAFF')")
    public ResponseEntity<ResponseWrapper<List<UserDto>>> getUsersByTier(@PathVariable AnalystTier tier) {
        try {
            List<UserDto> users = userService.getUsersByTier(tier);
            return ResponseEntity.ok(ResponseWrapper.success(users));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ResponseWrapper.error("Failed to get users by tier: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseWrapper<UserDto>> updateUser(@PathVariable String id, @Valid @RequestBody UserDto userDto) {
        try {
            UserDto user = userService.updateUser(id, userDto);
            return ResponseEntity.ok(ResponseWrapper.success("User updated successfully", user));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ResponseWrapper.error("Failed to update user: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseWrapper<Void>> deactivateUser(@PathVariable String id) {
        try {
            userService.deactivateUser(id);
            return ResponseEntity.ok(ResponseWrapper.success("User deactivated successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ResponseWrapper.error("Failed to deactivate user: " + e.getMessage()));
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<ResponseWrapper<Void>> changePassword(@Valid @RequestBody ChangePasswordDto changePasswordDto, Principal principal) {
        try {
            UserDto user = userService.getUserByUsername(principal.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            userService.changePassword(user.getId(), changePasswordDto.getCurrentPassword(), changePasswordDto.getNewPassword());
            return ResponseEntity.ok(ResponseWrapper.success("Password changed successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ResponseWrapper.error("Failed to change password: " + e.getMessage()));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<ResponseWrapper<String>> health() {
        return ResponseEntity.ok(ResponseWrapper.success("User Management Service is healthy"));
    }
}
