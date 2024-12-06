package guru.springframework.jdbc.dao;

import guru.springframework.jdbc.domain.Author;

public interface AuthorDao {
    Author getById(Long id);

    Author getByName(String firstName, String lastName);

    Author saveNew(Author author);

    Author update(Author author);

    void deleteById(Long id);
}
