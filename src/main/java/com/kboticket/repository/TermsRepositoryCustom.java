package com.kboticket.repository;

import com.kboticket.domain.Terms;

import java.util.List;

public interface TermsRepositoryCustom {

    List<Terms> findFirstByTitleOrderByVersionDesc();
}
