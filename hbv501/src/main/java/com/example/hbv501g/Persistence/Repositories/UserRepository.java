package com.example.hbv501g.Persistence.Repositories;

import com.example.hbv501g.Persistence.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User save(User user);

    void delete(User user);

    List<User> findAll();

    User findByUsername(String username);

    User findByEmail(String email);

    User findById(long ID);
}
