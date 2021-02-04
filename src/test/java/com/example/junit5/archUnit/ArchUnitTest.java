package com.example.junit5.archUnit;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

public class ArchUnitTest {
    JavaClasses classes = new ClassFileImporter().importPackages("com.example.junit5");

    @Test
    void packageDependencyTests() {

        ArchRule domainPackageRule = classes().that().resideInAPackage("..domain..")
                .should().onlyBeAccessed().byClassesThat()
                .resideInAnyPackage("..study..", "..member..", "..domain..");

        domainPackageRule.check(classes);
    }

    @Test
    void memberPackageRule() {
        ArchRule memberPackageRule = noClasses().that().resideInAPackage("..domain..")
                .should().accessClassesThat().resideInAPackage("..member..");

        memberPackageRule.check(classes);
    }

    @Test
    void studyPackageRule() {
        ArchRule studyPackageRule = noClasses().that().resideOutsideOfPackage("..study..")
                .should().accessClassesThat().resideInAPackage("..study..");

        studyPackageRule.check(classes);
    }

    @Test
    void slicePackageRule() {
        ArchRule studyPackageRule = noClasses().that().resideOutsideOfPackage("..study..")
                .should().accessClassesThat().resideInAPackage("..study..");

        studyPackageRule.check(classes);
    }
}
