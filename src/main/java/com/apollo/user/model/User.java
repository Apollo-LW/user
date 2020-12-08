package com.apollo.user.model;

import lombok.Data;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Data
public class User {

    private final String userId;
    private final Date issuedAt, expiresIn, authTime;
    private Date birthDate;
    private String givenName, familyName, username, email, imageUrl;
    private Gender gender;
    private UserType userType;
    private boolean isActive;

    public User() {
        this.userId = UUID.randomUUID().toString();
        this.userType = UserType.USER;
        this.isActive = true;
        this.authTime = Calendar.getInstance().getTime(); //This is not final yet, meaning that it will change based on the gateway
        this.issuedAt = Calendar.getInstance().getTime(); //This is not final yet, meaning that it will change based on the gateway
        this.expiresIn = Calendar.getInstance().getTime(); //This is not final yet, meaning that it will change based on the gateway
    }
}
