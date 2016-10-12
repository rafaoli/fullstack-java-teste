package com.rafaoli.pedidos.services;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.EmbeddedEntity;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.rafaoli.pedidos.models.Cliente;
import com.rafaoli.pedidos.models.Pedido;

@Path("/pedidos")
public class PedidoManager {
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Pedido> getPedidos() {

		List<Pedido> pedidos = new ArrayList<>();

		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();

		Query query;

		query = new Query("Pedidos").addSort("Numero", SortDirection.ASCENDING);

		List<Entity> entities = datastore.prepare(query).asList(
				FetchOptions.Builder.withDefaults());

		for (Entity pedidoEntity : entities) {

			Pedido pedido = entityToPedido(pedidoEntity);
			pedidos.add(pedido);
		}

		return pedidos;

	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Pedido save(Pedido pedido) {

		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();

		Key key = KeyFactory.createKey("Pedidos", "pedidoKey");

		Entity pedidoEntity = new Entity("Pedidos", key);

		pedidoToEntity(pedido, pedidoEntity);

		datastore.put(pedidoEntity);

		pedido.setId(pedidoEntity.getKey().getId());

		return pedido;

	}
	
	

	private void pedidoToEntity(Pedido pedido, Entity pedidoEntity) {

		pedidoEntity.setProperty("Numero", pedido.getNumero());
		pedidoEntity.setProperty("DataEmissao", pedido.getDataEmissao());
		pedidoEntity.setProperty("Cliente", pedido.getCliente().getId());
		pedidoEntity.setProperty("Produtos", pedido.getProdutos());
		pedidoEntity.setProperty("VlrTotal", pedido.getVlrTotal());

	}

	

	private Pedido entityToPedido(Entity pedidoEntity) {

		Pedido pedido = new Pedido();
		pedido.setId(pedidoEntity.getKey().getId());
		
		pedido.setNumero(Integer.parseInt(pedidoEntity.getProperty("Numero").toString()));
		pedido.setDataEmissao(convertDate(pedidoEntity.getProperty("DataEmissao").toString()));
		
		pedido.setCliente(getCliente(pedidoEntity.getProperty("Cliente").toString()));
		
		pedido.setVlrTotal(Float.parseFloat(pedidoEntity.getProperty("VlrTotal").toString()));

		return pedido;

	}
	
	private Cliente entityToCliente(Entity clienteEntity) {

		Cliente cliente = new Cliente();
		cliente.setId(clienteEntity.getKey().getId());
		
		cliente.setCodigo(Integer.parseInt(clienteEntity.getProperty("Codigo").toString()));
		cliente.setNome((String) clienteEntity.getProperty("Nome"));		
		cliente.setTelefone((String) clienteEntity.getProperty("Telefone"));	
		cliente.setCpf((String) clienteEntity.getProperty("Cpf"));		
		cliente.setEmail((String) clienteEntity.getProperty("Email"));
		
		
		
		return cliente;

	}
	
	private Cliente getCliente(String id){
		
		
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();

		Entity clienteEntity;
		
		try {
			
			Key key = KeyFactory.createKey("Clientes", id);
			
			clienteEntity = datastore.get(key);
			
			return entityToCliente(clienteEntity);
			
		} catch (EntityNotFoundException e) {
			
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			return null;
		}

		
		
	}
	
	
	private Date convertDate(String date){
		
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        

        try {

            Date retorno = formatter.parse(date);
            
            return retorno;

        } catch (ParseException e) {
            e.printStackTrace();
            
            return null;
        }
		
	}
	

}
