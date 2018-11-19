package org.spring.learning.core.ioc;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.spring.learning.core.ioc.chapter1.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.provider.HibernateUtils;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.Attribute;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;


// It's test class for checking data saving and
// removing inside database on the persistance layer

@RunWith(SpringRunner.class)

// If database is non embedded then you must enable autoconfiguration
// avoiding its replacing

@AutoConfigureTestDatabase(replace = Replace.NONE)
@DataJpaTest
public class JPADataTests {

    @Autowired
    private IBookRepository bookManager;

    @Autowired
    private IAuthorRepository authorRepository;

    @Autowired
    private EntityManager em;

    @Test
    @Rollback(false)
    public void TestAddBookRecords(){

        bookManager.saveAll(DataTests.setData());
        //authorRepository.save(new Author("Стас", "Михайлов"));
        printAllBookRecords(5000);
    }

    @Test
    public void TestRemoveBooksByMultipleCriteria(){

        Book book = DataTests.setData().get(1);
        /*Root<Book> bookRoot = em.getCriteriaBuilder().createQuery().from(Book.class);
        Set<Attribute<? super Book, ?>> attributes = bookRoot.getModel().getAttributes().stream().
                collect(Collectors.toSet());

        for(final Attribute<?,?>a : attributes){
            System.out.println(a.getName());
        }
        */

       EntityCriteria<Book> bc = new BookEntityCriteria(em.getCriteriaBuilder());

       bc = new BookNameCriteria(bc);
       ((BookNameCriteria)bc).setName(book.getName());
       //bc = bnc;
       bc = new BookISBNCriteria(bc);
       ((BookISBNCriteria)bc).setISBN(book.getISBN());
       //bc = bic;
       bc = new BookPublishingCriteria(bc);
       ((BookPublishingCriteria )bc).setPublishing(book.getPublishingName());
       //bc = pbc;


       em.createQuery(bc.finishedCriteria()).getResultList();


    }

    private void printAllBookRecords(int pageSize){

        int entryAmount = (int)bookManager.count();
        int pageNumber = 1;
        int entryLastPos = 0;

        if(pageSize < entryAmount)
            pageSize = entryAmount;

        do{
            entryLastPos = pageNumber * pageSize;

            Session session = em.unwrap(Session.class);

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Book> cq = cb.createQuery(Book.class);
            Root<Book> criteria = cq.from(Book.class);
            cq.select(criteria).orderBy(cb.asc(criteria.get("id")));

            session.createQuery(cq)
                    .setFirstResult(entryLastPos - pageSize)
                    .setMaxResults(pageSize)
                    .getResultList()
                    .parallelStream()
                    .forEach((entry) -> {System.out.println(entry.toBookDTO().toString());});

            session.close();

            pageNumber++;
        }while(entryLastPos < entryAmount);

    }



}
