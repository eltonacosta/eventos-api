package dev.eltoncosta.eventos_api.dto;

import java.time.LocalTime;
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
public class HorarioDisponivelResponse {

    private LocalTime inicio;
    private LocalTime fim;
}
