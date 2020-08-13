package com.example.gaeapp.model;

import com.google.appengine.api.datastore.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    private long id;

    private int code;

    private String name;

    private String model;

    private float price;



}
