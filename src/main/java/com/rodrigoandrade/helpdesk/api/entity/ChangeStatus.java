package com.rodrigoandrade.helpdesk.api.entity;

import com.rodrigoandrade.helpdesk.api.enums.StatusEnum;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Data
@Document
public class ChangeStatus {

    @Id
    private String id;

    @DBRef
    private Ticket ticket;

    @DBRef
    private User userChange;

    private LocalDate dateChangeStatus;

    private StatusEnum status;

}
