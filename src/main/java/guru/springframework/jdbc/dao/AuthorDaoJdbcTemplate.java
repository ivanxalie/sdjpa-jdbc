package guru.springframework.jdbc.dao;

import guru.springframework.jdbc.domain.Author;
import guru.springframework.jdbc.domain.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class AuthorDaoJdbcTemplate implements AuthorDao {
    private final ObjectProvider<RowMapper<Author>> mapper;
    private final JdbcTemplate template;

    @Override
    public Author getById(Long id) {
        try {
            List<Author> authors = template.query(
                    "select " +
                            "a.id id, " +
                            "first_name, " +
                            "last_name, " +
                            "b.id book_id, " +
                            "b.isbn, " +
                            "b.publisher, " +
                            "b.title, " +
                            "b.author_id " +
                            "from book b " +
                            "right join author a on b.author_id = a.id " +
                            "where a.id = ?", mapper.getObject(), id);
            if (authors.size() == 1) return authors.get(0);
            return merge(authors);
        } catch (Exception e) {
            return null;
        }
    }

    private Author merge(List<Author> authors) {
        Author author = authors.get(0);
        List<Book> books = authors
                .stream()
                .map(Author::getBooks)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .peek(book -> book.setAuthor(author))
                .toList();
        author.setBooks(books);
        return author;
    }

    @Override
    public Author getByName(String firstName, String lastName) {
        try {
            List<Author> authors = template.query(
                    "select " +
                            "a.id id, " +
                            "first_name, " +
                            "last_name, " +
                            "b.id book_id, " +
                            "b.isbn, " +
                            "b.publisher, " +
                            "b.title, " +
                            "b.author_id " +
                            "from book b " +
                            "right join author a on b.author_id = a.id " +
                            "where first_name = ? and last_name = ?",
                    mapper.getObject(), firstName, lastName);
            if (authors.size() == 1) return authors.get(0);
            return merge(authors);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Author saveNew(Author author) {
        String sql = "INSERT INTO author (first_name, last_name) VALUES (?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        template.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setObject(1, author.getFirstName());
            ps.setObject(2, author.getLastName());
            return ps;
        }, keyHolder);
        author.setId(keyHolder.getKey().longValue());
        return author;
    }

    @Override
    public Author update(Author author) {
        template.update(
                "update author set first_name = ?, last_name = ? where id = ?"
                , author.getFirstName(), author.getLastName(), author.getId());
        return author;
    }

    @Override
    public void deleteById(Long id) {
        template.update("delete from author where id = ?", id);
    }
}
