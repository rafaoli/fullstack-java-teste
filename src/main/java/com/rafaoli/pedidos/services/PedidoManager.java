package com.rafaoli.pedidos.services;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
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
import com.rafaoli.pedidos.models.ItemPedido;
import com.rafaoli.pedidos.models.Pedido;
import com.rafaoli.pedidos.models.Product;

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
	
	@PUT	
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/{code}")
	public Pedido alterPedido(@PathParam("code") int code, Pedido pedido) {

		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();

		Filter codeFilter = new FilterPredicate("Numero", FilterOperator.EQUAL,
				code);

		Query query = new Query("Pedidos").setFilter(codeFilter);

		Entity pedidoEntity = datastore.prepare(query).asSingleEntity();

		if (pedidoEntity != null) {

			pedidoToEntity(pedido, pedidoEntity);

			datastore.put(pedidoEntity);
			
			pedido.setId(pedidoEntity.getKey().getId());

			return pedido;

		} else {
			throw new WebApplicationException(Status.NOT_FOUND);
		}
	}
	
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)	
	@Path("/{code}")
	public void deletePedido(@PathParam("code") int code) {

		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();

		Filter codeFilter = new FilterPredicate("Numero", FilterOperator.EQUAL,
				code);

		Query query = new Query("Pedidos").setFilter(codeFilter);

		Entity pedidoEntity = datastore.prepare(query).asSingleEntity();

		if (pedidoEntity != null) {
			datastore.delete(pedidoEntity.getKey());
			
		} else {
			throw new WebApplicationException(Status.NOT_FOUND);
		}
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{codigo}")
	public Pedido getPedido(@PathParam("codigo") long codigo) {

		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();

		Filter codeFilter = new FilterPredicate("Numero", FilterOperator.EQUAL,
				codigo);

		Query query;

		query = new Query("Pedidos").setFilter(codeFilter);

		Entity pedidoEntity = datastore.prepare(query).asSingleEntity();

		if (pedidoEntity != null) {
			Pedido pedido = entityToPedido(pedidoEntity);

			return pedido;

		} else {
			throw new WebApplicationException(Status.NOT_FOUND);

		}

	}
	
	
	
	

	private void pedidoToEntity(Pedido pedido, Entity pedidoEntity) {

		pedidoEntity.setProperty("Numero", pedido.getNumero());
		
		String data = new SimpleDateFormat("dd/MM/yyyy").format(pedido.getDataEmissao());
		
		pedidoEntity.setProperty("DataEmissao", data);
		
		
		pedidoEntity.setProperty("Cliente", pedido.getCliente().getCodigo());
				
		List<EmbeddedEntity> prods = new ArrayList<EmbeddedEntity>();

		
		for (ItemPedido item : pedido.getProdutos()) {
			
			EmbeddedEntity em = new EmbeddedEntity();
			
			em.setProperty("ProdCod", item.getProduto().getCodigo());
			em.setProperty("ProdQnt", item.getQuantidade());
			em.setProperty("ProdUnit", item.getVlrUnit());
			
			prods.add(em);			
			
		}
		
		pedidoEntity.setProperty("Produtos", prods);
		
	
		pedidoEntity.setProperty("VlrTotal", pedido.getVlrTotal());

	}

	

	private Pedido entityToPedido(Entity pedidoEntity) {

		Pedido pedido = new Pedido();
		pedido.setId(pedidoEntity.getKey().getId());
		
		pedido.setNumero(Integer.parseInt(pedidoEntity.getProperty("Numero").toString()));
		pedido.setDataEmissao(convertDate(pedidoEntity.getProperty("DataEmissao").toString()));
		
		pedido.setCliente(getCliente( Integer.parseInt(pedidoEntity.getProperty("Cliente").toString())));
		
		pedido.setVlrTotal(Float.parseFloat(pedidoEntity.getProperty("VlrTotal").toString()));
		
		
		@SuppressWarnings("unchecked") // Cast can't verify generic type.
		ArrayList<EmbeddedEntity> prods = (ArrayList<EmbeddedEntity>) pedidoEntity.getProperty("Produtos");
		
		
		List<ItemPedido> itens = new ArrayList<ItemPedido>();
		
		for (EmbeddedEntity item : prods) {
			
			ItemPedido ip = new ItemPedido();
			
			ip.setProduto(getProduct(Integer.parseInt(item.getProperty("ProdCod").toString())));
			ip.setQuantidade(Float.parseFloat(item.getProperty("ProdQnt").toString()));
			ip.setVlrUnit((Float.parseFloat(item.getProperty("ProdUnit").toString())));
			
			itens.add(ip);
			
			
			
		}
		
		pedido.setProdutos(itens);
		
		
		

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
	
	private Product entityToProduct(Entity productEntity) {

		Product product = new Product();
		product.setId(productEntity.getKey().getId());
		
		product.setDescricao((String) productEntity.getProperty("Descricao"));
		product.setCodigo(Integer.parseInt(productEntity.getProperty("Codigo").toString()));
		
		product.setValor(Float.parseFloat(productEntity.getProperty("Valor").toString()));
		
		
		return product;

	}
	
	private Cliente getCliente(long id){
		
		
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();

		
		Filter codeFilter = new FilterPredicate("Codigo", FilterOperator.EQUAL, id);

		Query query;

		query = new Query("Clientes").setFilter(codeFilter);

		Entity clienteEntity = datastore.prepare(query).asSingleEntity();

		if (clienteEntity != null) {
			Cliente cliente = entityToCliente(clienteEntity);

			return cliente;

		} else {
			
			return null;

		}

		
		
	}
	
	
	private Product getProduct(long id){
		
		
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();

		
		Filter codeFilter = new FilterPredicate("Codigo", FilterOperator.EQUAL, id);

		Query query;

		query = new Query("Products").setFilter(codeFilter);

		Entity produtoEntity = datastore.prepare(query).asSingleEntity();

		if (produtoEntity != null) {
			Product produto = entityToProduct(produtoEntity);

			return produto;

		} else {
			
			return null;

		}

		
		
	}
	
	private Date convertDate(String date){
		
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        

        try {

            Date retorno = formatter.parse(date);
            
            return retorno;

        } catch (ParseException e) {
            e.printStackTrace();
            
            return null;
        }
		
	}
	

}
