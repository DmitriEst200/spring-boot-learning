package org.spring.learning.core.ioc.chapter1;

import com.querydsl.core.types.dsl.BooleanExpression;

public interface QueryBuilder{

    public BooleanExpression build();
}
