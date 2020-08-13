package com.example.gaeapp.controller;


import com.example.gaeapp.model.Product;
import com.google.appengine.api.datastore.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
@RequestMapping(path = "/api/products")
public class ProductController {





    private void productToEntity(Product product, Entity entity) {


        entity.setProperty("Name", product.getName());
        entity.setProperty("Model", product.getModel());
        entity.setProperty("Price", product.getPrice());
        entity.setProperty("Code", product.getCode());

    }

    private Product entityToProduct(Entity entity) {
        Product product = new Product();

        product.setId(entity.getKey().getId());
        product.setName(entity.getProperty("Name").toString());
        product.setCode(Integer.parseInt(entity.getProperty("Code").toString()));
        product.setModel(entity.getProperty("Model").toString());
        product.setPrice(Float.parseFloat(entity.getProperty("Price").toString()));

        return product;


    }

    @GetMapping("/{code}")
    public ResponseEntity<Product> getProduct(@PathVariable int code) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        Query.Filter codeFilter = new Query.FilterPredicate("Code", Query.FilterOperator.EQUAL, code );

        Query query = new Query("Products").setFilter(codeFilter);

        Entity entity = datastore.prepare(query).asSingleEntity();

        if (entity != null) {
            Product product = entityToProduct(entity);
            return ResponseEntity.ok(product);
        }
        else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<?> getProducts() {

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        Query query = new Query("Products")
                  .addSort("Code", Query.SortDirection.ASCENDING);

        List<Entity> entities = datastore
                .prepare(query)
                .asList(FetchOptions.Builder.withDefaults());

        List<Product> products = entities
                .stream()
                .map((entity) -> entityToProduct(entity))
                .collect(Collectors.toList());

       /*List<Product> products = IntStream.range(0, 5)
               .mapToObj((i) -> createProduct(i+1))
               .collect(Collectors.toList());*/

        return ResponseEntity.ok(products);

    }

    @PostMapping
    public ResponseEntity<Product> saveProduct(@RequestBody Product product) {

        DatastoreService dataStore = DatastoreServiceFactory.getDatastoreService();

        String kind = "Products";

        Key productKey = KeyFactory.createKey(kind, "productKey");

        Entity entity = new Entity(kind, productKey);

        this.productToEntity(product, entity);

        dataStore.put(entity);

        return new ResponseEntity<>(product, HttpStatus.CREATED);
    }

    @PutMapping(path="/{code}")
    public ResponseEntity<Product> updateProduct(
            @RequestBody Product product,
            @PathVariable("code") int code) {

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        Query.Filter codeFilter = new Query.FilterPredicate("Code",Query.FilterOperator.EQUAL,code);

        Query query = new Query("Products").setFilter(codeFilter);

        Entity entity = datastore.prepare(query).asSingleEntity();

        if (entity != null) {
            productToEntity(product,entity);

            datastore.put(entity);

            product.setId(entity.getKey().getId());

            return ResponseEntity.ok(product);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @DeleteMapping("/{code}")
    public ResponseEntity<Product> deleteProduct(@PathVariable("code") int code) {

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        Query.FilterPredicate codeFilter = new Query.FilterPredicate("Code", Query.FilterOperator.EQUAL, code);

        Query query = new Query("Products").setFilter(codeFilter);

        Entity entity = datastore.prepare(query).asSingleEntity();
        
        if (entity != null) {
            datastore.delete(entity.getKey());

            Product product = entityToProduct(entity);
            return ResponseEntity.ok(product);
        } else {
            
            return ResponseEntity.notFound().build();
            
        }
    }


}
