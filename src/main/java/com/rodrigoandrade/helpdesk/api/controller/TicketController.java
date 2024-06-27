package com.rodrigoandrade.helpdesk.api.controller;

import com.rodrigoandrade.helpdesk.api.Service.TicketService;
import com.rodrigoandrade.helpdesk.api.dto.Summary;
import com.rodrigoandrade.helpdesk.api.entity.Ticket;
import com.rodrigoandrade.helpdesk.api.response.Response;
import com.rodrigoandrade.helpdesk.api.security.jwt.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(("/api/ticket"))
@CrossOrigin(origins = "*")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @PostMapping
    @PreAuthorize("hasAnyRole('CUSTOMER')")
    public ResponseEntity<Response<Ticket>> createTicket(HttpServletRequest request, @RequestBody Ticket ticket,
                                                   BindingResult result){
        Response<Ticket> response = ticketService.createTicket(request, ticket, result);

        if(response.getErros() != null && !response.getErros().isEmpty()){
            return ResponseEntity.badRequest().body(response);
        }

        return ResponseEntity.ok(response);
    }



    @PutMapping
    @PreAuthorize("hasAnyRole('CUSTOMER')")
    public ResponseEntity<Response<Ticket>> updateTicket(HttpServletRequest request, @RequestBody Ticket ticket, BindingResult result){
        Response<Ticket> response = ticketService.updateTicket(request, ticket, result);

        if(response.getErros() != null && !response.getErros().isEmpty()){
            return ResponseEntity.badRequest().body(response);
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'TECHNICIAN')")
    public ResponseEntity<Response<Ticket>> findTicketById(@PathVariable String id){
        Response<Ticket> response = ticketService.findTicketById(id);

        if(response.getErros() != null && !response.getErros().isEmpty()){
            return ResponseEntity.badRequest().body(response);
        }

        return ResponseEntity.ok(response);
    }

    @DeleteMapping({"{id}"})
    @PreAuthorize("hasAnyRole('CUSTOMER')")
    public ResponseEntity<Response<Ticket>> delete(@PathVariable String id){
        Response<Ticket> response = ticketService.delete(id);

        if(response.getErros() != null && !response.getErros().isEmpty()){
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("{page}/{count}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'TECHNICIAN')")
    public ResponseEntity<Response<Page<Ticket>>> findAllTickets(HttpServletRequest request, @PathVariable int page, @PathVariable int count){

        return ResponseEntity.ok(ticketService.findAllTickets(request, page, count));
    }

    @GetMapping("{page}/{count}/{number}/{title}/{status}/{priority}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'TECHNICIAN')")
    public ResponseEntity<Response<Page<Ticket>>> findByTicketParameters(HttpServletRequest request
            , @PathVariable int page
            , @PathVariable int count
            , @PathVariable Integer number
            , @PathVariable String title
            , @PathVariable String status
            , @PathVariable String priority){


        return ResponseEntity.ok(ticketService.findByTicketParameters(
                page, count, number, title, status, priority, request
        ));

    }

    @PatchMapping("{id}/{status}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'TECHNICIAN')")
    public ResponseEntity<Response<Ticket>> changeStatus(
            @PathVariable String id,
            @PathVariable String status,
            HttpServletRequest request){

        Response<Ticket> response = ticketService.changeTicketStatus(id, status, request);

        if(response.getErros() != null && !response.getErros().isEmpty()){
            return ResponseEntity.badRequest().body(response);
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/summary")
    public ResponseEntity<Response<Summary>> findSummary(){

        return ResponseEntity.ok(ticketService.findSummary());
    }

}
