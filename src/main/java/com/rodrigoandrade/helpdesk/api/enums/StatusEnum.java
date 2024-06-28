package com.rodrigoandrade.helpdesk.api.enums;

public enum StatusEnum {

    NEW, ASSIGNED, RESOLVED, APPROVED, REJECTED, CLOSED;

    public static StatusEnum getStatus(String status){
        switch (status) {
            case "NEW":
                return NEW;
            case "RESOLVED" : return RESOLVED;
            case "ASSIGNED" : return ASSIGNED;
            case "APPROVED": return APPROVED;
            case "REJECTED" : return REJECTED;
            case "CLOSED": return CLOSED;
            default: return NEW;
        }
    }
}
