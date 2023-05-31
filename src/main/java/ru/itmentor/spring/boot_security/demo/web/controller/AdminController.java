package ru.itmentor.spring.boot_security.demo.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.itmentor.spring.boot_security.demo.dto.RoleDto;
import ru.itmentor.spring.boot_security.demo.dto.UserDto;
import ru.itmentor.spring.boot_security.demo.model.Role;
import ru.itmentor.spring.boot_security.demo.model.User;
import ru.itmentor.spring.boot_security.demo.repositories.UserRepository;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
public class AdminController {

    private UserRepository userRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @GetMapping(value = "/admin/all_users")
    public ResponseEntity<List<UserDto>> allUsers() {
        List<UserDto> userList = new LinkedList<>();
        userRepository.findAll().forEach(user -> userList.add(new UserDto(user)));
        return ResponseEntity.ok(userList);
    }

    @GetMapping(value = "/admin/current_user")
    public ResponseEntity<UserDto> currentUser() {
        return ResponseEntity.ok(getCurrentUser());
    }

    private UserDto getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return new UserDto(userRepository.findByName(authentication.getName()));
    }

    @RequestMapping(value = "/admin/updateUser", method = RequestMethod.POST)
    public ResponseEntity<String> updateUser(@RequestBody UserDto user) {
        // Call the UserService implementation to update the user
        Optional<User> userOptional = userRepository.findById(user.getId());
        if (userOptional.isPresent()) {
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
        }
        else {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok("User data updated successfully");
    }

    @RequestMapping(value = "/admin/saveUser", method = RequestMethod.POST)
    public ResponseEntity<String> saveNewUser(@RequestBody UserDto user) {
        User presentUser = userRepository.findByName(user.getName());
        if (presentUser == null) {
            presentUser = new User(user);
            if(presentUser.getRoles().isEmpty()){
                presentUser.setRoles(Set.of(new Role(RoleDto.ROLE_USER)));
            }
            userRepository.save(presentUser);
        } else {
            return new ResponseEntity<>("User already exists",HttpStatus.CONFLICT);
        }
        return ResponseEntity.ok("User was saved");
    }


    @RequestMapping(value = "/admin/deleteUser/{name}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteUser(@PathVariable String name) {
        User existingUser = userRepository.findByName(name);
        if(existingUser != null){
            userRepository.delete(existingUser);
            return ResponseEntity.ok("User was deleted successfully");
        }
        else{
            return new ResponseEntity<>("User not found",HttpStatus.NOT_FOUND);
        }
    }

}
