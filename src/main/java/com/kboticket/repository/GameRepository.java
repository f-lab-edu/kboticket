package com.kboticket.repository;

import com.kboticket.domain.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GameRepository extends JpaRepository<Game, Long>, GameCustomRepository {

    Optional<Game> findById(Long id);

    List<Game> findAll();


}
