package com.manage.debt_management.shared;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.manage.debt_management.repository.UserAccountRepository;

@Service
public class UserServiceShared {
    @Autowired
    private UserAccountRepository userAccountRepository;

    public boolean isEmailExists(String email) {
        return userAccountRepository.findByEmail(email).isPresent();
    }

    public boolean isUsernameExists(String username) {
        return userAccountRepository.findByUsername(username).isPresent();
    }

    public boolean isPhoneExists(String phone) {
        return userAccountRepository.findByPhone(phone).isPresent();
    }
}