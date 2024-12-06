package guru.springframework.jdbc.dao;

import guru.springframework.jdbc.domain.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("local")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({BookDaoImpl.class, DataSourceExecutor.class})
class BookDaoIntegrationTest {
    @Autowired
    BookDao bookDao;

    @Test
    void testGetAuthorById() {
        Book book = bookDao.getById(1L);

        assertThat(book).isNotNull().satisfies(selectedBook -> {
            assertThat(selectedBook.getTitle()).isEqualTo("Spring in Action, 5th Edition");
            assertThat(selectedBook.getPublisher()).isEqualTo("Simon & Schuster");
            assertThat(selectedBook.getIsbn()).isEqualTo("978-1617294945");
        });
    }

    @Test
    void testGetAuthorByTitle() {
        Book author = bookDao.getByTitle("Spring in Action, 5th Edition");

        assertThat(author).isNotNull().satisfies(selectedAuthor -> assertThat(selectedAuthor.getId())
                .isEqualTo(1L));
    }

    @Test
    void testInsert() {
        Book author = bookDao.saveNew(Book.builder()
                .title("Andrew")
                .publisher("Bean")
                .isbn("Bean")
                .build());

        Book saved = bookDao.getById(author.getId());

        assertThat(saved).isNotNull().satisfies(selectedAuthor -> {
            assertThat(selectedAuthor.getTitle()).isEqualTo("Andrew");
            assertThat(selectedAuthor.getPublisher()).isEqualTo("Bean");
            assertThat(selectedAuthor.getIsbn()).isEqualTo("Bean");
        });

        bookDao.deleteById(author.getId());
    }

    @Test
    void testUpdate() {
        Book authorToUpdate = bookDao.getById(1L);
        String previousLastName = authorToUpdate.getIsbn();
        authorToUpdate.setIsbn("Carlione");

        bookDao.update(authorToUpdate);

        Book fetched = bookDao.getById(authorToUpdate.getId());

        assertThat(fetched).isNotNull();
        assertThat(fetched.getIsbn()).isEqualTo("Carlione");

        authorToUpdate.setIsbn(previousLastName);
        bookDao.update(authorToUpdate);
    }

    @Test
    void testDeleteById() {
        Book author = bookDao.saveNew(Book.builder().build());

        bookDao.deleteById(author.getId());

        assertThat(bookDao.getById(author.getId())).isNull();
    }
}