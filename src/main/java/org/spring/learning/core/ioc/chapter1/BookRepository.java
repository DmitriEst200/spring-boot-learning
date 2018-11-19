package org.spring.learning.core.ioc.chapter1;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookRepository /*implements DAO<Book, Integer>*/ {

    //@PersistenceContext
    private EntityManager em;



    private List<Book> books;

    /*public Book save(Book b){

        //instead of nested null checking
        Optional.ofNullable(b).ifPresentOrElse(
             val -> {
                 if(getItemById(val.getId()).isEmpty()){
                        //books.add(val);
                     em.persist(val);
                     em.close();
                 }
                 } ,()->{System.out.println("failed");
             });
    }*/

    public List<Book> getAll(){
        Query q = em.createNamedQuery("SELECT b FROM Book b");
        //books = q.getResultList();
        return q.getResultList();
    }

    public void delete(Book b){
        Optional.ofNullable(b).ifPresent( val -> {em.remove(b);});

        //books.remove(b);
    }

    public Optional<Book> getItemById(Integer id){
        return Optional.ofNullable(em.find(Book.class, id));
    }

    public BookRepository(){
        //books = new ArrayList();
    }
}
