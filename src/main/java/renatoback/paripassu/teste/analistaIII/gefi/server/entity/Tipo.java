package renatoback.paripassu.teste.analistaIII.gefi.server.entity;

import renatoback.paripassu.teste.analistaIII.gefi.server.exception.TipoInvalido;

public enum Tipo {
	NORMAL("N", 10000), PREFERENCIAL("P", 0);

	private final String sigla;
	private final int prioridade;

	private Tipo(final String sigla, final int prioridade) {
		this.sigla = sigla;
		this.prioridade = prioridade;
	}

	public String getSigla() {
		return sigla;
	}

	public int getPrioridade() {
		return prioridade;
	}

	public static Tipo getBySigla(final String sigla) {
		if (NORMAL.sigla.equals(sigla)) {
			return NORMAL;
		}
		if (PREFERENCIAL.sigla.equals(sigla)) {
			return Tipo.PREFERENCIAL;
		}

		throw new TipoInvalido();
	}

}
