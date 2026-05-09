package com.capstone.kkumteul.domain.vocab.service;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * vocab service 패키지가 carrier(웹/카프카) 의존을 갖지 않음을 빌드 시점에 검증.
 *
 * <p>이 테스트가 깨진다는 것은 누군가 service에 web DTO 또는 kafka import를 추가했다는 신호.
 * Phase 2 카프카 어댑터 추가 시점에도 service 코드 자체는 변경되지 않아야 한다.</p>
 */
class VocabServiceArchTest {

    private static final JavaClasses CLASSES = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.capstone.kkumteul");

    @Test
    void vocab_service_has_no_web_or_kafka_dependencies() {
        noClasses()
                .that().resideInAPackage("..vocab.service..")
                .should().dependOnClassesThat()
                .resideInAnyPackage(
                        "..vocab.web..",
                        "..vocab.message..",
                        "org.springframework.kafka..",
                        "org.springframework.web..",
                        "jakarta.servlet.."
                )
                .check(CLASSES);
    }
}
