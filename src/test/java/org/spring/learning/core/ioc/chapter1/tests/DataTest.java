package org.spring.learning.core.ioc.chapter1.tests;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.modelmapper.ModelMapper;
import org.spring.learning.core.ioc.chapter1.Author;
import org.spring.learning.core.ioc.chapter1.Book;
import org.spring.learning.core.ioc.chapter1.BookDTO;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.util.*;


public class DataTest {

    private ModelMapper modelMapper = new ModelMapper();
    private ValidatorFactory factory = Validation.buildDefaultValidatorFactory();


    @Ignore
    @Test
    public void checkMySqlConnection(){

        //for testing the method: will either be thrown specific exception or not

        SQLException SQLerror = Assertions.assertThrows(
                SQLException.class,
                () -> { Connection conn = DriverManager.
                        getConnection("jdbc:mysql://localhost:3306/testdatabase?" +
                        "user=root&password=jack206*-*");

                },"No sql error found. Access approved!!!");

        System.out.println("SQLException: " + SQLerror.getMessage());
        System.out.println("SQLState: " + SQLerror.getSQLState());
        System.out.println("VendorError: " + SQLerror.getErrorCode());
    }

    @Ignore
    @Test
    public void primitiveOperations(){

        BigDecimal num = new BigDecimal("29.35752174382658725672177512452187" +
                "64837563756783264521761254187847624");
        BigDecimal num2 = num;
        System.out.println("accurate value: "+num);
        System.out.println("accurate string value: "+num.toString());
        System.out.println("plain value: "+num.toPlainString());
        num = num2.multiply(new BigDecimal("2.856826572346745"));

        Assert.assertFalse(
                "Float value must be without scientific notation",
                 num.toString().toLowerCase().contains("e")
        );

        System.out.println("number num1 after multiplying = "+num);
        System.out.println("rounded num1: "+
                num.setScale(2, RoundingMode.HALF_DOWN));

        num = new BigDecimal("65.54332652356781256875178597927352638");
        System.out.println("new num1 = "+num);

        System.out.println("rounded num1: "+
                num.setScale(2, RoundingMode.HALF_DOWN));
        num2 = new BigDecimal("1.555");
        System.out.println("rounded num2: "+
                num2.setScale(2, RoundingMode.HALF_DOWN));

    }

    @ParameterizedTest(name = "book No {index}:")
    @MethodSource("setData")
    public void checkCopyFromEntityToDTO(Book testBook){

        Set<ConstraintViolation<Book>> violations =
                factory.getValidator().validate(testBook);
        Assert.assertTrue("Error messages is occured!!!",
                violations.isEmpty());

        BookDTO bookDTO = testBook.toBookDTO();
        Assert.assertEquals(bookDTO.getId(), testBook.getId());
        Assert.assertEquals(bookDTO.getName(), testBook.getName());
        Assert.assertEquals(bookDTO.getISBN(), testBook.getISBN());
        Assert.assertEquals(bookDTO.getPublishingName(), testBook.getPublishingName());

        if(testBook.getPrice() != null)
            Assert.assertEquals(
                    bookDTO.getPrice(),
                    testBook.getPrice().floatValue(), 0.002);
    }

    public static final List<Book> setData(){

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

        b = new Book("Тайны Библии. Научные открытия, находки, факты");
        b.getAuthors().add( new Author("Яна","Грецова") );
        b.getAuthors().add( new Author("Ева", "Павлычева") );
        b.setPublishingName("Никея");
      //  b.setISBN("978-5-91761-927-9");
        b.setPrice(new BigDecimal(9.92));
        data.add(b);

        b = new Book("Санузел");
        b.getAuthors().add( new Author("Александр", "Кира"));
        b.setPublishingName("Студия Артемия Лебедева");
        b.setISBN("978-5-98062-102-5");
        b.setPrice(new BigDecimal(22.45));
        data.add(b);

        return data;
    }
}
