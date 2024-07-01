package com.kboticket.service;

import com.kboticket.domain.Terms;
import com.kboticket.domain.TermsPk;
import com.kboticket.domain.User;
import com.kboticket.dto.TermsDto;
import com.kboticket.repository.TermsRepository;
import com.kboticket.repository.TermsRepositoryCustom;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TermsService {

    private final TermsRepository termsRepository;
    private final TermsRepositoryCustom termsRepositoryCustom;


    public Terms getTermsByTermPk(String title, String version) {
        return termsRepository.findByTitleAndVersion(title, version)
                .orElseThrow(() -> new EntityNotFoundException("error"));
    }

    public List<Terms> getAllTerms() {
        return termsRepository.findAll();
    }

    public Terms createNewTerms(TermsDto termsDto) {
        Terms terms = Terms.builder()
                .title(termsDto.getTitle())
                .version(termsDto.getVersion())
                .content(Base64.getEncoder().encodeToString(termsDto.getContent().getBytes()))
                .mandatory(termsDto.isMandatory())  // boolean 의 getter은 isMandatory~
                .build();

        return termsRepository.save(terms);
    }


    public List<Terms> findLatestTermsByTitle() {
        return termsRepositoryCustom.findFirstByTitleOrderByVersionDesc();
   }

    public boolean checkAllMandatoryTermsAgreed(List<TermsDto> termsList) {

        List<Terms> mandatoryTerms = termsRepository.findByMandatoryTrue();

        if (mandatoryTerms.size() != termsList.size()) {
            return false;
        }

        for (Terms mandatoryTerm : mandatoryTerms) {
            boolean found = false;
            for (TermsDto dto : termsList) {
                if (mandatoryTerm.getTitle().equals(dto.getTitle()) &&
                        mandatoryTerm.getVersion().equals(dto.getVersion())) {
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
