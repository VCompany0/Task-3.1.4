package ru.itmentor.spring.boot_security.demo.web.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ru.itmentor.spring.boot_security.demo.repositories.RoleRepository;
import ru.itmentor.spring.boot_security.demo.repositories.UserRepository;
import ru.itmentor.spring.boot_security.demo.role.Role;
import ru.itmentor.spring.boot_security.demo.role.RoleEnum;
import ru.itmentor.spring.boot_security.demo.user.User;
import ru.itmentor.spring.boot_security.demo.user.UserDto;

import java.util.*;


@RestController
public class AdminController {

    private UserRepository userRepository;
    private RoleRepository roleRepository;


    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setRoleRepository(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
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
//
//    @GetMapping("/admin/addUser")
//    public String addUser(Model model) {
//        // create model attribute to bind form data
//        UserDto user = new UserDto();
//        model.addAttribute("user", user);
//
//        model.addAttribute("currentUser", getCurrentUser());
//        return "add_user";
//    }

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
                presentUser.setRoles(Set.of(new Role(RoleEnum.ROLE_USER)));
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
