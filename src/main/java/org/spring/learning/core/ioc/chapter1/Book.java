package org.spring.learning.core.ioc.chapter1;

import org.modelmapper.ModelMapper;

import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.GenerationType;
import javax.validation.constraints.DecimalMin;
import java.math.BigDecimal;

@Entity
@Table(name = "books")
public class Book {

    //auto increment unique id of book starting from a zero

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id", updatable = false, nullable = false)
    private int id;

    @Column(name = "book_name", nullable = false)
    private String name;

    @Column(name = "author", length = 2178)
    private String author;

    //price must be greater than zero and rounded to two marks after a comma

    @DecimalMin(value = "0.0", inclusive = true, message = "price must be positive number")
    @Column(name = "price", scale = 2)
    private BigDecimal price;

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public void setAuthor(String author){
        this.author = author;
    }

    public String getAuthor(){
        return author;
    }

    public void setPrice(BigDecimal price){
        this.price = price;
    }

    public BigDecimal getPrice(){
        return price;
    }

    public BookDTO toBookDTO(){
        ModelMapper mapper = new ModelMapper();
        return mapper.map(this, BookDTO.class);
    }

    public Book(String name){ this.name = name; }
}
