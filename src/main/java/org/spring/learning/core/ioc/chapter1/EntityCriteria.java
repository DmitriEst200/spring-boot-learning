package org.spring.learning.core.ioc.chapter1;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public abstract class EntityCriteria<T>{

    private Root<T> criteriaRoot;
    private CriteriaQuery criteriaQuery;
    private CriteriaBuilder cb;
    private Map<String, Predicate> predicates = new HashMap();

    public abstract CriteriaQuery finishedCriteria();
    //public abstract CriteriaQuery EqualCriteria();

    protected void setCriteriaQuery(CriteriaQuery cq){
        criteriaQuery = cq;
    }

    public CriteriaQuery getCriteriaQuery(){
        return criteriaQuery;
    }

    protected CriteriaBuilder getCriteriaBuilder(){
        return cb;
    }

    protected void setCriteriaBuilder(CriteriaBuilder cb){
        this.cb = cb;
    }

    protected void setCriteriaRoot(Root cr){
        this.criteriaRoot = cr;
    }

    protected Root<T> getCriteriaRoot(){
        return criteriaRoot;
    }

    public Map<String, Predicate> getPredicateList(){
        return predicates;
    }

    public void setPredicateList(Map<String, Predicate> predicates) {
        this.predicates = predicates;
    }
}
