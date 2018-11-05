package org.spring.learning.core.ioc.chapter1;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DAO<T>{

    void add(T book);
    void delete(T book);
    List<T> getAll();
    Optional<T> getBookById(int id);
}
