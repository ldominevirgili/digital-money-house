package com.digitalmoneyhouse.account.service;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class CvuAliasGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int CVU_LENGTH = 22;

    private static final String[] WORDS = {
        "dinero", "casa", "digital", "billetera", "cuenta", "banco",
        "transfer", "pago", "saldo", "clave", "red", "seguro",
        "moneda", "virtual", "uniforme", "alias", "usuario", "acceso"
    };

    public String generateCvu() {
        StringBuilder sb = new StringBuilder(CVU_LENGTH);
        for (int i = 0; i < CVU_LENGTH; i++) {
            sb.append(RANDOM.nextInt(10));
        }
        return sb.toString();
    }

    public String generateAlias() {
        List<String> selected = IntStream.range(0, 3)
            .mapToObj(i -> WORDS[RANDOM.nextInt(WORDS.length)])
            .collect(Collectors.toList());
        return String.join(".", selected);
    }

    public String generateUniqueCvu(CvuAliasUniquenessChecker uniquenessChecker) {
        String cvu;
        do {
            cvu = generateCvu();
        } while (uniquenessChecker.existsCvu(cvu));
        return cvu;
    }

    public String generateUniqueAlias(CvuAliasUniquenessChecker uniquenessChecker) {
        String alias;
        int maxAttempts = 100;
        int attempts = 0;
        do {
            alias = generateAlias();
            attempts++;
            if (attempts >= maxAttempts) {
                throw new IllegalStateException("No se pudo generar alias unico");
            }
        } while (uniquenessChecker.existsAlias(alias));
        return alias;
    }

    @FunctionalInterface
    public interface CvuAliasUniquenessChecker {
        boolean existsCvu(String cvu);
        boolean existsAlias(String alias);
    }
}
