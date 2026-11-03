package com.tripsync.user.entity;

import com.tripsync.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users",uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_users_email",columnNames = "email")})
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,length = 100)
    private String fullName;

    @Column(nullable = false,length = 150,unique = true)
    private String email;

    @Column(nullable = false,name="password_hash",length = 100)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false,length = 20)
    private Role role = Role.USER;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false,length =20)
    private AccountStatus status = AccountStatus.ACTIVE;


}
