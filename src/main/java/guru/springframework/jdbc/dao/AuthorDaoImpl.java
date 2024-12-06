package guru.springframework.jdbc.dao;

import guru.springframework.jdbc.domain.Author;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class AuthorDaoImpl implements AuthorDao {
    private final DataSourceExecutor executor;

    @Override
    public Author getById(Long id) {
        return executor.select(
                "select id, first_name, last_name from author where id = ?",
                executor.idFunction(singleAuthorMapper()),
                id
        );
    }

    private DataSourceExecutor.Mapper<Author> singleAuthorMapper() {
        return resultSet -> Author.builder()
                .id(resultSet.getLong(1))
                .firstName(resultSet.getString(2))
                .lastName(resultSet.getString(3))
                .build();
    }

    @Override
    public Author getByName(String firstName, String lastName) {
        return executor.select(
                "select id, first_name, last_name from author where first_name = ? and last_name = ?",
                executor.idFunction(singleAuthorMapper()),
                firstName,
                lastName
        );
    }

    @Override
    @SneakyThrows
    public Author saveNew(Author author) {
        Long id = executor.insert("author", Map.of(
                        "first_name", Objects.toString(author.getFirstName(), ""),
                        "last_name", Objects.toString(author.getLastName(), "")
                )
        );
        author.setId(id);
        return author;
    }

    @Override
    public Author update(Author author) {
        executor.update("author", author.getId(), Map.of(
                "first_name", Objects.toString(author.getFirstName(), ""),
                "last_name", Objects.toString(author.getLastName(), "")
        ));
        return author;
    }

    @Override
    public void delete(Long id) {
        executor.delete("author", id);
    }
}
