package org.spring.learning.core.ioc;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.spring.learning.core.ioc.chapter1.DAO;
import org.spring.learning.core.ioc.chapter1.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;


// It's test class for checking data saving and
// removing inside database on the persistance layer

@RunWith(SpringRunner.class)

// If database is non embedded then you must enable autoconfiguration
// avoiding its replacing

@AutoConfigureTestDatabase(replace = Replace.NONE)
@DataJpaTest
public class JPADataTests {

    @Autowired
    private DAO bookManager;

    @Test
    public void TestAddBookRecord(){

        bookManager.add(DataTests.SetData().get(0));

    }

}
