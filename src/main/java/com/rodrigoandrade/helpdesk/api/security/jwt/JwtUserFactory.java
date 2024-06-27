package com.rodrigoandrade.helpdesk.api.security.jwt;

import com.rodrigoandrade.helpdesk.api.entity.User;
import com.rodrigoandrade.helpdesk.api.enums.ProfileEnum;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;

/***
 * Classe que converte o perfil do usuário para o usuário reconhecido no spring security
 */
public class JwtUserFactory {

    private JwtUserFactory() {
    }

    public static JwtUser create(User user){
        return new JwtUser(user.getId(), user.getEmail(), user.getPassword(),
                        mapToGrantedAuthorities(user.getProfile()));
    }


    private static List<GrantedAuthority> mapToGrantedAuthorities(ProfileEnum profile) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        authorities.add(new SimpleGrantedAuthority(profile.toString()));

        return authorities;
    }
}
