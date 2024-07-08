package com.kboticket.repository;

import com.kboticket.domain.Terms;
import com.kboticket.domain.TermsPk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TermsRepository extends JpaRepository<Terms, TermsPk>, QuerydslPredicateExecutor<Terms> {

    List<Terms> findByMandatoryTrue();

}
