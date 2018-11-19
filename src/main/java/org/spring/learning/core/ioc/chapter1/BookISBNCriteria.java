package org.spring.learning.core.ioc.chapter1;

import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import java.util.Map;

public class BookISBNCriteria extends CriteriaBookDecorator{

    private EntityCriteria<Book>cr;
    private String isbn;

    public CriteriaQuery finishedCriteria(){

        Map<String, Predicate> tempMap = getPredicateList();

        if(isbn != null){

            Predicate newPredicate = cr.getCriteriaBuilder().
                    equal(cr.getCriteriaRoot().get("isbn"), isbn);

            if(!tempMap.containsKey("isbn"))
                tempMap.put("isbn", newPredicate);
            else
                tempMap.replace("isbn", newPredicate);

            setPredicateList(tempMap);
        }

        return this.cr.finishedCriteria();
    }

    public BookISBNCriteria(EntityCriteria<Book> cr){
        this.cr = cr;
        System.out.println(cr.getCriteriaRoot());
    }

    public void setISBN(String isbn){
        this.isbn = isbn;
    }

    public String getISBN(){
        return isbn;
    }

}
