package org.spring.learning.core.ioc;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.modelmapper.ModelMapper;
import org.spring.learning.core.ioc.chapter1.Author;
import org.spring.learning.core.ioc.chapter1.Book;
import org.spring.learning.core.ioc.chapter1.BookDTO;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.util.*;

@RunWith(Parameterized.class)
public class DataTests {

    private ModelMapper modelMapper = new ModelMapper();
    private ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private Book testBook;

    @Ignore
    @Test
    public void CheckMySqlConnection(){

        try{

            Connection conn = DriverManager.
                    getConnection("jdbc:mysql://localhost:3306/testdatabase?" +
                            "user=root&password=jack206*-*");

        }catch(SQLException e){

            System.out.println("SQLException: " + e.getMessage());
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("VendorError: " + e.getErrorCode());
        }

    }

    @Ignore
    @Test
    public void PrimitiveOperations(){

        BigDecimal num = new BigDecimal("29.3575217438265872567217751245218764837563756783264521761254187847624");
        BigDecimal num2 = num;
        System.out.println("accurate value: "+num);
        System.out.println("approximate value:"+num.toString());
        System.out.println("plain value:"+num.toPlainString());
        num2.multiply(new BigDecimal("2.0"));
        System.out.println(num);
        System.out.println("rounded num1: "+num.setScale(2, RoundingMode.HALF_DOWN));
        num = new BigDecimal("65.54332652356781256875178597927352638");
        System.out.println("new num1 = "+num);
        System.out.println("rounded num1: "+num.setScale(2, RoundingMode.HALF_DOWN));
        num2 = new BigDecimal("1.555");
        System.out.println("rounded num2: "+num2.setScale(2, RoundingMode.HALF_DOWN));

    }

    @Test
    public void CheckCopyFromEntityToDTO(){

        Set<ConstraintViolation<Book>> violations = factory.getValidator().validate(testBook);
        Assert.assertTrue("Error messages is occured!!!",violations.isEmpty());

        BookDTO bookDTO = testBook.toBookDTO();
        Assert.assertEquals(bookDTO.getId(), testBook.getId());
        Assert.assertEquals(bookDTO.getName(), testBook.getName());

        if(testBook.getPrice() != null)
            Assert.assertEquals(bookDTO.getPrice(), testBook.getPrice().floatValue(), 0.002);
    }

    @Parameters( name = "book No {index}:" )
    public static List<Book> setData(){

        List<Book> data = new ArrayList();

        Book b = new Book("Война и мир");
        b.setPrice(new BigDecimal(78.67));
        b.getAuthors().add( new Author("Лев","Толстой"));
        data.add(b);

        b = new Book("Тёмная сторона Солнца");
        //b.setISBN("6857-586-32-456-0");
        b.setPublishingName("Visotsky Consulting");
        data.add(b);

        b = new Book("Малый бизнес. Большая игра");
        b.getAuthors().add( new Author("Александр","Высоцкий" ));
        b.setPublishingName("Visotsky Consulting");
        b.setISBN("978-966-2022-70-4");
        b.setPrice(new BigDecimal(47.36));
        data.add(b);

        b = new Book("Россия: между прошлым и будущим");
        b.getAuthors().add( new Author("Вильонар", "Васильевич") );
        b.getAuthors().add( new Author("Михаил","Васильевич") );
        b.setPublishingName("Алетейя");
        b.setISBN("978-5-907115-13-2");
        b.setPrice(new BigDecimal(33.00));
        data.add(b);

        return data;
    }

    public DataTests(Book book){
        testBook = book;
    }
}
