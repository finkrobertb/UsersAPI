package com.tts.UsersAPI.controllers;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tts.UsersAPI.models.User;
import com.tts.UsersAPI.repositories.UserRepository;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;


@RestController
@Api(value="users", description="Operations to view/create/update/delete users")
@RequestMapping("/v1")
public class UserControllerV1
{

    @Autowired
    private UserRepository repository;

    // If user passes in query parameter for state, we find the user by state...
    // If user passes in no value, we find all users
    @ApiOperation(value = "Get all users", response = User.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved users")
        })
    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers(@RequestParam(value = "state", required = false) String state)
    {
        List<User> users;
        if(state != null)
        {
            users = repository.findByState(state);
            return new ResponseEntity<>(users, HttpStatus.OK);
        }

        users = repository.findAll();
        return new ResponseEntity<>(users, HttpStatus.OK);

    }

    // Find a user by id
    @GetMapping("/users/{id}")
    @ApiOperation(value = "Get a single user", response = User.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved menu items"),
            @ApiResponse(code = 404, message = "User wasn't found")
        })
    public ResponseEntity<Optional<User>> getUserById(@PathVariable(value = "id") Long id)
    {
        Optional<User> user = repository.findById(id);
        if(!user.isPresent())
        {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    // Create a new user
    @PostMapping("/users")
    @ApiOperation(value = "Create a user", response = Void.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully created user"),
            @ApiResponse(code = 400, message = "Bad request formatting or user exists")
        })
    public ResponseEntity<Void> createUser(@RequestBody @Valid User user, BindingResult bindingResult)
    {

        if(repository.findById(user.getId()) != null) {
            bindingResult.rejectValue("id", "error.id", "User id aleady exists");
        }
        if(bindingResult.hasErrors()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        repository.save(user);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    // Update an existing user by id
    @PutMapping("/users/{id}")
    @ApiOperation(value = "Update a user", response = Void.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "User updated successfully"),
            @ApiResponse(code = 400, message = "Bad request formatting"),
            @ApiResponse(code = 404, message = "User ID not found")
        })
    public ResponseEntity<Void> updateUser(@PathVariable(value = "id") Long id, @RequestBody User user, BindingResult bindingResult)
    {

        Optional<User> optUser = repository.findById(id);

        if(optUser.isEmpty())
        {
            bindingResult.rejectValue("id", "error.id", "User id not found");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if(bindingResult.hasErrors())
        {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        repository.save(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // Delete a user by id
    @DeleteMapping("/users/{id}")
    @ApiOperation(value = "Delete a user", response = Void.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "User deleted successfully"),
            @ApiResponse(code = 404, message = "User ID not found")
        })
    public ResponseEntity<Void> deleteUser(@PathVariable(value = "id") Long id)
    {
        Optional<User> optUser = repository.findById(id);

        if(optUser.isEmpty())
        {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        repository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
