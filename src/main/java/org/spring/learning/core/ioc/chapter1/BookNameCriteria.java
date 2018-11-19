package org.spring.learning.core.ioc.chapter1;

import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import java.util.List;
import java.util.Map;

public class BookNameCriteria extends CriteriaBookDecorator{

    private EntityCriteria<Book> cr;
    private String name;

    public CriteriaQuery finishedCriteria(){

        Map<String, Predicate> tempMap = getPredicateList();

        if(name != null){

            Predicate newPredicate = cr.getCriteriaBuilder().
                    equal(cr.getCriteriaRoot().get("name"), name);

            if(!tempMap.containsKey("name"))
                tempMap.put("name", newPredicate);
            else
                tempMap.replace("name", newPredicate);

            setPredicateList(tempMap);

            //System.out.println(getCriteriaRoot().toString());
        }
        //System.out.println(tempMap.size());
        return cr.finishedCriteria();
    }

    public BookNameCriteria(EntityCriteria<Book> cr){
        this.cr = cr;
        System.out.println(cr.getCriteriaRoot());
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }
}
