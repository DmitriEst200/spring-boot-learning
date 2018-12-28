package org.spring.learning.core.ioc.chapter1.tests;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPADeleteClause;
import org.assertj.core.util.Lists;
import org.hibernate.Session;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.spring.learning.core.ioc.chapter1.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.springframework.test.context.transaction.TestTransaction.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.*;


// It's test class for checking data saving and
// removing inside database on the persistence layer

@ExtendWith(SpringExtension.class)
@ContextConfiguration

// If database is non embedded then you must enable autoconfiguration
// avoiding its replacing

@AutoConfigureTestDatabase(replace = Replace.NONE)
@DataJpaTest
public class JPADataTest {

    @Autowired
    private IBookRepository bookManager;

    @Autowired
    private IAuthorRepository authorRepository;

    // use annotation @PersistenceContext instead of @Autowired if
    // you need to inject entity manager and/or your
    // application uses multiple connections to multiple databases
    @PersistenceContext
    private EntityManager em;

    @Test
    public void testAddAndPrintBookRecords(){

        try{
            bookManager.saveAll(DataTest.setData());
            flagForCommit();
            outputResult();
            end();
        }catch(RuntimeException ex){
            System.out.println(ex.getCause().getMessage());
        }
    }

    @Test
    public void outputResult(){
        System.out.println("Next books are:");
        Assertions.assertTimeout(Duration.ofMinutes(15), () -> {
            printAllBookRecords(5000);
        }, "Output duration is too long for an waiting.");
    }

    @ParameterizedTest(name = "book No {index}:")
    @ValueSource(ints = {0,1,5})
    public void testRemoveBooksByMultipleCriteria(int index){

       Assert.assertTrue("Index out of collection",
                index >= 0 &&
                          index <= DataTest.setData().size() - 1);

       final Book book = DataTest.setData().get(index);
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

    @ParameterizedTest(name = "book No {index}:")
    @ValueSource(ints = {0})
    public void testRemoveAuthorsFromBookRecord(int index){

        Assert.assertTrue("Index out of collection bounds",
                index >= 0 && index <= DataTest.setData().size() - 1);

        Book book = DataTest.setData().get(index);
        final BigDecimal price = book.getPrice();
        final QBook qbook = QBook.book;
        final QAuthor qauthor = QAuthor.author;
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
        Assert.assertTrue("Book not found by your criteria",
                                    rBook.isPresent());
        book = rBook.get();
        final List<String>fullNames = new ArrayList<>();

        book.getAuthors().stream().
                forEach(a ->
                    fullNames.add(a.getAuthorName()+" "+a.getAuthorLastName())
                );

        fullNames.add("Иван Мясников");

        JPADeleteClause queryDelete = new JPADeleteClause(em, qauthor).
                where(qauthor.authorName
                        .concat(" ")
                        .concat(qauthor.authorLastName).in(fullNames)
                        .and(qauthor.in(book.getAuthors()))
                );

        //System.out.println(authorRepository.findAll(Sort.by("authorLastName")));

        System.out.println(queryDelete.execute());
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
                    .forEach((entry) -> {
                        System.out.println(entry.toBookDTO().toString());
                    });
            session.close();
            pageNumber++;

        }while(entryLastPos < entryAmount);

    }

}
