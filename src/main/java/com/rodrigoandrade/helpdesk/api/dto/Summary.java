package com.rodrigoandrade.helpdesk.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class Summary implements Serializable {


    private static final long serialVersionUID = 3115576674091318688L;

    private Integer amountNew;
    private Integer amountResolved;
    private Integer amountApproved;
    private Integer amountDisaproved;
    private Integer amountAssigned;
    private Integer amountClosed;


}
