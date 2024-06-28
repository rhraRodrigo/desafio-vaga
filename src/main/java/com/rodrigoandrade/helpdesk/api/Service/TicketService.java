package com.rodrigoandrade.helpdesk.api.Service;

import com.rodrigoandrade.helpdesk.api.dto.Summary;
import com.rodrigoandrade.helpdesk.api.entity.ChangeStatus;
import com.rodrigoandrade.helpdesk.api.entity.Ticket;
import com.rodrigoandrade.helpdesk.api.response.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;

@Component
public interface TicketService {

    Response<Ticket> delete(String id);

    Response<Summary> findSummary();

    Response<Ticket> changeTicketStatus(String id, String status, HttpServletRequest request);

    Response<Page<Ticket>> findByTicketParameters(int page, int count, Integer number, String title, String status, String priority, HttpServletRequest request);

    Response<Page<Ticket>> findAllTickets(HttpServletRequest request, int page, int count);

    Response<Ticket> findTicketById(String id);

    Response<Ticket> updateTicket(HttpServletRequest request, Ticket ticket, BindingResult result);

    Response<Ticket> createTicket(HttpServletRequest request, Ticket ticket, BindingResult result);
}
