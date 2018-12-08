package org.spring.learning.core.ioc.chapter1;

import com.google.common.collect.Lists;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Visitor;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPADeleteClause;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import javax.annotation.Nullable;
import javax.persistence.EntityManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class BookManagerService{

    @Autowired
    IBookRepository bookManager;

    @Autowired
    IAuthorRepository authorManager;

    @Autowired
    EntityManager em;

    public void addBookRecord(Book book){

        Optional.ofNullable(book).ifPresentOrElse(
            b -> bookManager.save(b), () -> {}
        );
    }

    public void removeBook(Book book){

        Optional.ofNullable(book).ifPresentOrElse(
            b -> bookManager.delete(b), () -> {}
        );
    }

    public void removeBooksByBaseCriteriaItems(Book book){

        Optional.ofNullable(book).ifPresentOrElse(b -> {

            final QBook qbook = QBook.book;
            final BooleanExpression deleteQuery =
                    new CriteriaQueryBuilder(qbook.isNotNull()).
                            notNullAnd(qbook.isbn::eq, b.getISBN()).
                            notNullAnd(qbook.name::eq, b.getName()).
                            notNullAnd(qbook.price::eq, b.getPrice()).
                            notNullAnd(qbook.publishingName::eq, b.getPublishingName()).
                            build();

            final List qResult = Lists.newArrayList(
                    bookManager.findAll(deleteQuery).iterator());

            if(!qResult.isEmpty())
                bookManager.deleteAll(qResult);

        }, () -> {});
    }

    public void removeAuthorsFromBook(Book targetBook, List<Author> authors){

        if(targetBook == null || authors == null) return;

        final QBook qbook = QBook.book;
        final BooleanExpression findQuery =
                new CriteriaQueryBuilder(qbook.isNotNull()).
                        notNullAnd(qbook.isbn::eq, targetBook.getISBN()).
                        notNullAnd(qbook.name::eq, targetBook.getName()).
                        notNullAnd(qbook.price::eq, targetBook.getPrice()).
                        notNullAnd(qbook.publishingName::eq, targetBook.getPublishingName()).
                        build();

            final Optional<Book> book = bookManager.findOne(findQuery);
            book.ifPresent((v) -> {
                //authorManager.deleteById(v.getId());

                final QAuthor qauthor = QAuthor.author;

                System.out.println(
                        new JPADeleteClause(em, qauthor).where(qauthor.in(authors).
                                and(qauthor.books.contains(v)).eq(qbook.id.eq(v.getId()))).execute());



            });
    }
}
