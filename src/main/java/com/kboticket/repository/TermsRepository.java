package com.kboticket.repository;

import com.kboticket.domain.Terms;
import com.kboticket.domain.TermsPk;
import com.kboticket.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TermsRepository extends JpaRepository<Terms, TermsPk>, QuerydslPredicateExecutor<Terms> {

    Optional<Terms> findByTitleAndVersion(String title, String version);

    List<Terms> findByMandatoryTrue();

}
