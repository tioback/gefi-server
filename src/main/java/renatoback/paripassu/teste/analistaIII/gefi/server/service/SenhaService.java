package renatoback.paripassu.teste.analistaIII.gefi.server.service;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import renatoback.paripassu.teste.analistaIII.gefi.server.entity.Senha;
import renatoback.paripassu.teste.analistaIII.gefi.server.entity.Tipo;
import renatoback.paripassu.teste.analistaIII.gefi.server.repository.SenhaRepository;

@Service
public class SenhaService {

	@Autowired
	private SenhaRepository repo;

	private static Senha ultimaSenhaChamada;
	private static AtomicInteger contadorNormal = new AtomicInteger(0);
	private static AtomicInteger contadorPrioritario = new AtomicInteger(0);

	public Optional<Senha> buscaSenha(Senha senha) {
		return Optional.ofNullable(repo.busca(senha));
	}

	public Optional<Senha> buscaUltimaSenhaChamada() {
		return Optional.ofNullable(ultimaSenhaChamada);
	}

	public Senha geraNovaSenha(Tipo tipo) {
		if (tipo == null) {
			throw new IllegalArgumentException();
		}

		Senha senha = new Senha(tipo, getContador(tipo).incrementAndGet());
		repo.salva(senha);
		return senha;
	}

	private AtomicInteger getContador(Tipo tipo) {
		if (Tipo.NORMAL.equals(tipo)) {
			return contadorNormal;
		}
		return contadorPrioritario;
	}

	public void excluiSenha(Senha senha) {
		repo.remove(senha);
	}

	public void reiniciaSenha(Tipo tipo) {
		getContador(tipo).set(0);
	}

	public Optional<Senha> chamaProximaSenha() {
		Optional<Senha> result = Optional.ofNullable(repo.primeiraDaFila());
		if (result.isPresent()) {
			ultimaSenhaChamada = result.get();
		}
		return result;
	}

	public void excluiTodas() {
		reiniciaSenha(Tipo.NORMAL);
		reiniciaSenha(Tipo.PREFERENCIAL);
		repo.deleteAllInBatch();
	}

}
