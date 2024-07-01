package com.kboticket.repository;

import com.kboticket.domain.Game;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class GameRepository {

    private final EntityManager em;

    public Game findOne(Long id) {
        return em.find(Game.class, id);
    }

    public List<Game> findAll () {
       return em.createQuery("select g from Game g", Game.class)
                 .getResultList();
    }
}
