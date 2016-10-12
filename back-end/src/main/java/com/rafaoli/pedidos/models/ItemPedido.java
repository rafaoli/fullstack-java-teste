package com.rafaoli.pedidos.models;

import java.io.Serializable;

public class ItemPedido implements Serializable {

	private static final long serialVersionUID = 1L;

	private long id;
	
	private Product produto;
	
	private float quantidade;
	
	private float vlrUnit;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Product getProduto() {
		return produto;
	}

	public void setProduto(Product produto) {
		this.produto = produto;
	}

	public float getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(float quantidade) {
		this.quantidade = quantidade;
	}

	public float getVlrUnit() {
		return vlrUnit;
	}

	public void setVlrUnit(float vlrUnit) {
		this.vlrUnit = vlrUnit;
	}

	
	
	

}
