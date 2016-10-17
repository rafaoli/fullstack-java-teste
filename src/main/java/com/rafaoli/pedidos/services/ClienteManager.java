package com.rafaoli.pedidos.services;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.rafaoli.pedidos.models.Cliente;

@Path("/clientes")
public class ClienteManager {

	private void clienteToEntity(Cliente cliente, Entity clienteEntity) {

		clienteEntity.setProperty("Codigo", cliente.getCodigo());
		clienteEntity.setProperty("Nome", cliente.getNome());		
		clienteEntity.setProperty("Telefone", cliente.getTelefone());
		clienteEntity.setProperty("Cpf", cliente.getCpf());
		clienteEntity.setProperty("Email", cliente.getEmail());
		clienteEntity.setProperty("Tipo", cliente.getTipo());
		clienteEntity.setProperty("Sexo", cliente.getSexo());
		

	}

	private Cliente entityToCliente(Entity clienteEntity) {

		Cliente cliente = new Cliente();
		cliente.setId(clienteEntity.getKey().getId());
		
		cliente.setCodigo(Integer.parseInt(clienteEntity.getProperty("Codigo").toString()));
		cliente.setNome((String) clienteEntity.getProperty("Nome"));		
		cliente.setTelefone((String) clienteEntity.getProperty("Telefone"));	
		cliente.setCpf((String) clienteEntity.getProperty("Cpf"));		
		cliente.setEmail((String) clienteEntity.getProperty("Email"));
		
		cliente.setTipo((String) clienteEntity.getProperty("Tipo"));
		cliente.setSexo((String) clienteEntity.getProperty("Sexo"));
		
		return cliente;

	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Cliente saveCliente(Cliente cliente) {

		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();

		Key produKey = KeyFactory.createKey("Clientes", "clienteKey");
		
		Entity clienteEntity = new Entity("Clientes", produKey);

		clienteToEntity(cliente, clienteEntity);

		datastore.put(clienteEntity);
		
		cliente.setId(clienteEntity.getKey().getId());

		return cliente;

	}
	
	@PUT	
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/{code}")
	public Cliente alterCliente(@PathParam("code") int code, Cliente cliente) {

		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();

		Filter codeFilter = new FilterPredicate("Codigo", FilterOperator.EQUAL,
				code);

		Query query = new Query("Clientes").setFilter(codeFilter);

		Entity clienteEntity = datastore.prepare(query).asSingleEntity();

		if (clienteEntity != null) {

			clienteToEntity(cliente, clienteEntity);

			datastore.put(clienteEntity);
			
			cliente.setId(clienteEntity.getKey().getId());

			return cliente;

		} else {
			throw new WebApplicationException(Status.NOT_FOUND);
		}
	}
	
	
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)	
	@Path("/{code}")
	public void deleteCliente(@PathParam("code") int code) {

		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();

		Filter codeFilter = new FilterPredicate("Codigo", FilterOperator.EQUAL,
				code);

		Query query = new Query("Clientes").setFilter(codeFilter);

		Entity clienteEntity = datastore.prepare(query).asSingleEntity();

		if (clienteEntity != null) {
			datastore.delete(clienteEntity.getKey());
			
		} else {
			throw new WebApplicationException(Status.NOT_FOUND);
		}
	}
	
	

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Cliente> getClientes() {

		List<Cliente> clientes = new ArrayList<>();

		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();

		Query query;

		query = new Query("Clientes").addSort("Codigo", SortDirection.ASCENDING);

		List<Entity> clientesEntities = datastore.prepare(query).asList(
				FetchOptions.Builder.withDefaults());

		for (Entity clienteEntity : clientesEntities) {

			Cliente cliente = entityToCliente(clienteEntity);
			clientes.add(cliente);
		}

		return clientes;

	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/listSelect2")
	public List<Cliente> getClientesSelect2(@QueryParam("term") String term) {

		
		//System.out.println(term);
		
		List<Cliente> clientes = new ArrayList<>();

		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();

		Query query;

		
		// não implementei o filtro por nome
		
		query = new Query("Clientes").addSort("Codigo", SortDirection.ASCENDING);

		List<Entity> clientesEntities = datastore.prepare(query).asList(
				FetchOptions.Builder.withDefaults());

		for (Entity clienteEntity : clientesEntities) {

			Cliente cliente = entityToCliente(clienteEntity);
			clientes.add(cliente);
		}

		return clientes;

	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{codigo}")
	public Cliente getCliente(@PathParam("codigo") long codigo) {

		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();

		Filter codeFilter = new FilterPredicate("Codigo", FilterOperator.EQUAL,
				codigo);

		Query query;

		query = new Query("Clientes").setFilter(codeFilter);

		Entity clienteEntity = datastore.prepare(query).asSingleEntity();

		if (clienteEntity != null) {
			Cliente cliente = entityToCliente(clienteEntity);

			return cliente;

		} else {
			throw new WebApplicationException(Status.NOT_FOUND);

		}

	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/teste")
	public Cliente get() {
		
		Cliente teste = new Cliente();
		
		teste.setEmail("ksaoksoakoskaoksao");
		
		return teste;
		
		
	}
	
	

}
