package ru.itmentor.spring.boot_security.demo.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.itmentor.spring.boot_security.demo.dto.UserDto;
import ru.itmentor.spring.boot_security.demo.service.AppService;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private AppService service;

    @Autowired
    public void setService(AppService service) {
        this.service = service;
    }

    @GetMapping(value = "/all_users")
    public ResponseEntity<List<UserDto>> allUsers() {
        List<UserDto> userList = service.getAllUsers();
        return ResponseEntity.ok(userList);
    }

    @GetMapping(value = "/current_user")
    public ResponseEntity<UserDto> currentUser() {
        return ResponseEntity.ok(service.getCurrentUser());
    }


    @PostMapping(value = "/updateUser")
    public ResponseEntity<String> updateUser(@RequestBody UserDto user) {
        // Call the UserService implementation to update the user
        if (service.updateUser(user)) {
            return ResponseEntity.ok("User data updated successfully");
        } else {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

    }

    @PostMapping(value = "/saveUser")
    public ResponseEntity<String> saveNewUser(@RequestBody UserDto user) {
        if (service.addUser(user)) {
            return ResponseEntity.ok("User was saved");
        } else {
            return new ResponseEntity<>("User already exists", HttpStatus.CONFLICT);
        }

    }


    @DeleteMapping(value = "/deleteUser/{name}")
    public ResponseEntity<String> deleteUser(@PathVariable String name) {
        if (service.deleteUserByName(name)) {
            return ResponseEntity.ok("User was deleted successfully");
        } else {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
    }

}
