package com.kboticket.service;

import com.kboticket.domain.Terms;
import com.kboticket.dto.TermsDto;
import com.kboticket.enums.TermsType;
import com.kboticket.repository.TermsCustomRepository;
import com.kboticket.repository.TermsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TermsService {

    private final TermsRepository termsRepository;
    private final TermsCustomRepository termsRepositoryCustom;

    public List<Terms> getAllTerms() {
        return termsRepository.findAll();
    }

    public Terms createNewTerms(TermsDto termsDto) {
        Terms terms = Terms.builder()
                .title(termsDto.getTitle())
                .version(termsDto.getVersion())
                .content(Base64.getEncoder().encodeToString(termsDto.getContent().getBytes()))
                .mandatory(termsDto.isMandatory())
                .build();

        return termsRepository.save(terms);
    }


    public List<Terms> findLatestTermsByTitle(TermsType type) {
        return termsRepositoryCustom.findFirstByTitleOrderByVersionDesc(type);
    }

    public boolean checkAllMandatoryTermsAgreed(List<TermsDto> termsList) {
        List<Terms> mandatoryTerms = termsRepository.findByMandatoryTrue();

        if (mandatoryTerms.size() != termsList.size()) {
            return false;
        }

        for (Terms mandatoryTerm : mandatoryTerms) {
            boolean found = false;
            for (TermsDto dto : termsList) {

                if (mandatoryTerm.getTermsPk().getTitle().equals(dto.getTitle()) &&
                        mandatoryTerm.getTermsPk().getVersion().equals(dto.getVersion())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }
        return true;
    }
}
