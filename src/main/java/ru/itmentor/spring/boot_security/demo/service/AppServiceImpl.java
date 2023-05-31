package ru.itmentor.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ru.itmentor.spring.boot_security.demo.dto.RoleDto;
import ru.itmentor.spring.boot_security.demo.dto.UserDto;
import ru.itmentor.spring.boot_security.demo.model.Role;
import ru.itmentor.spring.boot_security.demo.model.User;
import ru.itmentor.spring.boot_security.demo.repositories.UserRepository;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
public class AppServiceImpl implements AppService {

    private UserRepository userRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return new UserDto(userRepository.findByName(authentication.getName()));
    }


    @Override
    public List<UserDto> getAllUsers() {
        List<UserDto> userList = new LinkedList<>();
        userRepository.findAll().forEach(user -> userList.add(new UserDto(user)));
        return userList;
    }

    @Override
    public boolean updateUser(UserDto user) {
        // Call the UserService implementation to update the user
        Optional<User> userOptional = userRepository.findById(user.getId());
        if (!userOptional.isEmpty()) {
            User existingUser = userOptional.get();
            if (!user.getPassword().isBlank()) {
                existingUser.setPassword(user.getPassword());
            }
            if (user.getRoles() != null && !user.getRoles().isEmpty()) {
                existingUser.setRoles(user.getRoles());
            }
            existingUser.setName(user.getName());
            existingUser.setEmail(user.getEmail());
            existingUser.setAge(user.getAge());
            userRepository.save(existingUser);
            return true;
        }
        return false;
    }

    @Override
    public boolean addUser(UserDto user) {
        User presentUser = userRepository.findByName(user.getName());
        if (presentUser == null) {
            presentUser = new User(user);
            if (presentUser.getRoles().isEmpty()) {
                presentUser.setRoles(Set.of(new Role(RoleDto.ROLE_USER)));
            }
            userRepository.save(presentUser);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean deleteUserByName(String name) {
        User existingUser = userRepository.findByName(name);
        if (existingUser != null) {
            userRepository.delete(existingUser);
            return true;
        }
        return false;
    }


}
