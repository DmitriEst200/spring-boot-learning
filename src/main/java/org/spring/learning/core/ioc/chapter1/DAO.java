package org.spring.learning.core.ioc.chapter1;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

//@Repository
public interface DAO<T, ID extends Serializable>/* extends CrudRepository<T, ID>*/{
   // void add(T item);
   // void delete(T item);
   // List<T> getAll();
   // Optional<T> getItemById(ID id);
}
