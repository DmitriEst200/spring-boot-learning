package org.spring.learning.core.ioc.chapter1;

import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import java.util.Map;

public class BookPublishingCriteria extends CriteriaBookDecorator{

    private EntityCriteria<Book> cr;
    private String publishing;

    public CriteriaQuery finishedCriteria(){

        Map<String, Predicate> tempMap = getPredicateList();

        if(publishing != null){


            Predicate newPredicate = cr.getCriteriaBuilder().
                    equal(cr.getCriteriaRoot().get("publishingName"), publishing);

            if(!tempMap.containsKey("publishingName"))
                tempMap.put("publishingName", newPredicate);
            else
                tempMap.replace("publishingName", newPredicate);

            setPredicateList(tempMap);
        }
       // System.out.println(tempMap.size());
        return cr.finishedCriteria();
    }

    public BookPublishingCriteria(EntityCriteria<Book> cr){
        this.cr = cr;
        System.out.println(cr.getCriteriaRoot());
    }

    public void setPublishing(String publishing){
        this.publishing = publishing;
    }

    public String getPublishing(){
        return publishing;
    }
}
