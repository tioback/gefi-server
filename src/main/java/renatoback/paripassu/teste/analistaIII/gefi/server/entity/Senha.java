package renatoback.paripassu.teste.analistaIII.gefi.server.entity;

import renatoback.paripassu.teste.analistaIII.gefi.server.exception.CodigoInvalido;

public class Senha {

	private Tipo tipo;
	private Integer sequencial;

	public Senha() {
	}

	public Senha(Tipo tipo, Integer sequencial) {
		super();
		this.tipo = tipo;
		this.sequencial = sequencial;
	}

	public Senha(String codigo) {
		try {
			if (codigo.length() < 5) {
				throw new Exception();
			}

			this.tipo = Tipo.getBySigla(codigo.substring(0, 1));
			this.sequencial = Integer.parseInt(codigo.substring(1));
		} catch (Exception e) {
			throw new CodigoInvalido();
		}
	}

	public Tipo getTipo() {
		return tipo;
	}

	public void setTipo(Tipo tipo) {
		this.tipo = tipo;
	}

	public Integer getSequencial() {
		return sequencial;
	}

	public void setSequencial(Integer sequencial) {
		this.sequencial = sequencial;
	}

	public String getCodigo() {
		return tipo.getSigla() + String.format("%04d", sequencial);
	}

	public int getPrioridade() {
		return tipo.getPrioridade() + sequencial;
	}

}
