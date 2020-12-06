package com.apollo.user.model;

import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class User {
    
    private final String userId = UUID.randomUUID().toString();
    private String givenName , familyName , username , email, imageUrl;
    private Gender gender;
    private UserType userType = UserType.USER;
    private Date birthDate , issuedAt , expiresIn , authTime;
    private boolean isActive = true;

}
