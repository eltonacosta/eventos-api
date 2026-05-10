package dev.eltoncosta.eventos_api.exception;

public class BuscaNaoEncontrada extends RuntimeException {
    public BuscaNaoEncontrada(String message) {
        super(message);
    }
}
