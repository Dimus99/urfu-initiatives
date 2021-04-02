package com.example.demo.repos;

import com.example.demo.models.Initiative;
import org.springframework.data.repository.CrudRepository;

public interface InitiativeRepo extends CrudRepository<Initiative, Long> {
}
