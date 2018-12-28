package org.spring.learning.core.ioc.chapter1;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;
import java.io.Serializable;

@NoRepositoryBean
public interface DAO<T, ID extends Serializable> extends CrudRepository<T, ID>{

    Iterable<T> findAll(Sort sort);
    Page<T> findAll(Pageable pageable);
}
