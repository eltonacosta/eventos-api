package dev.eltoncosta.eventos_api.repository.specification;

import java.time.LocalDate;
import java.time.LocalDateTime;

import dev.eltoncosta.eventos_api.model.Reserva;
import org.springframework.data.jpa.domain.Specification;

public class ReservaSpecification {

    private ReservaSpecification() {
    }

    public static Specification<Reserva> fetchSala() {
        return (root, query, criteriaBuilder) -> {
            if (query.getResultType() != Long.class) {
                root.fetch("sala");
            }

            return criteriaBuilder.conjunction();
        };
    }

    public static Specification<Reserva> dataReservadaInicioEntre(LocalDate inicio, LocalDate fim) {
        return (root, query, criteriaBuilder) -> {
            LocalDateTime inicioDoPeriodo = inicio.atStartOfDay();
            LocalDateTime fimDoPeriodo = fim.plusDays(1).atStartOfDay();

            return criteriaBuilder.and(
                    criteriaBuilder.greaterThanOrEqualTo(root.get("dataReservadaInicio"), inicioDoPeriodo),
                    criteriaBuilder.lessThan(root.get("dataReservadaInicio"), fimDoPeriodo));
        };
    }

}
