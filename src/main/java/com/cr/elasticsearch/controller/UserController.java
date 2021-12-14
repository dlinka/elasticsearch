package com.cr.elasticsearch.controller;

import com.cr.elasticsearch.entity.User;
import com.cr.elasticsearch.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/user/create")
    public User createUser() {
        return userService.createUser(1L, "CR");
    }

    @GetMapping("/user/delete")
    public void deleteUser() {
        userService.deleteUser(1L);
    }

    @GetMapping("/user/update")
    public void updateUser() {
        userService.updateUser(new User(1L, "CR27"));
    }

    @GetMapping("/user/find")
    public User findUser() {
        return userService.findById(1L).get();
    }

}
