package com.tripsync.user.service;

import com.tripsync.user.entity.AccountStatus;
import com.tripsync.user.entity.Role;
import com.tripsync.user.entity.User;
import com.tripsync.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminBootstrapService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void createInitialAdmin(String email, String  password, String fullName){
        if (email==null||email.isBlank()){
            throw new IllegalStateException("Admin email is required.");
        }
        if (password==null||password.isBlank()){
            throw new IllegalStateException("Admin password is required.");
        }
        if (fullName==null||fullName.isBlank()){
            throw new IllegalStateException("Admin fullName is required.");
        }

        String normalizedEmail = email.trim().toLowerCase();
        var existingUser = userRepository.findByEmail(normalizedEmail);
        if (existingUser.isPresent()){
            if (existingUser.get().getRole()!= Role.ADMIN){
                throw new IllegalStateException("Email already belongs to a non-admin account."+
                        "No role changes were made.");
            }
            return;
        }
        User admin = new User();
        admin.setFullName(fullName);
        admin.setEmail(normalizedEmail);
        admin.setPasswordHash(passwordEncoder.encode(password));
        admin.setRole(Role.ADMIN);
        admin.setStatus(AccountStatus.ACTIVE);
        userRepository.save(admin);
    }
}
