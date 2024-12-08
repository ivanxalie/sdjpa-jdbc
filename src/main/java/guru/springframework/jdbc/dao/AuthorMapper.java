package guru.springframework.jdbc.dao;

import guru.springframework.jdbc.domain.Author;
import guru.springframework.jdbc.domain.Book;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class AuthorMapper implements RowMapper<Author> {
    @Override
    public Author mapRow(ResultSet rs, int rowNum) throws SQLException {
        Author.AuthorBuilder builder = Author.builder()
                .id(rs.getLong("id"))
                .firstName(rs.getString("first_name"))
                .lastName(rs.getString("last_name"));
        long bookId = rs.getLong("book_id");
        if (bookId != -1) {
            Book book = Book.builder()
                    .id(bookId)
                    .isbn(rs.getString("isbn"))
                    .publisher(rs.getString("publisher"))
                    .title(rs.getString("title"))
                    .build();
            builder.books(List.of(book));
        }
        return builder
                .build();
    }
}
