package com.kboticket.repository;

import com.kboticket.domain.QTerms;
import com.kboticket.domain.Terms;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@AllArgsConstructor
public class TermsRepositoryCustomImpl implements TermsRepositoryCustom {

    private EntityManager em;

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Terms> findFirstByTitleOrderByVersionDesc() {
        QTerms terms = QTerms.terms;
        QTerms termsSub = new QTerms("termsSub");

        return queryFactory
                .select(terms)
                .from(terms)
                .where(terms.termsPk.version.eq(
                        JPAExpressions
                                .select(termsSub.termsPk.version.max())
                                .from(termsSub)
                                .where(termsSub.termsPk.title.eq(terms.termsPk.title))
                ))
                .fetch();
    }
}
