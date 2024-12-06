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
@Import(AuthorDaoImpl.class)
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
}