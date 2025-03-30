package com.codehive.service.impl;

import com.codehive.dto.UserDto;
import com.codehive.entity.User;
import com.codehive.mapper.UserMapper;
import com.codehive.repository.UserRepository;
import com.codehive.service.UserService;
import com.codehive.vm.UserVM;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto updateUserProfile(String username, UserVM vm) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userMapper.updateUserFromVM(vm, user);
        userRepository.save(user);
        return userMapper.toDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return userMapper.toDto(user);
    }

}
