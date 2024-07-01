package com.rodrigoandrade.helpdesk.api.controller;

import com.mongodb.DuplicateKeyException;
import com.rodrigoandrade.helpdesk.api.Service.UserService;
import com.rodrigoandrade.helpdesk.api.entity.User;
import com.rodrigoandrade.helpdesk.api.response.Response;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name="Users", description = "Maintenance Users")
@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    private ResponseEntity<Response<User>> create(HttpServletRequest request, @RequestBody User user
            , BindingResult result){
        Response<User> response = new Response<>();
        try{
            validateCreateUser(user, result);

            if (persistPasswordIfNoErrorsFound(user, result, response))
                return ResponseEntity.badRequest().body(response);
        }catch (DuplicateKeyException e){

            response.setErros(
                    List.of("E-mail already registered!")
            );
            return ResponseEntity.badRequest().body(response);
        }
        catch (Exception e){
            response.setErros(
                    List.of(e.getMessage())
            );

            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Response<User>> update(HttpServletRequest request, @RequestBody User user
            , BindingResult result){
        Response<User> response = new Response<>();
        try {
            validateUpdateUser(user, result);
            if (persistPasswordIfNoErrorsFound(user, result, response))
                return ResponseEntity.badRequest().body(response);
        }catch (Exception e){
            response.setErros(
                    List.of(e.getMessage())
            );
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);

    }

    @GetMapping("{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Response<User>> findById(@PathVariable("id") String id){
        Response<User> response = new Response<>();
        User user = userService.findById(id);
        if(user==null){
            response.setErros(
                    List.of("Register not found id " + id)
            );
            return ResponseEntity.badRequest().body(response);
        }
        response.setData(user);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Response<String>> delete(@PathVariable("id") String id){
            Response<String> response=new Response<>();
            User user = userService.findById(id);
            if(user==null){
                response.setErros(
                        List.of("Register not found id " + id)
                );

                return ResponseEntity.badRequest().body(response);
            }
            userService.delete(id);
            return ResponseEntity.ok(new Response<>());
    }

    @GetMapping("{page}/{count}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Response<Page<User>>> findAll(@PathVariable int page, @PathVariable int count){
        Response<Page<User>> response=new Response<>();
        Page<User> users = userService.findAll(page, count);
        response.setData(users);
        return ResponseEntity.ok(response);
    }

    private boolean persistPasswordIfNoErrorsFound(@RequestBody User user, BindingResult result, Response<User> response) {
        if(result.hasErrors()){
            result.getAllErrors().forEach(error -> response.getErros().add(error.getDefaultMessage()));
            return true;
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User userPersisted = userService.createOrUpdate(user);
        response.setData(userPersisted);
        return false;
    }

    private void validateCreateUser(User user, BindingResult result){
        if(user.getEmail()==null){
            result.addError(new ObjectError("User", "Email not informed"));
        }
    }

    private void validateUpdateUser(User user, BindingResult result){
        if(user.getId()==null){
            result.addError(new ObjectError("User", "Id not informed"));
        }
        if(user.getEmail() == null){
            result.addError(new ObjectError("User", "Email not informed"));
        }
    }
}
