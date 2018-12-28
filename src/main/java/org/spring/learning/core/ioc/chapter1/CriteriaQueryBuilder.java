package org.spring.learning.core.ioc.chapter1;

import com.querydsl.core.support.CollectionAnyVisitor;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Visitor;
import com.querydsl.core.types.dsl.*;
import com.querydsl.core.types.Expression;

import org.springframework.cglib.core.internal.Function;

import java.util.Collection;


public class CriteriaQueryBuilder implements QueryBuilder{

    private BooleanExpression criteriaPredicate;

    public <T> CriteriaQueryBuilder notNullAnd(
            Function<T, BooleanExpression> criteriaExpression, T value){

        if(value != null){

            return new CriteriaQueryBuilder(
                    criteriaPredicate.and(criteriaExpression.apply(value)
                    ));
        }

        return this;
    }



    public CriteriaQueryBuilder notEmptyAnd(
            Function<String, BooleanExpression> criteriaExpression,
            String value){

        if(!value.isEmpty()){
            return new CriteriaQueryBuilder(
                    criteriaPredicate.
                    and(criteriaExpression.apply(value)));
        }

        return this;
    }

    public <T> CriteriaQueryBuilder notEmptyAnd(
            Function<Collection<T>, BooleanExpression> criteriaExpression,
            Collection<T> collection){

        if(!collection.isEmpty())
            return new CriteriaQueryBuilder(
                    criteriaPredicate.
                    and(criteriaExpression.apply(collection)));

        return this;
    }

    public <T> CriteriaQueryBuilder eqOrNullAnd(
            ComparableExpressionBase criteria, T expectedValue){

        final BooleanExpression expr = (expectedValue != null)
                ? criteria.eq(expectedValue)
                : criteria.isNull();

        return new CriteriaQueryBuilder(criteriaPredicate.and(expr));
    }

    public BooleanExpression build(){
        return criteriaPredicate;
    }

    public CriteriaQueryBuilder(
            BooleanExpression criteriaExpression){
        this.criteriaPredicate = criteriaExpression;
    }


}
