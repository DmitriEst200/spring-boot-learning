package org.spring.learning.core.ioc.chapter1;

import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

/* Usually for data storage are using type long for entity id, but
 * in our task we will not store too much records.
 * Therefore type integer for id is more appropriate
 */

@Repository
public interface IBookRepository extends CrudRepository<Book, Integer>, QuerydslPredicateExecutor<Book> {

}
