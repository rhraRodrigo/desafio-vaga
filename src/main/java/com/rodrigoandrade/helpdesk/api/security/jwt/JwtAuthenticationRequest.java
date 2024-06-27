package com.rodrigoandrade.helpdesk.api.security.jwt;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class JwtAuthenticationRequest implements Serializable {
    private static final long serialVersionUID = 518746464449689242L;

    private String email;
    private String password;

    public JwtAuthenticationRequest(){
        super();
    }

    public JwtAuthenticationRequest(String email, String password){
        this.setEmail(email);
        this.setPassword(password);
    }

}
