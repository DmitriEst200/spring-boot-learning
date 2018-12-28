package org.spring.learning.core.ioc.chapter1;

import org.springframework.stereotype.Repository;

@Repository
public interface IAuthorRepository extends DAO<Author, Integer>{
}
