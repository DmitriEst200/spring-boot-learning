package org.spring.learning.core.ioc.chapter1;

import org.modelmapper.ModelMapper;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "books")
public class Book {

    //auto increment unique id of book starting from a zero

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id", updatable = false, nullable = false)
    private int id;

    @Column(name = "book_name", nullable = false, length = 4805)
    private String name;

    @Pattern(regexp = "(?:ISBN(?:-1[03])?:? )?(?=[0-9X]{10}$|" +
            "(?=(?:[0-9]+[- ]){3})[- 0-9X]{13}$|" +
            "97[89][0-9]{10}$|" +
            "(?=(?:[0-9]+[- ]){4})[- 0-9]{17}$)(?:97[89][- ]?)?" +
            "[0-9]{1,5}[- ]?[0-9]+[- ]?[0-9]+[- ]?[0-9X]$",
            message = "Invalid ISBN code")
    @Column(name = "ISBN", unique = true)
    private String isbn;

    @Column(name = "publishing_house")
    private String publishingName;

    //price must be greater than zero and rounded to two marks after a comma

    @DecimalMin(value = "0.0", message = "price must be positive number")
    @Column(name = "price", scale = 2)
    private BigDecimal price;

    @ManyToMany(cascade = { CascadeType.ALL })
    @JoinTable(
        name = "book_author",
        joinColumns = { @JoinColumn( name = "book_id" ) },
        inverseJoinColumns = { @JoinColumn( name = "author_id" )}
    )
    //Set collection is widely used for avoiding of the redundant data duplication
    private Set<Author> authorList = new HashSet();

    public void setPublishingName(String publishingName){
        this.publishingName = publishingName;
    }

    public String getPublishingName(){
        return publishingName;
    }

    public void setISBN(String isbn){
        this.isbn = isbn;
    }

    public String getISBN(){
        return this.isbn;
    }

    public int getId(){
        return id;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public void setPrice(BigDecimal price){
        this.price = price;
    }

    public BigDecimal getPrice(){
        return price;
    }

    public Set<Author> getAuthors(){
        return authorList;
    }

    public void setAuthors(Set<Author> authorList){
        this.authorList = authorList;
    }

    public BookDTO toBookDTO(){
        ModelMapper mapper = new ModelMapper();
        return mapper.map(this, BookDTO.class);
    }

    public Book(){}

    public Book(String name){
        this.name = name;
    }
}
