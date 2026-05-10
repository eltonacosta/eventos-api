package dev.eltoncosta.eventos_api.service;

import dev.eltoncosta.eventos_api.exception.ConflitoHorarioException;
import dev.eltoncosta.eventos_api.repository.specification.ReservaSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import dev.eltoncosta.eventos_api.dto.ReservaRequest;
import dev.eltoncosta.eventos_api.dto.ReservaResponse;
import dev.eltoncosta.eventos_api.mapper.ReservaMapper;
import dev.eltoncosta.eventos_api.model.Ambiente;
import dev.eltoncosta.eventos_api.model.Reserva;
import dev.eltoncosta.eventos_api.repository.AmbienteRepository;
import dev.eltoncosta.eventos_api.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final AmbienteRepository ambienteRepository;
    private final ReservaMapper reservaMapper;

    public ReservaResponse salvar(ReservaRequest request) {
        Ambiente sala = buscarAmbiente(request.getAmbienteId());
        validarHorarioDisponivel(request);
        Reserva reserva = reservaMapper.toEntity(request, sala);
        Reserva reservaSalva = reservaRepository.save(reserva);
        return reservaMapper.toResponse(reservaSalva);
    }

    public ReservaResponse buscarPorId(Long id) {
        Reserva reserva = buscarReserva(id);
        return reservaMapper.toResponse(reserva);
    }

    public ReservaResponse atualizar(Long id, ReservaRequest request) {
        Reserva reserva = buscarReserva(id);
        Ambiente sala = buscarAmbiente(request.getAmbienteId());
        validarHorarioDisponivelParaAtualizacao(id, request);
        reservaMapper.updateEntity(reserva, request, sala);
        Reserva reservaAtualizada = reservaRepository.save(reserva);
        return reservaMapper.toResponse(reservaAtualizada);
    }

    public void deletar(Long id) {
        reservaRepository.deleteById(id);
    }

    public Page<ReservaResponse> listar(Pageable pageable) {
        return reservaRepository.findAllComSala(pageable)
                .map(reservaMapper::toResponse);
    }

    public Page<ReservaResponse> listarPorPeriodo(LocalDate inicio, LocalDate fim, Pageable pageable) {
        Pageable pageableOrdenado = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.ASC, "dataReservadaInicio")
        );

        Specification<Reserva> specification = Specification
                .where(ReservaSpecification.fetchSala())
                .and(ReservaSpecification.dataReservadaInicioEntre(inicio, fim));

        return reservaRepository.findAll(specification, pageableOrdenado)
                .map(reservaMapper::toResponse);
    }

    private Reserva buscarReserva(Long id) {
        return reservaRepository.findByIdComSala(id)
                .orElseThrow(() -> new RuntimeException("Reserva nao encontrada"));
    }

    private Ambiente buscarAmbiente(Long id) {
        return ambienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ambiente nao encontrado"));
    }

    private void validarHorarioDisponivel(ReservaRequest request) {
        validarPeriodo(request);

        boolean existeConflito = reservaRepository.existsConflitoHorario(
                request.getAmbienteId(),
                request.getDataReservadaInicio(),
                request.getDataReservadaFim());

        if (existeConflito) {
            throw new ConflitoHorarioException("Ja existe reserva para este ambiente no horario informado");
        }
    }

    private void validarHorarioDisponivelParaAtualizacao(Long reservaId, ReservaRequest request) {
        validarPeriodo(request);

        boolean existeConflito = reservaRepository.existsConflitoHorarioIgnorandoReserva(
                reservaId,
                request.getAmbienteId(),
                request.getDataReservadaInicio(),
                request.getDataReservadaFim());

        if (existeConflito) {
            throw new ConflitoHorarioException("Ja existe reserva para este ambiente no horario informado");
        }
    }

    private void validarPeriodo(ReservaRequest request) {
        if (!request.getDataReservadaInicio().isBefore(request.getDataReservadaFim())) {
            throw new RuntimeException("Horario inicial deve ser anterior ao horario final");
        }
    }
}
