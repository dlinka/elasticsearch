package com.cr.elasticsearch.service;

import com.cr.elasticsearch.entity.User;
import com.cr.elasticsearch.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 使用ElasticsearchRepository操作ES
 */
@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    public User createUser(Long uid, String name) {
        User user = new User(uid, name);
        return userRepository.save(user);
    }

    public void deleteUser(Long uid) {
        userRepository.deleteById(uid);
    }

    public void updateUser(User user) {
        userRepository.save(user);
    }

    public Optional<User> findById(Long uid){
        return userRepository.findById(uid);
    }

}
