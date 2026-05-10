package dev.eltoncosta.eventos_api.global;

import dev.eltoncosta.eventos_api.dto.ErrorResponse;
import dev.eltoncosta.eventos_api.exception.BuscaNaoEncontrada;
import dev.eltoncosta.eventos_api.exception.ConflitoHorarioException;
import dev.eltoncosta.eventos_api.exception.HorarioErrorException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandlerGlobal {

    @ExceptionHandler(ConflitoHorarioException.class)
    public ResponseEntity<ErrorResponse> tratarConflitoHorarioException(ConflitoHorarioException ex) {
        ErrorResponse erro = new ErrorResponse(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    @ExceptionHandler(BuscaNaoEncontrada.class)
    public ResponseEntity<ErrorResponse> tratarBuscaNaoEncontradaException(BuscaNaoEncontrada ex) {
        ErrorResponse erro = new ErrorResponse(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    @ExceptionHandler(HorarioErrorException.class)
    public ResponseEntity<ErrorResponse> tratarHorarioErrorException(HorarioErrorException ex) {
        ErrorResponse erro = new ErrorResponse(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

}
