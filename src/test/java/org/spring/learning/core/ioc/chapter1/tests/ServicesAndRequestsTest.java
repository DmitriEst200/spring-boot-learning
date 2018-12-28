package org.spring.learning.core.ioc.chapter1.tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.spring.learning.core.ioc.chapter1.Book;
import org.spring.learning.core.ioc.chapter1.BookManagerController;
import org.spring.learning.core.ioc.chapter1.BookManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.filter.CorsFilter;
import org.mockito.Mockito;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
/*@ContextConfiguration
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest*/
public class ServicesAndRequestsTest{

    @Mock
    private BookManagerService bms;

    //@Autowired
    private MockMvc mockMvc;

    @InjectMocks
    private BookManagerController bmc;

    private Logger logger = LogManager.getLogger(ServicesAndRequestsTest.class);

    @BeforeEach
    public void init(){
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(bmc)
                .build();
    }

    @ParameterizedTest(name = "book No {index}:")
    @ValueSource(ints = {2,3,4})
    public void testSaveBook(int index) throws Exception{

        Assert.assertTrue("Index out of collection",
                index >= 0 &&
                          index <= DataTest.setData().size() - 1);

        Book book = DataTest.setData().get(index);

        Mockito.doCallRealMethod().when(bms).addBook(book);
        mockMvc.perform(
                post("/api/book/add")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(toJSONString(book)))
                .andExpect(status().isCreated());
    }

    public static String toJSONString(Object obj){
        try{
            return new ObjectMapper().writeValueAsString(obj);
        }catch(JsonProcessingException e){
            throw new RuntimeException(e);
        }
    }
}
