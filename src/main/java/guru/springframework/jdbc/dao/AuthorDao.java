package guru.springframework.jdbc.dao;

import guru.springframework.jdbc.domain.Author;

public interface AuthorDao {
    Author getById(Long id);

    Author getByName(String firstName, String lastName);
}
