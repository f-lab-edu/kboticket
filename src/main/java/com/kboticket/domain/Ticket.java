package com.kboticket.domain;

import com.kboticket.enums.TicketStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Entity
@Table(name = "ticket")
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_seat_id")
    private OrderSeat orderSeat;

    private String ticketNumber;

    @JoinColumn(name = "ticket_nm")
    private String name;

    @ColumnDefault("0")
    private int price;

    private LocalDateTime issuedAt;

    private TicketStatus status;

}
