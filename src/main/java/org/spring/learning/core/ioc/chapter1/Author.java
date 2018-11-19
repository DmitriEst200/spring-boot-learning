package org.spring.learning.core.ioc.chapter1;


import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "authors")
public class Author{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "author_id")
    private int id;

    @Column(name = "firstname", length = 2178)
    private String authorName;

    @Column(name = "lastname", length = 2178)
    private String authorLastName;

    @ManyToMany(mappedBy = "authorList")
    private Set<Book> books = new HashSet<Book>();

    /*public void setId(int id){
        this.id = id;
    }*/

    public int getId(){
        return id;
    }

    public void setAuthorName(String authorName){
        this.authorName = authorName;
    }

    public String getAuthorName(){
        return authorName;
    }

    public void setAuthorLastName(String authorLastName){
        this.authorLastName = authorLastName;
    }

    public String getAuthorLastName(){
        return authorLastName;
    }

    public void setBooks(Set<Book> books){
        this.books = books;
    }

    public Set<Book> getBooks(){
        return books;
    }

    public Author(){}

    public Author(String authorName, String authorLastName){
        this.authorName = authorName;
        this.authorLastName = authorLastName;
    }

}
