package dev.eltoncosta.eventos_api.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import dev.eltoncosta.eventos_api.model.Reserva;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    @Query(
            value = """
                    SELECT r
                    FROM Reserva r
                    JOIN FETCH r.sala
                    """,
            countQuery = """
                    SELECT COUNT(r)
                    FROM Reserva r
                    """)
    Page<Reserva> findAllComSala(Pageable pageable);

    @Query("""
            SELECT r
            FROM Reserva r
            JOIN FETCH r.sala
            WHERE r.id = :id
            """)
    Optional<Reserva> findByIdComSala(@Param("id") Long id);

    @Query("""
            SELECT COUNT(r) > 0
            FROM Reserva r
            WHERE r.sala.id = :ambienteId
              AND r.dataReservadaInicio < :fim
              AND r.dataReservadaFim > :inicio
            """)
    boolean existsConflitoHorario(
            @Param("ambienteId") Long ambienteId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim);

    @Query("""
            SELECT COUNT(r) > 0
            FROM Reserva r
            WHERE r.sala.id = :ambienteId
              AND r.id <> :reservaId
              AND r.dataReservadaInicio < :fim
              AND r.dataReservadaFim > :inicio
            """)
    boolean existsConflitoHorarioIgnorandoReserva(
            @Param("reservaId") Long reservaId,
            @Param("ambienteId") Long ambienteId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim);
}
