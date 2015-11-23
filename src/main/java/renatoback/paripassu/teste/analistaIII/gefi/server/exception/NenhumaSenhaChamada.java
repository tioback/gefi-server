package renatoback.paripassu.teste.analistaIII.gefi.server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Nenhuma senha chamada até o momento.")
public class NenhumaSenhaChamada extends RuntimeException {

	private static final long serialVersionUID = 1L;

}