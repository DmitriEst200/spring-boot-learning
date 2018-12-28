package org.spring.learning.core.ioc.chapter1;

import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class BookManagerController {

    BookManagerService bookService;

    @RequestMapping(value = "/book/add", method = RequestMethod.POST)
    public String addBookEntry(
                Model model,
                @Validated @ModelAttribute("bookData") Book book,
                BindingResult result
    ){
        if(result.hasErrors()){
            return "error";
        }

        System.out.println("hello");

        bookService.addBook(book);

        return "add";
    }

    /* If you are using dependencies ( your class fields
     * which must contain bean content ) in the class and they should
     * be loaded as beans in Spring application context,
     * then use constructor with parameters for
     * injection in field instead of @Autowired or @Injectannotation
     */

    public BookManagerController(BookManagerService bookService){
        this.bookService = bookService;
    }
}
