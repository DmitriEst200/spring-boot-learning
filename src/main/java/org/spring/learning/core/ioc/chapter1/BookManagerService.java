package org.spring.learning.core.ioc.chapter1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class BookManagerService{

    @Autowired
    IBookRepository bookManager;

    @Autowired
    EntityManager em;

    public void addBookRecord(Book book){

        Optional.ofNullable(book).ifPresentOrElse(
            b -> bookManager.save(b), () -> {}
        );
    }

    public void removeBook(Book book){

        Optional.ofNullable(book).ifPresentOrElse(
            b -> bookManager.delete(b), () -> {}
        );
    }

    public void removeBookByBaseCriteriaItems(Book book){

        final Map<String, Object> criteriaItem = new HashMap<>();
        //criteriaItem.put("", book.);
    }

}
