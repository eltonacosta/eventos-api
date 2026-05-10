package dev.eltoncosta.eventos_api.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import dev.eltoncosta.eventos_api.dto.ReservaRequest;
import dev.eltoncosta.eventos_api.dto.ReservaResponse;
import dev.eltoncosta.eventos_api.model.Ambiente;
import dev.eltoncosta.eventos_api.model.Reserva;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReservaMapper {

    private final AmbienteMapper ambienteMapper;

    public Reserva toEntity(ReservaRequest request, Ambiente sala) {
        return Reserva.builder()
                .dataDeReserva(request.getDataDeReserva())
                .dataReservadaInicio(request.getDataReservadaInicio())
                .dataReservadaFim(request.getDataReservadaFim())
                .descricaoReserva(request.getDescricaoReserva())
                .sala(sala)
                .build();
    }

    public void updateEntity(Reserva reserva, ReservaRequest request, Ambiente sala) {
        reserva.setDataDeReserva(request.getDataDeReserva());
        reserva.setDataReservadaInicio(request.getDataReservadaInicio());
        reserva.setDataReservadaFim(request.getDataReservadaFim());
        reserva.setDescricaoReserva(request.getDescricaoReserva());
        reserva.setSala(sala);
    }

    public ReservaResponse toResponse(Reserva reserva) {
        return ReservaResponse.builder()
                .id(reserva.getId())
                .dataDeReserva(reserva.getDataDeReserva())
                .dataReservadaInicio(reserva.getDataReservadaInicio())
                .dataReservadaFim(reserva.getDataReservadaFim())
                .descricaoReserva(reserva.getDescricaoReserva())
                .sala(ambienteMapper.toResponse(reserva.getSala()))
                .build();
    }

    public List<ReservaResponse> toResponseList(List<Reserva> reservas) {
        return reservas.stream()
                .map(this::toResponse)
                .toList();
    }
}
