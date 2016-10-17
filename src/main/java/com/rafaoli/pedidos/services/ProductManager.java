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
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.rafaoli.pedidos.models.Cliente;
import com.rafaoli.pedidos.models.Product;

@Path("/products")
public class ProductManager {

	/*
	 * @GET
	 * 
	 * @Produces(MediaType.APPLICATION_JSON)
	 * 
	 * @Path("/{code}") public Product getProduct(@PathParam("code") int code){
	 * 
	 * return
	 * 
	 * 
	 * }
	 */

	private void productToEntity(Product product, Entity productEntity) {

		
		productEntity.setProperty("Descricao", product.getDescricao());
		productEntity.setProperty("Codigo", product.getCodigo());
		productEntity.setProperty("Valor", product.getValor());

	}

	private Product entityToProduct(Entity productEntity) {

		Product product = new Product();
		product.setId(productEntity.getKey().getId());
		
		product.setDescricao((String) productEntity.getProperty("Descricao"));
		product.setCodigo(Integer.parseInt(productEntity.getProperty("Codigo").toString()));
		
		product.setValor(Float.parseFloat(productEntity.getProperty("Valor").toString()));
		
		
		return product;

	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Product save(Product product) {

		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();

		Key produKey = KeyFactory.createKey("Products", "productKey");

		Entity productEntity = new Entity("Products", produKey);

		productToEntity(product, productEntity);

		datastore.put(productEntity);

		product.setId(productEntity.getKey().getId());

		return product;

	}
	
	@PUT	
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/{code}")
	public Product alter(@PathParam("code") int code, Product product) {

		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();

		Filter codeFilter = new FilterPredicate("Codigo", FilterOperator.EQUAL,
				code);

		Query query = new Query("Products").setFilter(codeFilter);

		Entity productEntity = datastore.prepare(query).asSingleEntity();

		if (productEntity != null) {

			productToEntity(product, productEntity);

			datastore.put(productEntity);
			
			product.setId(productEntity.getKey().getId());

			return product;

		} else {
			throw new WebApplicationException(Status.NOT_FOUND);
		}
	}
	
	
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)	
	@Path("/{code}")
	public void delete(@PathParam("code") int code) {

		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();

		Filter codeFilter = new FilterPredicate("Codigo", FilterOperator.EQUAL,
				code);

		Query query = new Query("Products").setFilter(codeFilter);

		Entity productEntity = datastore.prepare(query).asSingleEntity();

		if (productEntity != null) {
			datastore.delete(productEntity.getKey());
			
		} else {
			throw new WebApplicationException(Status.NOT_FOUND);
		}
	}

	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Product> getProducts() {

		List<Product> products = new ArrayList<>();

		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();

		Query query;

		query = new Query("Products").addSort("Codigo", SortDirection.ASCENDING);
		
		
		List<Entity> productsEntities = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
		
		
		for (Entity productEntity : productsEntities) {

			Product product = entityToProduct(productEntity);
			products.add(product);
		}

		return products;

	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/listSelect2")
	public List<Product> getProductsSelect2(@QueryParam("term") String term) {

		List<Product> produtos = new ArrayList<>();

		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();

		Query query;

		
		// não implementei o filtro por nome
		
		query = new Query("Products").addSort("Codigo", SortDirection.ASCENDING);

		List<Entity> produtosEntities = datastore.prepare(query).asList(
				FetchOptions.Builder.withDefaults());

		for (Entity produtoEntity : produtosEntities) {

			Product produto = entityToProduct(produtoEntity);
			produtos.add(produto);
		}

		return produtos;

	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{codigo}")
	public Product getProducts(@PathParam("codigo") long codigo) {

		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		
		
	
		
		Filter codeFilter =	new	FilterPredicate("Codigo", FilterOperator.EQUAL, codigo);

		Query query;

		query = new Query("Products").setFilter(codeFilter);
		
		Entity productEntity = datastore.prepare(query).asSingleEntity();
		
		
		if(productEntity != null){
			Product product = entityToProduct(productEntity);
			
			return product;
			
		}
		else{
			throw new WebApplicationException(Status.NOT_FOUND);
			
		}
		
	

	}
	
	

}
