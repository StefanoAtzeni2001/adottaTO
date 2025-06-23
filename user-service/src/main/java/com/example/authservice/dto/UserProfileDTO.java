package com.example.authservice.dto;

public class UserProfileDTO {
    private Long id;
    private String name;
    private String surname;
    private String email;
    private String profilePicture;

    public UserProfileDTO() {
    }

    public UserProfileDTO(Long id, String name, String surname, String email, String profilePicture) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.profilePicture = profilePicture;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getEmail() {
        return email;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
}
