package com.rodrigoandrade.helpdesk.api.dto;

import java.io.Serializable;


public record Summary(Integer amountNew, Integer amountResolved,
                      Integer amountApproved, Integer amountDisaproved, Integer amountAssigned,
                      Integer amountClosed) implements Serializable {

}
