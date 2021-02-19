package com.apollo.user.model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.util.Date;
import java.util.UUID;

/**
 * Main User Data Model
 */
@Data
public class User {

    private final String userId = UUID.randomUUID().toString();
    private String imageUrl = "";
    private boolean isActive = true;
    private @NotNull Date birthDate;
    private @NotNull Gender gender;
    private @NotNull @NotEmpty String givenName;
    private @NotNull @NotEmpty String familyName;
    private @NotNull String username;
    private @NotNull @NotEmpty @Email String email;
    private UserType userType = UserType.USER;

}
