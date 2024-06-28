package com.rodrigoandrade.helpdesk.api.entity;

import com.rodrigoandrade.helpdesk.api.enums.PriorityEnum;
import com.rodrigoandrade.helpdesk.api.enums.StatusEnum;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;


@Data
@Document
public class Ticket {

    @Id
    private String id;

    @DBRef(lazy = true)
    private User user;

    private LocalDate date;

    private String title;

    private Integer number;

    private StatusEnum status;

    private PriorityEnum priority;

    @DBRef(lazy = true)
    private User assignedUser;

    private String description;

    private String image;

    @Transient
    private List<ChangeStatus> changes;


}
