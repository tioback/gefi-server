package renatoback.paripassu.teste.analistaIII.gefi.server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Tipo inválido.")
public class TipoInvalido extends RuntimeException {

	private static final long serialVersionUID = 1L;

}