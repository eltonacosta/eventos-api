package dev.eltoncosta.eventos_api.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import dev.eltoncosta.eventos_api.dto.AmbienteRequest;
import dev.eltoncosta.eventos_api.dto.AmbienteResponse;
import dev.eltoncosta.eventos_api.model.Ambiente;

@Component
public class AmbienteMapper {

    public Ambiente toEntity(AmbienteRequest request) {
        return Ambiente.builder()
                .tipo(request.getTipo())
                .descricao(request.getDescricao())
                .build();
    }

    public AmbienteResponse toResponse(Ambiente ambiente) {
        return AmbienteResponse.builder()
                .id(ambiente.getId())
                .tipo(ambiente.getTipo())
                .descricao(ambiente.getDescricao())
                .build();
    }

    public List<AmbienteResponse> toResponseList(List<Ambiente> ambientes) {
        return ambientes.stream()
                .map(this::toResponse)
                .toList();
    }
}
