package org.spring.learning.core.ioc;

import com.querydsl.core.support.QueryBase;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPAQueryBase;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.hibernate.HibernateDeleteClause;
import com.querydsl.jpa.impl.JPADeleteClause;
import com.querydsl.jpa.impl.JPAQuery;
import org.assertj.core.util.Lists;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.hibernate.transform.ResultTransformer;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
//import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.spring.learning.core.ioc.chapter1.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;


// It's test class for checking data saving and
// removing inside database on the persistence layer

@ExtendWith(SpringExtension.class)
@ContextConfiguration
//@RunWith(Parameterized.class)

// If database is non embedded then you must enable autoconfiguration
// avoiding its replacing

@AutoConfigureTestDatabase(replace = Replace.NONE)
///@SpringBootTest
@DataJpaTest
public class JPADataTests {

   /* @ClassRule
    public static final SpringClassRule springClassRule = new SpringClassRule();

    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();
*/
    @Autowired
    private IBookRepository bookManager;

    @Autowired
    private IAuthorRepository authorRepository;

    @Autowired
    private EntityManager em;

    @Test(timeout = 120000)
    @Rollback(false)
    public void testAddBookRecords(){

        bookManager.saveAll(DataTests.setData());
        printAllBookRecords(5000);
    }

    @ParameterizedTest(name = "book No {index}:")/*timeout = 15000)*/
    @ValueSource(ints = {0,1,5})
    public void testRemoveBooksByMultipleCriteria(int index){

       Assert.assertTrue("Index out of collection",
                index >= 0 &&
                          index <= DataTests.setData().size() - 1);

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
   // @Rollback(false)
    public void testRemoveAuthorsFromBookRecord(int index){

        Assert.assertTrue("Index out of collection bounds",
                index >= 0 && index <= DataTests.setData().size() - 1);

        final Book book = DataTests.setData().get(index);


        final BigDecimal price = book.getPrice();
        QBook qbook = QBook.book;
        final QAuthor qauthor = new QAuthor("sa");
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

        qbook = new QBook("sb");

        final List<String>fullNames = new ArrayList<>();

        rBook.get().
                getAuthors().
                stream().
                forEach(a ->
                    fullNames.add(a.getAuthorName()+" "+a.getAuthorLastName())
                );

        fullNames.add("Иван Мясников");



      /*   List<Author>result = new JPAQuery<Author>(em)
                 .from(qauthor)
                .innerJoin(qauthor.books, qbook)
                .on(qbook.id.eq(rBook.get().getId()))
                .where(qauthor.authorName
                        .concat(" ")
                        .concat(qauthor.authorLastName)
                        .in(fullNames)
                ).fetch();
*/
        /*JPADeleteClause deleteQuery =
                new JPADeleteClause(em, qauthor).
                    where(qauthor.in(authors));
*/
        //System.out.println(deleteQuery.execute());

        final String hql = "DELETE FROM Author a WHERE a IN( SELECT sa FROM Author sa " +
                            "INNER JOIN sa.books as sb "+
                            "WHERE sb.id = :book_id " +
                            "AND CONCAT(sa.authorName, ' ', sa.authorLastName ) IN ( :fullNames ) )";
        final Session session = em.unwrap(Session.class);
        Assert.assertNotNull(session);
        final Query query = session.createQuery(hql)
              .setParameter("book_id", rBook.get().getId())
              .setParameterList("fullNames", fullNames);

       // List d = query.getResultList();
        //authorRepository.deleteAll(result);

        System.out.println(query.executeUpdate());
        /*session.close();*/
        //deleteQuery.execute();
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
