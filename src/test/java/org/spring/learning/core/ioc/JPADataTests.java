package org.spring.learning.core.ioc;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.hibernate.HibernateDeleteClause;
import com.querydsl.jpa.impl.JPADeleteClause;
import org.assertj.core.util.Lists;
import org.hibernate.Session;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.runner.RunWith;
import org.spring.learning.core.ioc.chapter1.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.math.RoundingMode;
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

    @org.junit.Test(timeout = 120000)
    @Rollback(false)
    public void testAddBookRecords(){

        bookManager.saveAll(DataTests.setData());
        printAllBookRecords(5000);
    }

    @ParameterizedTest(name = "book No {index}:")/*timeout = 15000)*/
    @ValueSource(ints = {0,1,5})
    public void testRemoveBooksByMultipleCriteria(int index){

       Assert.assertTrue("Index out of collection",
                index >= 0 && index <= DataTests.setData().size() - 1);

       final Book book = DataTests.setData().get(index);
       final QBook qbook = QBook.book;
       final BigDecimal price = book.getPrice();
       final CriteriaQueryBuilder rmQueryBuilder =
               new CriteriaQueryBuilder(
                       qbook.isNotNull()).
                       notNullAnd(qbook.isbn::eq, book.getISBN()).
                       notNullAnd(qbook.name::eq, book.getName()).
                       notNullAnd(qbook.price::eq,
                                 (price != null)
                                         ? price.setScale(2, RoundingMode.HALF_EVEN)
                                         : price
                       ).
                       notNullAnd(qbook.publishingName::eq, book.getPublishingName());

       final List qResult = Lists.newArrayList(
               bookManager.findAll(rmQueryBuilder.build()).iterator());

       Assert.assertTrue("Nothing found", qResult.size() >= 1);
       bookManager.deleteAll(qResult);
    }

    @ParameterizedTest
    @ValueSource(ints = {0})
    public void testRemoveAuthorsFromBookRecord(int index){

        Assert.assertTrue("Index out of collection",
                index >= 0 && index <= DataTests.setData().size() - 1);

        final Book book = DataTests.setData().get(index);
        final List<Author>authors = book.
                getAuthors().
                stream().
                collect(Collectors.toList());

        authors.add(new Author("Иван", "Мясников"));

        final BigDecimal price = book.getPrice();
        final QBook qbook = QBook.book;
        final BooleanExpression findQuery =
                new CriteriaQueryBuilder(qbook.isNotNull()).
                        eqOrNullAnd(qbook.isbn, book.getISBN()).
                        eqOrNullAnd(qbook.name, book.getName()).
                        eqOrNullAnd(qbook.price,
                                (price != null)
                                    ? price.setScale(2, RoundingMode.HALF_EVEN)
                                    : price
                        ).
                        eqOrNullAnd(qbook.publishingName, book.getPublishingName()).
                        build();

        final Optional<Book> rBook = bookManager.findOne(findQuery);

        Assert.assertTrue("Book not found by your criteria", rBook.isPresent());

        final QAuthor qauthor = QAuthor.author;


        JPADeleteClause deleteQuery =
                new JPADeleteClause(em, qauthor).
                    where(qauthor.in(authors));

         //Assertions.assertThrows();
         //new HibernateDeleteClause()

        System.out.println(deleteQuery.toString());
        deleteQuery.execute();
    }

    @Test
    @DisplayName("Test if hql phrase not contain errors")
    public void checkHQLCompiling(){

    }

    private void printAllBookRecords(int pageSize){

        int entryAmount = (int)bookManager.count();
        int pageNumber = 1;
        int entryLastPos = 0;

        if(pageSize < entryAmount)
            pageSize = entryAmount;

        do{
            entryLastPos = pageNumber * pageSize;

            final Session session = em.unwrap(Session.class);
            final CriteriaBuilder cb = em.getCriteriaBuilder();
            final CriteriaQuery<Book> cq = cb.createQuery(Book.class);
            final Root<Book> criteria = cq.from(Book.class);

            cq.select(criteria).orderBy(cb.asc(criteria.get("id")));

            session.createQuery(cq)
                    .setFirstResult(entryLastPos - pageSize)
                    .setMaxResults(pageSize)
                    .getResultList()
                    .parallelStream()
                    .forEach((entry) -> {
                        System.out.println(entry.toBookDTO().toString());
                    });

            session.close();

            pageNumber++;
        }while(entryLastPos < entryAmount);

    }
}
