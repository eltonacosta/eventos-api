package dev.eltoncosta.eventos_api.service;

import dev.eltoncosta.eventos_api.exception.ConflitoHorarioException;
import dev.eltoncosta.eventos_api.repository.specification.ReservaSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import dev.eltoncosta.eventos_api.dto.HorarioDisponivelResponse;
import dev.eltoncosta.eventos_api.dto.ReservaRequest;
import dev.eltoncosta.eventos_api.dto.ReservaResponse;
import dev.eltoncosta.eventos_api.dto.SalaDisponivelResponse;
import dev.eltoncosta.eventos_api.mapper.AmbienteMapper;
import dev.eltoncosta.eventos_api.mapper.ReservaMapper;
import dev.eltoncosta.eventos_api.model.Ambiente;
import dev.eltoncosta.eventos_api.model.Reserva;
import dev.eltoncosta.eventos_api.repository.AmbienteRepository;
import dev.eltoncosta.eventos_api.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final AmbienteRepository ambienteRepository;
    private final ReservaMapper reservaMapper;
    private final AmbienteMapper ambienteMapper;

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

    public List<HorarioDisponivelResponse> listarHorariosDisponiveis(Long ambienteId, LocalDate data) {
        buscarAmbiente(ambienteId);

        LocalDateTime inicioDia = data.atStartOfDay();
        LocalDateTime fimDia = data.plusDays(1).atStartOfDay();

        List<Reserva> reservas = reservaRepository.findReservasDoDiaPorAmbiente(
                ambienteId,
                inicioDia,
                fimDia);

        return calcularHorariosDisponiveis(data, reservas);
    }

    public List<SalaDisponivelResponse> listarSalasDisponiveis(LocalDate data) {
        LocalDateTime inicioDia = data.atStartOfDay();
        LocalDateTime fimDia = data.plusDays(1).atStartOfDay();

        List<Ambiente> salas = ambienteRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
        List<Reserva> reservas = reservaRepository.findReservasDoDia(inicioDia, fimDia);
        Map<Long, List<Reserva>> reservasPorSala = reservas.stream()
                .collect(Collectors.groupingBy(reserva -> reserva.getSala().getId()));

        return salas.stream()
                .map(sala -> SalaDisponivelResponse.builder()
                        .sala(ambienteMapper.toResponse(sala))
                        .horariosDisponiveis(calcularHorariosDisponiveis(
                                data,
                                reservasPorSala.getOrDefault(sala.getId(), List.of())))
                        .build())
                .filter(salaDisponivel -> !salaDisponivel.getHorariosDisponiveis().isEmpty())
                .toList();
    }

    private List<HorarioDisponivelResponse> calcularHorariosDisponiveis(LocalDate data, List<Reserva> reservas) {
        LocalDateTime inicioDia = data.atStartOfDay();
        LocalDateTime fimDia = data.plusDays(1).atStartOfDay();
        List<HorarioDisponivelResponse> horariosDisponiveis = new ArrayList<>();
        LocalDateTime inicioDisponivel = inicioDia;

        for (Reserva reserva : reservas) {
            LocalDateTime inicioReserva = maiorDataHora(reserva.getDataReservadaInicio(), inicioDia);
            LocalDateTime fimReserva = menorDataHora(reserva.getDataReservadaFim(), fimDia.minusMinutes(1));

            if (inicioDisponivel.isBefore(inicioReserva)) {
                horariosDisponiveis.add(HorarioDisponivelResponse.builder()
                        .inicio(inicioDisponivel.toLocalTime())
                        .fim(inicioReserva.minusMinutes(1).toLocalTime())
                        .build());
            }

            LocalDateTime proximoInicioDisponivel = fimReserva.plusMinutes(1);
            if (proximoInicioDisponivel.isAfter(inicioDisponivel)) {
                inicioDisponivel = proximoInicioDisponivel;
            }
        }

        if (inicioDisponivel.isBefore(fimDia)) {
            horariosDisponiveis.add(HorarioDisponivelResponse.builder()
                    .inicio(inicioDisponivel.toLocalTime())
                    .fim(LocalTime.of(23, 59))
                    .build());
        }

        return horariosDisponiveis;
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

    private LocalDateTime maiorDataHora(LocalDateTime primeiraData, LocalDateTime segundaData) {
        return primeiraData.isAfter(segundaData) ? primeiraData : segundaData;
    }

    private LocalDateTime menorDataHora(LocalDateTime primeiraData, LocalDateTime segundaData) {
        return primeiraData.isBefore(segundaData) ? primeiraData : segundaData;
    }
}
