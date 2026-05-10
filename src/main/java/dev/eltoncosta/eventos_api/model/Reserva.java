package dev.eltoncosta.eventos_api.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "reservas")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;

    private LocalDate dataDeReserva;

    private LocalDateTime dataReservadaInicio;
    private LocalDateTime dataReservadaFim;

    private String descricaoReserva;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ambiente_id", nullable = false)
    private Ambiente sala;

}
