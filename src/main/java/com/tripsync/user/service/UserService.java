package com.tripsync.user.service;

import com.tripsync.user.dto.ChangePasswordRequest;
import com.tripsync.user.dto.RegisterRequest;
import com.tripsync.user.dto.UpdateUserRequest;
import com.tripsync.user.dto.UserResponse;
import com.tripsync.user.entity.AccountStatus;
import com.tripsync.user.entity.Role;
import com.tripsync.user.entity.User;
import com.tripsync.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponse register(RegisterRequest request){
        String email = request.email().trim();
        if (userRepository.findByEmail(email).isPresent()){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Email is already registered");
        }
        User user = new User();
      user.setFullName(request.fullName().trim());
      user.setEmail(email);
      user.setPasswordHash(passwordEncoder.encode(request.password()));
      user.setRole(Role.USER);
      user.setStatus(AccountStatus.ACTIVE);
      User savedUser = userRepository.save(user);
        return new UserResponse(
                savedUser.getId(),
                savedUser.getFullName(),
                savedUser.getEmail(),
                savedUser.getRole(),
                savedUser.getStatus()
        );
    }
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers(){
        return userRepository.findAll()
                .stream()
                .map(user -> new UserResponse
                        (
                        user.getId(),
                        user.getFullName(),
                        user.getEmail(),
                        user.getRole(),
                        user.getStatus()
                        )
                ).toList();
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id){
        User user = userRepository.findById(id)
                .orElseThrow(()->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,"User not found"
                        ));
        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                user.getStatus()
        );
    }

    @Transactional
    public void changePassword(String email, ChangePasswordRequest request){
        User user = userRepository.findByEmail(email)
                .orElseThrow(()->
                        new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found"));
        if (user.getStatus()!=AccountStatus.ACTIVE){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Account is not active");
        }
        if (!passwordEncoder.matches(request.currentPassword(),user.getPasswordHash())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Current password is incorrect");
        }
        if (passwordEncoder.matches(request.newPassword(), user.getPasswordHash())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"New password must be different from current password");

        }
        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    @Transactional
    public void resetPassword(Long id,String newPassword){
        User user = userRepository.findById(id)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found"));
        if (user.getRole()==Role.ADMIN){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Admin password cannot be reset through this endpoint");
        }
        if (passwordEncoder.matches(newPassword,user.getPasswordHash())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"New password must be different from current password");
        }
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest request){
        User user = userRepository.findById(id)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found"));
        String email = request.email().trim();
        userRepository.findByEmail(email)
                .filter(existingUser->!existingUser.getId().equals(user.getId()))
                .ifPresent(existingUser->{
                    throw new ResponseStatusException(HttpStatus.CONFLICT,"Email is already registered");
                });
        user.setFullName(request.fullName().trim());
        user.setEmail(email);
        User savedUser = userRepository.save(user);
        return toUserResponse(savedUser);
    }

    @Transactional
    public UserResponse updateUserStatus(Long id, AccountStatus status){
        User user = userRepository.findById(id)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found"));
        if (user.getRole()==Role.ADMIN){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Admin account status cannot be changed here");
        }
        user.setStatus(status);
        User savedUser = userRepository.save(user);
        return toUserResponse(savedUser);
    }

    @Transactional
    public void deleteUser(Long id){
        User user = userRepository.findById(id)
                .orElseThrow(()->
                        new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found"));
        if (user.getRole()==Role.ADMIN){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Admin accounts cannot be deleted through this endpoint");

        }
        userRepository.delete(user);
    }

    private UserResponse toUserResponse(User user){
        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                user.getStatus()
        );
    }
}
