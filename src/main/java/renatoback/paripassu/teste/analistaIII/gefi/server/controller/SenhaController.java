package renatoback.paripassu.teste.analistaIII.gefi.server.controller;

import java.beans.PropertyEditorSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import renatoback.paripassu.teste.analistaIII.gefi.server.entity.Senha;
import renatoback.paripassu.teste.analistaIII.gefi.server.entity.Tipo;
import renatoback.paripassu.teste.analistaIII.gefi.server.exception.CodigoNaoEncontrado;
import renatoback.paripassu.teste.analistaIII.gefi.server.exception.NenhumaSenhaChamada;
import renatoback.paripassu.teste.analistaIII.gefi.server.exception.SemSenhasParaChamar;
import renatoback.paripassu.teste.analistaIII.gefi.server.service.SenhaService;

@RestController
@RequestMapping(value = "/senha", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class SenhaController {

	@Autowired
	private SenhaService senhaService;

	@Autowired
	private SimpMessageSendingOperations mensageria;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Tipo.class, new PropertyEditorSupport() {
			@Override
			public void setAsText(String text) throws IllegalArgumentException {
				setValue(Tipo.getBySigla(text));
			}
		});
		binder.registerCustomEditor(Senha.class, new PropertyEditorSupport() {
			@Override
			public void setAsText(String text) throws IllegalArgumentException {
				setValue(new Senha(text));
			}
		});
	}

	@RequestMapping(value = "/{senha}", method = RequestMethod.GET)
	public Senha buscaSenha(@PathVariable Senha senha) {
		return senhaService.buscaSenha(senha).orElseThrow(CodigoNaoEncontrado::new);
	}

	@RequestMapping(method = RequestMethod.GET)
	public Senha ultimaSenhaChamada() {
		return senhaService.buscaUltimaSenhaChamada().orElseThrow(NenhumaSenhaChamada::new);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/{tipo}")
	@ResponseStatus(value = HttpStatus.CREATED)
	public ResponseEntity<Senha> geraSenha(@PathVariable Tipo tipo, UriComponentsBuilder uriComponentsBuilder) {
		Senha senha = senhaService.geraNovaSenha(tipo);

		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(uriComponentsBuilder.path("/senha/{codigo}").buildAndExpand(senha.getCodigo()).toUri());
		return new ResponseEntity<Senha>(senha, headers, HttpStatus.CREATED);
	}

	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	public Senha chamaProximaSenha() {
		Senha senha = senhaService.chamaProximaSenha().orElseThrow(SemSenhasParaChamar::new);
		mensageria.convertAndSend("/topic/senha", senha);
		return senha;
	}

	@RequestMapping(method = RequestMethod.PUT, value = "/{tipo}")
	@ResponseStatus(value = HttpStatus.OK)
	public void reiniciaSenha(@PathVariable Tipo tipo) {
		senhaService.reiniciaSenha(tipo);
	}

	@RequestMapping(value = "/{senha}", method = RequestMethod.DELETE)
	@ResponseStatus(value = HttpStatus.OK)
	public void delete(@PathVariable Senha senha) {
		senhaService.excluiSenha(senha);
	}

}