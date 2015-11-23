package renatoback.paripassu.teste.analistaIII.gefi.server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.SERVICE_UNAVAILABLE, reason = "Serviço temporariamente indisponível.")
public class ServicoIndisponivel extends RuntimeException {

	private static final long serialVersionUID = 1L;

}
