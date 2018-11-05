package org.spring.learning.core.ioc.chapter1;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookRepository implements DAO<Book> {

    private List<Book> books;

    public void add(Book b){

        //instead of nested null checking
        Optional.ofNullable(b).ifPresent(
             val -> { if(getBookById(val.getId()).isEmpty())
                        books.add(val); });
    }

    public List<Book> getAll(){
        return books;
    }

    public void delete(Book b){
        books.remove(b);
    }

    public Optional<Book> getBookById(int id){
        return Optional.ofNullable(
                books.stream().filter( b -> b.getId() == id)
                        .findFirst().
                         orElse(null));
    }

    public BookRepository(){
        books = new ArrayList();
    }
}
