package com.apollo.user.model;

import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class User {

    private final String userId = UUID.randomUUID().toString();
    private Date birthDate;
    private Gender gender;
    private boolean isActive = true;
    private String givenName, familyName, username, email, imageUrl;
    private UserType userType = UserType.USER;

}
