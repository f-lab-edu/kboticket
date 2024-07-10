package com.kboticket.repository;

import com.kboticket.domain.Terms;

import java.util.List;

public interface TermsCustomRepository {

    List<Terms> findFirstByTitleOrderByVersionDesc();
}
