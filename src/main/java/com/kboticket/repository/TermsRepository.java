package com.kboticket.repository;

import com.kboticket.domain.Terms;
import com.kboticket.domain.TermsPk;
import com.kboticket.enums.TermsType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Repository
public interface TermsRepository extends JpaRepository<Terms, TermsPk>, QuerydslPredicateExecutor<Terms> {

    List<Terms> findByMandatoryTrueAndType(TermsType type);
}
