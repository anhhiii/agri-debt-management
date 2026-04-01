package com.manage.debt_management.model;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "users")
public class UserAccount {

	@Id
    private String id;
    private String username;
    private String password;
    private String email;
    private String phone;
    
    @DBRef
    private Role role;
   
    private String status;
    private String createdAt;
    private String updatedAt;
}

