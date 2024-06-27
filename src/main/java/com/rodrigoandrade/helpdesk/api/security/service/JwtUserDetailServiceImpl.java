package com.rodrigoandrade.helpdesk.api.security.service;

import com.rodrigoandrade.helpdesk.api.Service.UserService;
import com.rodrigoandrade.helpdesk.api.entity.User;
import com.rodrigoandrade.helpdesk.api.security.jwt.JwtUserFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/***
 * Serviço que manipula a interface UserDetail
 */

@Service
public class JwtUserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private UserService userService;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userService.findByEmail(email);

        if(user==null)
        {
            throw new UsernameNotFoundException(String.format("No user found with username %s.", email));
        }

        return JwtUserFactory.create(user);
    }
}
