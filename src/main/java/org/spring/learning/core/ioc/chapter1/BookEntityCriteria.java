package org.spring.learning.core.ioc.chapter1;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import java.util.Arrays;

public class BookEntityCriteria extends EntityCriteria<Book>{


    public CriteriaQuery finishedCriteria(){

        Object[]tempArr = getPredicateList().values().toArray();
        return getCriteriaQuery().
                where(Arrays.copyOfRange(tempArr, 0, tempArr.length, Predicate[].class));
    }

    public BookEntityCriteria(CriteriaBuilder cb){

        if(cb == null)
            throw new IllegalArgumentException("CriteriaBuilder must not be null !!!");

        //if(cb != getCriteriaBuilder()) {
            System.out.println("Ok");
            this.setCriteriaBuilder(cb);
            this.setCriteriaQuery(cb.createQuery());
            this.setCriteriaRoot(getCriteriaQuery().from(Book.class));
        //}

    }
}
