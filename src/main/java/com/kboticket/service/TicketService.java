package com.kboticket.service;

import com.kboticket.domain.Ticket;
import com.kboticket.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;

    public Ticket findOne(Long itemId) {
        return ticketRepository.findOne(itemId);
    }

}
