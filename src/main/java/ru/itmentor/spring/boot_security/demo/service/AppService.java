package ru.itmentor.spring.boot_security.demo.service;

import ru.itmentor.spring.boot_security.demo.dto.UserDto;

import java.util.List;

public interface AppService {

    UserDto getCurrentUser();

    List<UserDto> getAllUsers();

    boolean updateUser(UserDto userDto);

    boolean addUser(UserDto user);


    boolean deleteUserByName(String name);
}
