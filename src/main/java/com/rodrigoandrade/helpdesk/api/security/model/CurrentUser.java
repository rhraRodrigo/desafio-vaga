package com.rodrigoandrade.helpdesk.api.security.model;

import com.rodrigoandrade.helpdesk.api.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CurrentUser {

    private String token;
    private User user;


}
