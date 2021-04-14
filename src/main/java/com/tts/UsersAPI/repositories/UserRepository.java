package com.tts.UsersAPI.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tts.UsersAPI.models.User;

@Repository
public interface UserRepository extends CrudRepository<User, Long>
{
    public List<User> findByState(String state);
    
    // Spring will guarantee to return a list
    @Override
    public List<User> findAll();
}
