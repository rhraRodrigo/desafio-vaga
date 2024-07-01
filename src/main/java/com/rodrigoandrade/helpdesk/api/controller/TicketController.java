package com.rodrigoandrade.helpdesk.api.controller;

import com.rodrigoandrade.helpdesk.api.Service.TicketService;
import com.rodrigoandrade.helpdesk.api.dto.Summary;
import com.rodrigoandrade.helpdesk.api.entity.Ticket;
import com.rodrigoandrade.helpdesk.api.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Tag(name="Tickets", description = "Maintenance tickets")
@RestController
@RequestMapping(("/api/ticket"))
@CrossOrigin(origins = "*")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @Operation(summary = "Create ticket")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created the ticket",
                    content = {
                            @Content(mediaType = "application/json"
                            , schema = @Schema(implementation = Ticket.class))
            }),
            @ApiResponse(responseCode = "400", description = "Ticket not created", content = @Content)
    })

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


    @Operation(summary = "Update ticket")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Updated the ticket",
                    content = {
                            @Content(mediaType = "application/json"
                                    , schema = @Schema(implementation = Ticket.class))
                    }),
            @ApiResponse(responseCode = "400", description = "Ticket not updated", content = @Content)
    })
    @PutMapping
    @PreAuthorize("hasAnyRole('CUSTOMER')")
    public ResponseEntity<Response<Ticket>> updateTicket(HttpServletRequest request, @RequestBody Ticket ticket, BindingResult result){
        Response<Ticket> response = ticketService.updateTicket(request, ticket, result);

        if(response.getErros() != null && !response.getErros().isEmpty()){
            return ResponseEntity.badRequest().body(response);
        }

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Find ticket by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the ticket",
                    content = {@Content(schema = @Schema(implementation = Ticket.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "404", description = "Ticket not found", content = @Content)
    })
    @GetMapping("{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'TECHNICIAN')")
    public ResponseEntity<Response<Ticket>> findTicketById(@PathVariable String id){
        Response<Ticket> response = ticketService.findTicketById(id);

        if(response.getErros() != null && !response.getErros().isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete ticket")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "400", description = "Ticket not deleted", content = @Content)
    })
    @DeleteMapping({"{id}"})
    @PreAuthorize("hasAnyRole('CUSTOMER')")
    @SecurityRequirements()
    public ResponseEntity<Response<Ticket>> delete(@PathVariable String id){
        Response<Ticket> response = ticketService.delete(id);

        if(response.getErros() != null && !response.getErros().isEmpty()){
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Find paged ticket")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the ticket paged",
                    content = {
                            @Content(mediaType = "application/json"
                                    , schema = @Schema(implementation = Ticket.class))
                    })
    })
    @GetMapping("{page}/{count}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'TECHNICIAN')")
    public ResponseEntity<Response<Page<Ticket>>> findAllTickets(HttpServletRequest request, @PathVariable int page, @PathVariable int count){

        return ResponseEntity.ok(ticketService.findAllTickets(request, page, count));
    }

    @Operation(summary = "Find ticket by params")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found Ticket by params",
                    content = {
                            @Content(mediaType = "application/json"
                                    , schema = @Schema(implementation = Ticket.class))
                    }),
    })
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

    @Operation(summary = "Update ticket by id and status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Updated Ticket by id and status",
                    content = {
                            @Content(mediaType = "application/json"
                                    , schema = @Schema(implementation = Ticket.class))
                    }),
            @ApiResponse(responseCode = "400", description = "Ticket not updated", content = @Content)
    })
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

    @Operation(summary = "Find ticket summary")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Founded ticket summary",
                    content = {
                            @Content(mediaType = "application/json"
                                    , schema = @Schema(implementation = Summary.class))
                    })
    })
    @GetMapping("/summary")
    public ResponseEntity<Response<Summary>> findSummary(){

        return ResponseEntity.ok(ticketService.findSummary());
    }

}
