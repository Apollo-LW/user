package com.apollo.user.model;

import lombok.Data;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Data
public class User {

    private final String userId = UUID.randomUUID().toString();
    private final Date issuedAt = Calendar.getInstance().getTime(), expiresIn = Calendar.getInstance().getTime(), authTime = Calendar.getInstance().getTime();
    private Date birthDate;
    private String givenName, familyName, username, email, imageUrl;
    private Gender gender;
    private UserType userType = UserType.USER;
    private boolean isActive = true;

}
