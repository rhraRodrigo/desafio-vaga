package com.rodrigoandrade.helpdesk.api.entity;

import com.rodrigoandrade.helpdesk.api.enums.ProfileEnum;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Size;

@Data
@Document
public class User {

    @Id
    private String id;

    @Indexed(unique = true)
    private String email;

    @Size(min=6)
    private String password;

    private ProfileEnum profile;

}
