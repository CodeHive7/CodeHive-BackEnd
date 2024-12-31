package com.codehive.mapper;

import com.codehive.dto.UserDto;
import com.codehive.entity.User;
import com.codehive.vm.UserVM;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDto toDto(User user) {
        if(user == null) {
            return null;
        }
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setLocation(user.getLocation());
        dto.setPhoneNumber(user.getPhoneNumber());
        return dto;
    }

    public void updateUserFromVM(UserVM userVM, User user) {
        if(userVM.getFullName() != null) {
            user.setFullName(userVM.getFullName());
        }
        if(userVM.getUsername() != null) {
            user.setUsername(userVM.getUsername());
        }
        if(userVM.getEmail() != null) {
            user.setEmail(userVM.getEmail());
        }
        if(userVM.getLocation() != null) {
            user.setLocation(userVM.getLocation());
        }
        if(userVM.getPhoneNumber() != null) {
            user.setPhoneNumber(userVM.getPhoneNumber());
        }
    }
}
