package com.zoom.service;

import com.zoom.entity.User;
import com.zoom.repository.MeetingRepository;
import com.zoom.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private MeetingRepository meetingRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        User currentLoggedInUser = userRepository.findByEmail(userEmail);
        return currentLoggedInUser;
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void updateUser(User user) {
        userRepository.save(user);
    }
}

