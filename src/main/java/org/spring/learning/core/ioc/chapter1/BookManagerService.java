package org.spring.learning.core.ioc.chapter1;

import com.google.common.collect.Lists;
import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Visitor;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPADeleteClause;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;


import javax.annotation.Nullable;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Consumer;

@Service
public class BookManagerService{

    private final IBookRepository bookManager;
    private final IAuthorRepository authorManager;
    private final EntityManager entityManager;

    private Logger logger = LogManager.getLogger(BookManagerService.class);

    public void addBook(Book book){

        Optional.ofNullable(book).ifPresentOrElse(
            b ->{
                performTransaction((em) -> bookManager.save(b));
                System.out.println("0_0");
            } ,
            () -> { logger.error("Cannot save this book entity: it's null!"); }
        );
    }

    public void removeBook(Book book){

        Optional.ofNullable(book).ifPresentOrElse(
            b -> bookManager.delete(b), () -> {}
        );
    }


    public void removeBooksByBaseCriteriaItems(Book book){

        Optional.ofNullable(book).ifPresentOrElse(b -> {
            final BigDecimal price = book.getPrice();
            final QBook qbook = QBook.book;
            final BooleanExpression deleteQuery =
                    new CriteriaQueryBuilder(qbook.isNotNull()).
                            notNullAnd(qbook.isbn::eq, b.getISBN()).
                            notNullAnd(qbook.name::eq, b.getName()).
                            notNullAnd(qbook.price::eq,
                                    (price != null)
                                    ? price.setScale(2, RoundingMode.HALF_EVEN)
                                    : price).
                            notNullAnd(qbook.publishingName::eq, b.getPublishingName()).
                            build();

            final List qResult = Lists.newArrayList(
                    bookManager.findAll(deleteQuery).iterator());

            if(!qResult.isEmpty())
                bookManager.deleteAll(qResult);

        }, () -> {});
    }

    public void removeAuthorsFromBook(
            Book targetBook, List<Author> authors){

        if(targetBook == null || authors == null) return;

        final BigDecimal price = targetBook.getPrice();
        final QBook qbook = QBook.book;
        final QAuthor qauthor = QAuthor.author;
        final BooleanExpression findQuery =
                new CriteriaQueryBuilder(qbook.isNotNull()).
                        eqOrNullAnd(qbook.isbn, targetBook.getISBN()).
                        eqOrNullAnd(qbook.name, targetBook.getName()).
                        eqOrNullAnd(qbook.price,
                                (price != null)
                                        ? price.setScale(2, RoundingMode.HALF_EVEN)
                                        : price
                        ).
                        eqOrNullAnd(qbook.publishingName, targetBook.getPublishingName()).
                        build();

        final Optional<Book> rBook = bookManager.findOne(findQuery);

        if(rBook.isPresent()){
            final Book foundBook = rBook.get();
            final List<String>fullNames = new ArrayList<>();
            targetBook.getAuthors().stream().
                    forEach(a ->
                            fullNames.add(a.getAuthorName()+" "+a.getAuthorLastName())
                    );
            JPADeleteClause queryDelete = new JPADeleteClause(entityManager, qauthor).
                    where(qauthor.authorName
                            .concat(" ")
                            .concat(qauthor.authorLastName).in(fullNames)
                            .and(qauthor.in(targetBook.getAuthors()))
            );
            queryDelete.execute();
        }else{
            logger.warn("Book not found by your criteria! Deletion aborted !");
        }
    }

    private void performTransaction(Consumer<EntityManager> action){

        EntityTransaction transaction = entityManager.getTransaction();

        try{
            transaction.begin();
            action.accept(entityManager);
            transaction.commit();
        }
        catch(RuntimeException e){
            transaction.rollback();
            final String error = e.getCause().getMessage();
            logger.error(error);
            throw new RuntimeException(error);
        }
    }

    public BookManagerService( EntityManager entityManager, IBookRepository bookManager,
            IAuthorRepository authorManager)
    {
        this.entityManager = entityManager;
        this.bookManager = bookManager;
        this.authorManager = authorManager;
    }
}
