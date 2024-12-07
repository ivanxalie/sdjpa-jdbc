package guru.springframework.jdbc.dao;

import guru.springframework.jdbc.domain.Author;
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
@Import({AuthorDaoImpl.class, DataSourceExecutor.class})
class AuthorDaoIntegrationTest {
    @Autowired
    AuthorDao authorDao;

    @Test
    void testGetAuthorById() {
        Author author = authorDao.getById(1L);

        assertThat(author).isNotNull().satisfies(selectedAuthor -> {
            assertThat(selectedAuthor.getFirstName()).isEqualTo("Craig");
            assertThat(selectedAuthor.getLastName()).isEqualTo("Walls");
        });
    }

    @Test
    void testGetAuthorByName() {
        Author author = authorDao.getByName("Craig", "Walls");

        assertThat(author).isNotNull().satisfies(selectedAuthor -> assertThat(selectedAuthor.getId())
                .isEqualTo(1L));
    }

    @Test
    void testInsert() {
        Author author = authorDao.saveNew(Author.builder()
                .firstName("Andrew")
                .lastName("Bean")
                .build());

        Author saved = authorDao.getById(author.getId());

        assertThat(saved).isNotNull().satisfies(selectedAuthor -> {
            assertThat(selectedAuthor.getFirstName()).isEqualTo("Andrew");
            assertThat(selectedAuthor.getLastName()).isEqualTo("Bean");
        });

        authorDao.deleteById(saved.getId());
    }

    @Test
    void testUpdate() {
        Author authorToUpdate = authorDao.getById(1L);
        String previousLastName = authorToUpdate.getLastName();
        authorToUpdate.setLastName("Carlione");

        authorDao.update(authorToUpdate);

        Author fetched = authorDao.getById(authorToUpdate.getId());

        assertThat(fetched).isNotNull();
        assertThat(fetched.getLastName()).isEqualTo("Carlione");

        authorToUpdate.setLastName(previousLastName);
        authorDao.update(authorToUpdate);
    }

    @Test
    void testDeleteById() {
        Author author = authorDao.saveNew(Author.builder().build());

        authorDao.deleteById(author.getId());

        assertThat(authorDao.getById(author.getId())).isNull();
    }
}