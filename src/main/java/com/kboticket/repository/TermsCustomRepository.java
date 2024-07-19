package com.kboticket.repository;

import com.kboticket.domain.Terms;
import com.kboticket.enums.TermsType;

import java.util.List;

public interface TermsCustomRepository {

    List<Terms> findFirstByTitleOrderByVersionDesc(TermsType type);
}
