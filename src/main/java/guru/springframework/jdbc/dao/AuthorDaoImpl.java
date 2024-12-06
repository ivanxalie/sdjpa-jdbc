package guru.springframework.jdbc.dao;

import guru.springframework.jdbc.domain.Author;
import liquibase.pro.packaged.O;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class AuthorDaoImpl implements AuthorDao {
    private final DataSourceExecutor executor;

    @Override
    public Author getById(Long id) {
        return executor.select(
                "select id, first_name, last_name from author where id = ?",
                executor.idFunction(executor.singleAuthorMapper()),
                id
        );
    }

    @Override
    public Author getByName(String firstName, String lastName) {
        return executor.select(
                "select id, first_name, last_name from author where first_name = ? and last_name = ?",
                executor.idFunction(executor.singleAuthorMapper()),
                firstName,
                lastName
        );
    }

    @Override
    @SneakyThrows
    public Author saveNew(Author author) {
        Long id = executor.insert("author", Map.of("first_name", author.getFirstName(), "last_name",
                author.getLastName()));
        author.setId(id);
        return author;
    }

    @Override
    public Author update(Author author) {
        executor.update("author", author.getId(), Map.of("first_name", author.getFirstName(), "last_name",
                author.getLastName()));
        return author;
    }
}
