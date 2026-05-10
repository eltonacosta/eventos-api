package dev.eltoncosta.eventos_api.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import dev.eltoncosta.eventos_api.dto.AmbienteRequest;
import dev.eltoncosta.eventos_api.dto.AmbienteResponse;
import dev.eltoncosta.eventos_api.mapper.AmbienteMapper;
import dev.eltoncosta.eventos_api.model.Ambiente;
import dev.eltoncosta.eventos_api.repository.AmbienteRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AmbienteService {

    private final AmbienteRepository ambienteRepository;
    private final AmbienteMapper ambienteMapper;

    public AmbienteResponse salvar(AmbienteRequest request) {
        Ambiente ambiente = ambienteMapper.toEntity(request);
        Ambiente ambienteSalvo = ambienteRepository.save(ambiente);
        return ambienteMapper.toResponse(ambienteSalvo);
    }

    public AmbienteResponse buscarPorId(Long id) {
        Ambiente ambiente = ambienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ambiente nao encontrado"));
        return ambienteMapper.toResponse(ambiente);
    }

    public void deletar(Long id) {
        ambienteRepository.deleteById(id);
    }

    public Page<AmbienteResponse> listar(Pageable pageable) {
        return ambienteRepository.findAll(pageable)
                .map(ambienteMapper::toResponse);
    }
}
