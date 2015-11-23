package renatoback.paripassu.teste.analistaIII.gefi.server.repository;

import java.util.concurrent.PriorityBlockingQueue;

import org.springframework.stereotype.Repository;

import renatoback.paripassu.teste.analistaIII.gefi.server.entity.Senha;

@Repository
public class SenhaRepository {

	private static PriorityBlockingQueue<Senha> senhas = new PriorityBlockingQueue<>(10, (o1, o2) -> {
		return Integer.compare(o1.getPrioridade(), o2.getPrioridade());
	});

	public Senha busca(Senha senha) {
		if (senhas.contains(senha)) {
			return senha;
		}
		return null;
	}

	public void salva(Senha senha) {
		senhas.add(senha);
	}

	public boolean remove(Senha senha) {
		return senhas.remove(senha);
	}

	public Senha primeiraDaFila() {
		return senhas.poll();
	}

	public void deleteAllInBatch() {
		senhas.clear();
	}

}
