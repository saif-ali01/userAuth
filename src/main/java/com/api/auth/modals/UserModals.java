package com.api.auth.modals;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.mongodb.lang.NonNull;

@Document(collection = "user")  // MongoDB collection name for User
public class UserModals {

    @Id
    private String id;  // Unique ID for the user in the database
    
    @NonNull// Ensure name is not null
    private String name;  // User's full name
    
    @NonNull // Ensure email is not null
    private String email;  // User's email address
    
    @NonNull // Ensure password is not null
    private String password;  // User's password
    
    private boolean isVerified = false;  // Flag to indicate if the user is verified (default to false)

    // Getter for id
    public String getId() {
        return id;
    }

    // Setter for id
    public void setId(String id) {
        this.id = id;
    }

    // Getter for name
    public String getName() {
        return name;
    }

    // Setter for name
    public void setName(String name) {
        this.name = name;
    }

    // Getter for email
    public String getEmail() {
        return email;
    }

    // Setter for email
    public void setEmail(String email) {
        this.email = email;
    }

    // Getter for password
    public String getPassword() {
        return password;
    }

    // Setter for password
    public void setPassword(String password) {
        this.password = password;
    }

    // Getter for isVerified
    public boolean isVerified() {
        return isVerified;
    }

    // Setter for isVerified
    public void setVerified(boolean isVerified) {
        this.isVerified = isVerified;
    }
     // Setter for isVerified
     public boolean getVerified(){
        return isVerified;
     }
    
}
