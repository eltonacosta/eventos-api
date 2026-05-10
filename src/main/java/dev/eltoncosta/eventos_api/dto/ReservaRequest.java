package dev.eltoncosta.eventos_api.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservaRequest {

    private LocalDate dataDeReserva;
    private LocalDateTime dataReservadaInicio;
    private LocalDateTime dataReservadaFim;
    private String descricaoReserva;
    private Long ambienteId;
}
