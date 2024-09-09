package com.kboticket.service.terms;

import com.kboticket.domain.Terms;
import com.kboticket.dto.TermsDto;
import com.kboticket.enums.ErrorCode;
import com.kboticket.enums.TermsType;
import com.kboticket.exception.KboTicketException;
import com.kboticket.repository.terms.TermsCustomRepository;
import com.kboticket.repository.terms.TermsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

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


    public List<TermsDto> findLatestTermsByTitle(TermsType type) {
        List<Terms> termsList = termsRepositoryCustom.findFirstByTitleOrderByVersionDesc(type);

        List<TermsDto> termsDtos = termsList.stream()
                .map(terms -> {
                    return TermsDto.from(terms);
                }).collect(Collectors.toList());
        return termsDtos;
    }

    public boolean checkAllMandatoryTermsAgreed(List<TermsDto> termsList) {
        List<Terms> mandatoryTerms = termsRepository.findByMandatoryTrueAndType(TermsType.SIGNIN);

        if (mandatoryTerms.size() != termsList.size()) {
            throw new KboTicketException(ErrorCode.NOT_CHECKED_MANDATORY_TERMS);
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
                throw new KboTicketException(ErrorCode.NOT_CHECKED_MANDATORY_TERMS);
            }
        }
        return true;
    }
}
