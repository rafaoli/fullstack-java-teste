package com.rafaoli.pedidos.models;

import java.io.Serializable;
import java.util.Date;
import java.util.List;




public class Pedido implements Serializable {
	
	
	private static final long serialVersionUID = 1L;
	
	private long id;
	private int numero;
	private Date dataEmissao;
	private float vlrTotal;
	
	private Cliente cliente;
	
	private List<ItemPedido> produtos;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getNumero() {
		return numero;
	}

	public void setNumero(int numero) {
		this.numero = numero;
	}

	public Date getDataEmissao() {
		return dataEmissao;
	}

	public void setDataEmissao(Date dataEmissao) {
		this.dataEmissao = dataEmissao;
	}

	public float getVlrTotal() {
		return vlrTotal;
	}

	public void setVlrTotal(float vlrTotal) {
		this.vlrTotal = vlrTotal;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public List<ItemPedido> getProdutos() {
		return produtos;
	}

	public void setProdutos(List<ItemPedido> produtos) {
		this.produtos = produtos;
	}


	
	

}
