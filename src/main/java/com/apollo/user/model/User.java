package com.apollo.user.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class User {

    @Getter
    private final String userId;
    @Getter
    private final Date issuedAt;
    @Getter
    private final Date expiresIn;
    @Getter
    private final Date authTime;
    @Getter
    @Setter
    private String givenName, familyName, username, email, imageUrl;
    @Getter
    @Setter
    private Gender gender;
    @Getter
    @Setter
    private UserType userType;
    @Getter
    @Setter
    private Date birthDate;
    @Getter
    @Setter
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
