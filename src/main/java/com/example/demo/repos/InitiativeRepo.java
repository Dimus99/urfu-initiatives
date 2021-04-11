package com.example.demo.repos;

import com.example.demo.models.Initiative;
import com.example.demo.models.User;
import org.springframework.data.repository.CrudRepository;

public interface InitiativeRepo extends CrudRepository<Initiative, Long> {
    Iterable<Initiative> findInitiativesByAuthor(User author);
}
