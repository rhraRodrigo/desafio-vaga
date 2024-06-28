package com.rodrigoandrade.helpdesk.api.Service.impl;

import com.rodrigoandrade.helpdesk.api.Service.TicketService;
import com.rodrigoandrade.helpdesk.api.Service.UserService;
import com.rodrigoandrade.helpdesk.api.dto.Summary;
import com.rodrigoandrade.helpdesk.api.entity.ChangeStatus;
import com.rodrigoandrade.helpdesk.api.entity.Ticket;
import com.rodrigoandrade.helpdesk.api.entity.User;
import com.rodrigoandrade.helpdesk.api.enums.ProfileEnum;
import com.rodrigoandrade.helpdesk.api.enums.StatusEnum;
import com.rodrigoandrade.helpdesk.api.repository.ChangeStatusRepository;
import com.rodrigoandrade.helpdesk.api.repository.TicketRepository;
import com.rodrigoandrade.helpdesk.api.repository.UserRepository;
import com.rodrigoandrade.helpdesk.api.response.Response;
import com.rodrigoandrade.helpdesk.api.security.jwt.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

@Service
public class TicketServiceImpl implements TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private ChangeStatusRepository changeStatusRepository;

    @Autowired
    protected JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Response<Ticket> delete(String id) {
        Response<Ticket> response = new Response<>();
        Ticket ticket = ticketRepository.findById(id).orElse(null);

        if(ticket==null){
            response.setErros(
                    List.of("Register not found id" + id)
            );
        }else {
            this.ticketRepository.deleteById(id);
        }
        return response;
    }

    @Override
    public Response<Summary> findSummary() {
        Response<Summary> response = new Response<>();
        Summary summary = new Summary();

        int amountNew =0;
        int amountResolved=0;
        int amountApproved=0;
        int amountDisaproved=0;
        int amountAssigned=0;
        int amountClosed=0;

        List<Ticket> tickets = ticketRepository.findAll();
        if(!tickets.isEmpty()){
            Iterator<Ticket> iterator = tickets.iterator();
            if (iterator.hasNext()) {
                do {
                    Ticket ticket = iterator.next();
                    switch (ticket.getStatus()) {
                        case NEW:
                            amountNew++;
                            break;
                        case RESOLVED:
                            amountResolved++;
                            break;
                        case APPROVED:
                            amountApproved++;
                            break;
                        case REJECTED:
                            amountDisaproved++;
                            break;
                        case ASSIGNED:
                            amountAssigned++;
                            break;
                        case CLOSED:
                            amountClosed++;
                            break;

                    }
                } while (iterator.hasNext());
            }
        }

        summary.setAmountNew(amountNew);
        summary.setAmountResolved(amountResolved);
        summary.setAmountApproved(amountApproved);
        summary.setAmountDisaproved(amountDisaproved);
        summary.setAmountAssigned(amountAssigned);
        summary.setAmountClosed(amountClosed);

        response.setData(summary);

        return response;
    }

    @Override
    public Response<Ticket> changeTicketStatus(String id, String status, HttpServletRequest request) {
        Response<Ticket> response= new Response<>();

        try {
            validateChangeStatus(id, status, response);

            if(response.getErros() != null && !response.getErros().isEmpty()){
                return response;
            }

            Ticket currentTicket = ticketRepository.findById(id).orElse(null);

            if(currentTicket != null) {

                currentTicket.setStatus(StatusEnum.getStatus(status));

                if (status.equals("Assigned")) {
                    currentTicket.setAssignedUser(userFromRequest(request));
                }

                Ticket ticketPersisted = ticketRepository.save(currentTicket);

                ChangeStatus changeStatus = new ChangeStatus();
                changeStatus.setUserChange(userFromRequest(request));
                changeStatus.setDateChangeStatus(LocalDate.now());
                changeStatus.setStatus(StatusEnum.getStatus(status));
                changeStatus.setTicket(ticketPersisted);

                changeStatusRepository.save(changeStatus);

                response.setData(ticketPersisted);
            }
            else {
                response.setErros(
                        List.of("Ticket not found id " + id)
                );
            }
        }catch (Exception e){
            response.getErros().add(e.getMessage());
        }

        return response;
    }

    @Override
    public Response<Page<Ticket>> findByTicketParameters(int page, int count, Integer number, String title, String status, String priority, HttpServletRequest request) {
        title = title.equals("uninformed") ? "" : title;
        status = status.equals("uninformed") ? "" : status;
        priority = priority.equals("uninformed") ? "" : priority;

        Response<Page<Ticket>> response= new Response<>();

        Page<Ticket> tickets = null;

        if(number > 0){
            Pageable pages = PageRequest.of(page, count);
            tickets = ticketRepository.findByNumber(number, pages);
        }else{
            User userResquest = userFromRequest(request);
            Pageable pages = PageRequest.of(page, count);

            if(userResquest.getProfile().equals(ProfileEnum.ROLE_TECHNICIAN)){
                tickets = ticketRepository
                        .findByTitleIgnoreCaseContainingAndStatusAndPriorityOrderByDateDesc(title, status
                                , priority, pages);
            }else if(userResquest.getProfile().equals(ProfileEnum.ROLE_CUSTOMER)){
                tickets = ticketRepository
                        .findByTitleIgnoreCaseContainingAndStatusAndPriorityAndUserIdOrderByDateDesc(title, status
                                , priority, userResquest.getId(), pages);
            }

        }
        response.setData(tickets);

        return response;
    }

    @Override
    public Response<Page<Ticket>> findAllTickets(HttpServletRequest request, int page, int count) {
        Response<Page<Ticket>> response= new Response<>();
        Page<Ticket> tickets = null;

        User userRequest = userFromRequest(request);

        Pageable pages = PageRequest.of(page, count);

        if(userRequest.getProfile().equals(ProfileEnum.ROLE_TECHNICIAN)){
            tickets = ticketRepository.findAll(pages);
        }else if(userRequest.getProfile().equals(ProfileEnum.ROLE_CUSTOMER)){
            tickets = ticketRepository.findByUserIdOrderByDateDesc(pages, userRequest.getId());
        }
        response.setData(tickets);

        return response;
    }

    @Override
    public Response<Ticket> findTicketById(String id) {
        Response<Ticket> response = new Response<>();

        Ticket ticket = ticketRepository.findById(id).orElse(null);

        if(ticket==null){
            response.setErros(
                    List.of("Register not found id " + id)
            );
        }else {

            List<ChangeStatus> changes = new ArrayList<>();
            Iterable<ChangeStatus> changesCurrent = changeStatusRepository.findByTicketIdOrderByDateChangeStatusDesc(ticket.getId());
            Iterator<ChangeStatus> iterator = changesCurrent.iterator();
            if (iterator.hasNext()) {
                do {
                    ChangeStatus changeStatus = iterator.next();
                    changeStatus.setTicket(null);
                    changes.add(changeStatus);
                } while (iterator.hasNext());
            }
            ticket.setChanges(changes);
            response.setData(ticket);
        }
        return response;
    }

    @Override
    public Response<Ticket> updateTicket(HttpServletRequest request, Ticket ticket, BindingResult result) {
        Response<Ticket> response = new Response<>();

        try {
            validateUpdateTicket(ticket, result);
            if(result.hasErrors()){
                result.getAllErrors().forEach(error -> response.getErros().add(error.getDefaultMessage()));

            }else{
                Ticket currentTicket = ticketRepository.findById(ticket.getId()).orElse(null);

                if(currentTicket != null) {
                    ticket.setStatus(currentTicket.getStatus());
                    ticket.setUser(currentTicket.getUser());
                    ticket.setDate(currentTicket.getDate());
                    ticket.setNumber(currentTicket.getNumber());

                    if (currentTicket.getAssignedUser() != null) {
                        ticket.setAssignedUser(currentTicket.getAssignedUser());
                    }

                    Ticket ticketPersisted = ticketRepository.save(ticket);

                    response.setData(ticketPersisted);
                }else {
                    response.setErros(
                            List.of("Register not found id " + ticket.getId())
                    );
                }
            }
        }catch (Exception e){
            response.getErros().add(e.getMessage());

        }

        return response;
    }

    @Override
    public Response<Ticket> createTicket(HttpServletRequest request, Ticket ticket, BindingResult result) {
        Response<Ticket> response = new Response<>();

        try {
            validateCreateTicket(ticket, result);
            if(result.hasErrors()){
                result.getAllErrors().forEach(error -> response.getErros().add(error.getDefaultMessage()));
            }

            ticket.setStatus(StatusEnum.getStatus("NEW"));
            ticket.setUser(userFromRequest(request));
            ticket.setDate(LocalDate.now());
            ticket.setNumber(generateRandomNumber());

            Ticket ticketPersisted = ticketRepository.save(ticket);

            response.setData(ticketPersisted);
        }catch (Exception e){
            response.setErros(
                    List.of(e.getMessage())
            );
        }

        return response;
    }

    private Integer generateRandomNumber() {
        Random random = new Random();
        return random.nextInt(9999);
    }

    private void validateCreateTicket(Ticket ticket, BindingResult result){
        if(ticket.getTitle()==null){
            result.addError(new ObjectError("Ticket", "Title not informed"));
        }
    }

    private void validateUpdateTicket(Ticket ticket, BindingResult result){
        if(ticket.getId()==null){
            result.addError(new ObjectError("Ticket", "Id not informed"));

        }
        if(ticket.getTitle()==null){
            result.addError(new ObjectError("Ticket", "Title not informed"));

        }
    }

    public User userFromRequest(HttpServletRequest request){
        String token = request.getHeader("Authorization");
        String email = jwtTokenUtil.getUsernameFromToken(token);
        return userRepository.findByEmail(email);
    }

    private void validateChangeStatus(String id, String status, Response<Ticket> response){

        if(id==null || id.isEmpty()){
            response.getErros().add(String.valueOf(new ObjectError("Ticket", "Id not informed")));
        }
        if(status==null || status.isEmpty()){
            response.getErros().add(String.valueOf(new ObjectError("Ticket", "Status not informed")));

        }
    }
}
