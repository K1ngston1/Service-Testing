package com.example.Refactoring;

/*
 @author K1ngst0n
 @project Refactoring
 @class ArchitectureTests
 @version 1.0.0
 @since 4/19/2025 - 3:13 PM
*/


import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.parser.Entity;

import static com.tngtech.archunit.lang.conditions.ArchConditions.haveNameMatching;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.lang.conditions.ArchConditions.not;


@SpringBootTest
class ArchitectureTests {

    private JavaClasses applicationClasses;

    @BeforeEach
    void setUp() {
        applicationClasses = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("com.example.Refactoring");
    }


    // Перевірка дотримання шарової архітектури (Controller -> Service -> Repository)
    @Test
    void shouldFollowLayeredArchitecture() {
        layeredArchitecture()
                .consideringAllDependencies()
                .layer("Controller").definedBy("..controller..")
                .layer("Service").definedBy("..service..")
                .layer("Repository").definedBy("..repository..")
                .whereLayer("Controller").mayNotBeAccessedByAnyLayer()
                .whereLayer("Service").mayOnlyBeAccessedByLayers("Controller")
                .whereLayer("Repository").mayOnlyBeAccessedByLayers("Service")
                .check(applicationClasses);
    }

    // Контролери не повинні залежати один від одного
    @Test
    void controllersShouldNotDependOnOtherControllers() {
        noClasses()
                .that().resideInAPackage("..controller..")
                .should().dependOnClassesThat().resideInAPackage("..controller..")
                .because("контролери не повинні залежати один від одного")
                .check(applicationClasses);
    }

    // Репозиторії не повинні залежати від сервісів
    @Test
    void repositoriesShouldNotDependOnServices() {
        noClasses()
                .that().resideInAPackage("..repository..")
                .should().dependOnClassesThat().resideInAPackage("..service..")
                .because("репозиторії не повинні залежати від сервісів")
                .check(applicationClasses);
    }

    // Імена контролерів мають закінчуватись на "Controller"
    @Test
    void controllerClassesShouldBeNamedXController() {
        classes()
                .that().resideInAPackage("..controller..")
                .should().haveSimpleNameEndingWith("Controller")
                .because("назва класів контролерів має завершуватись на 'Controller'")
                .check(applicationClasses);
    }

    // Контролери мають бути анотовані @RestController
    @Test
    void controllerClassesShouldBeAnnotatedWithRestController() {
        classes()
                .that().resideInAPackage("..controller..")
                .should().beAnnotatedWith(RestController.class)
                .because("контролери мають бути анотовані @RestController")
                .check(applicationClasses);
    }

    // Репозиторії мають бути інтерфейсами
    @Test
    void repositoryShouldBeInterface() {
        classes()
                .that().resideInAPackage("..repository..")
                .should().beInterfaces()
                .because("репозиторії мають бути інтерфейсами")
                .check(applicationClasses);
    }

    // Заборонено використовувати @Autowired на полях у контролерах
    @Test
    void controllerFieldsShouldNotBeAnnotatedWithAutowired() {
        noFields()
                .that().areDeclaredInClassesThat().resideInAPackage("..controller..")
                .should().beAnnotatedWith(Autowired.class)
                .because("рекомендується використовувати конструкторну ін'єкцію замість поля з @Autowired")
                .check(applicationClasses);
    }

    // Поля моделей не мають бути публічними
    @Test
    void modelFieldsShouldNotBePublic() {
        fields()
                .that().areDeclaredInClassesThat().resideInAPackage("..model..")
                .should().notBePublic()
                .because("поля моделі мають бути приватними, щоб забезпечити інкапсуляцію")
                .check(applicationClasses);
    }

    // Класи сервісів повинні бути анотовані @Service
    @Test
    void serviceClassesShouldBeAnnotatedWithService() {
        classes()
                .that().resideInAPackage("..service..")
                .should().beAnnotatedWith(org.springframework.stereotype.Service.class)
                .because("сервіси мають бути анотовані @Service")
                .check(applicationClasses);
    }

    // Репозиторії мають бути анотовані @Repository
    @Test
    void repositoryClassesShouldBeAnnotatedWithRepository() {
        classes()
                .that().resideInAPackage("..repository..")
                .should().beAnnotatedWith(org.springframework.stereotype.Repository.class)
                .because("репозиторії мають бути анотовані @Repository")
                .check(applicationClasses);
    }

    // Назви сервісів мають завершуватись на "Service"
    @Test
    void serviceClassesShouldHaveNameEndingWithService() {
        classes()
                .that().resideInAPackage("..service..")
                .should().haveSimpleNameEndingWith("Service")
                .because("сервіси мають закінчуватись на 'Service'")
                .check(applicationClasses);
    }

    // Назви репозиторіїв мають завершуватись на "Repository"
    @Test
    void repositoryClassesShouldHaveNameEndingWithRepository() {
        classes()
                .that().resideInAPackage("..repository..")
                .should().haveSimpleNameEndingWith("Repository")
                .because("репозиторії мають закінчуватись на 'Repository'")
                .check(applicationClasses);
    }

    // Моделі мають бути розміщені в пакеті "model"
    @Test
    void modelClassesShouldBeInModelPackage() {
        classes()
                .that().haveSimpleNameEndingWith("Patient")
                .should().resideInAPackage("..model..")
                .because("класи моделей мають бути в пакеті 'model'")
                .check(applicationClasses);
    }

    // Контролери мають бути класами, а не інтерфейсами
    @Test
    void controllerClassesShouldNotBeInterfaces() {
        classes()
                .that().resideInAPackage("..controller..")
                .should().notBeInterfaces()
                .because("контролери мають бути класами, а не інтерфейсами")
                .check(applicationClasses);
    }

    // Сервіси мають бути публічними
    @Test
    void serviceClassesShouldBePublic() {
        classes()
                .that().resideInAPackage("..service..")
                .should().bePublic()
                .because("сервіси мають бути доступні для інших шарів")
                .check(applicationClasses);
    }

    // Методи контролера мають бути публічними
    @Test
    void controllerMethodsShouldBePublic() {
        methods()
                .that().areDeclaredInClassesThat().resideInAPackage("..controller..")
                .should().bePublic()
                .because("методи контролерів мають бути публічними")
                .check(applicationClasses);
    }

    // Кожна модель має мати конструктор без параметрів
    @Test
    void modelClassesShouldHaveNoArgsConstructor() {
        ArchCondition<JavaClass> haveNoArgsConstructor = new ArchCondition<>("have a no-args constructor") {
            @Override
            public void check(JavaClass javaClass, ConditionEvents events) {
                boolean hasNoArgsConstructor = javaClass.getConstructors().stream()
                        .anyMatch(constructor -> constructor.getRawParameterTypes().isEmpty());

                if (!hasNoArgsConstructor) {
                    String message = String.format("Клас %s не має конструктора без аргументів", javaClass.getName());
                    events.add(SimpleConditionEvent.violated(javaClass, message));
                }
            }
        };

        classes()
                .that().resideInAPackage("..model..")
                .should(haveNoArgsConstructor)
                .check(applicationClasses);
    }
    // Контролери мають залежати тільки від сервісів, моделей або стандартних бібліотек
    @Test
    void controllersShouldOnlyDependOnServiceOrModel() {
        classes()
                .that().resideInAPackage("..controller..")
                .should().onlyDependOnClassesThat()
                .resideInAnyPackage("..service..", "..model..", "java..", "javax..", "org.springframework..")
                .because("контролери повинні залежати тільки від сервісів, моделей або стандартних бібліотек")
                .check(applicationClasses);
    }
    // Сервіси можуть залежати лише від моделей, репозиторіїв та утиліт
    @Test
    void servicesShouldOnlyDependOnRepositoryModelAndUtils() {
        classes()
                .that().resideInAPackage("..service..")
                .should().onlyDependOnClassesThat()
                .resideInAnyPackage("..repository..", "..model..", "java..", "javax..", "org.springframework..")
                .because("сервіси повинні залежати від репозиторіїв, моделей або утиліт")
                .check(applicationClasses);
    }
    // Поля моделей мають бути private або protected
    @Test
    void fieldsInModelShouldBePrivateOrProtected() {
        fields()
                .that().areDeclaredInClassesThat().resideInAPackage("..model..")
                .should().bePrivate().orShould().beProtected()
                .because("інкапсуляція в моделях важлива")
                .check(applicationClasses);
    }

    // Контролери не повинні містити бізнес-логіку
    @Test
    void controllerClassesShouldNotHaveBusinessLogicMethods() {
        methods()
                .that().areDeclaredInClassesThat().resideInAPackage("..controller..")
                .should(not(haveNameMatching(".*process.*|.*calculate.*|.*validate.*")))
                .because("контролери не повинні містити бізнес-логіку")
                .check(applicationClasses);
    }

    // Моделі не повинні залежати від сервісів чи репозиторіїв
    @Test
    void modelShouldNotDependOnServiceOrRepository() {
        noClasses()
                .that().resideInAPackage("..model..")
                .should().dependOnClassesThat().resideInAnyPackage("..service..", "..repository..")
                .because("моделі не повинні залежати від сервісів або репозиторіїв")
                .check(applicationClasses);
    }


    // Сервіси не повинні звертатись до контролерів
    @Test
    void serviceClassesShouldNotAccessControllers() {
        noClasses()
                .that().resideInAPackage("..service..")
                .should().dependOnClassesThat().resideInAPackage("..controller..")
                .because("сервіси не повинні залежати від контролерів")
                .check(applicationClasses);
    }

    // Контролери мають мати анотацію @RequestMapping або @RestController
    @Test
    void controllersShouldHaveRequestMapping() {
        classes()
                .that().resideInAPackage("..controller..")
                .should().beAnnotatedWith(RequestMapping.class)
                .orShould().beAnnotatedWith(RestController.class)
                .because("мають бути REST контролери або мати мапінг")
                .check(applicationClasses);
    }

    // Кожна модель має бути анотована як @Document або @Entity
    @Test
    void modelClassesShouldBeAnnotatedWithDocumentOrEntity() {
        ArchCondition<JavaClass> beAnnotatedWithDocumentOrEntity = new ArchCondition<>("бути анотованим @Document або @Entity") {
            @Override
            public void check(JavaClass javaClass, ConditionEvents events) {
                boolean isDocument = javaClass.isAnnotatedWith(Document.class);
                boolean isEntity = javaClass.isAnnotatedWith(String.valueOf(Entity.class));
                boolean isValid = isDocument || isEntity;

                String message = isValid
                        ? String.format("✅ %s правильно анотовано", javaClass.getName())
                        : String.format("❌ %s не має анотації @Document або @Entity", javaClass.getName());

                events.add(new SimpleConditionEvent(javaClass, isValid, message));
            }
        };

        classes()
                .that().resideInAPackage("..model..")
                .should(beAnnotatedWithDocumentOrEntity)
                .because("модель має бути анотована для бази даних як @Document або @Entity")
                .check(applicationClasses);
    }
    // Методи контролера мають мати мапінг (Get, Post тощо)
    @Test
    void controllerMethodsShouldBeMapped() {
        methods()
                .that().areDeclaredInClassesThat().resideInAPackage("..controller..")
                .should().beAnnotatedWith(RequestMapping.class)
                .orShould().beAnnotatedWith(GetMapping.class)
                .orShould().beAnnotatedWith(PostMapping.class)
                .orShould().beAnnotatedWith(DeleteMapping.class)
                .orShould().beAnnotatedWith(PutMapping.class)
                .because("методи контролера мають мати мапінг")
                .check(applicationClasses);
    }
    // Заборонено використовувати @Autowired на полях
    @Test
    void autowiredShouldNotBeUsedOnFieldsInAnyPackage() {
        noFields()
                .should().beAnnotatedWith(Autowired.class)
                .because("використання @Autowired на полях не рекомендується")
                .check(applicationClasses);
    }


    // Контролер не повинен напряму викликати репозиторій
    @Test
    void controllerShouldNotCallRepositoryDirectly() {
        noClasses()
                .that().resideInAPackage("..controller..")
                .should().dependOnClassesThat().resideInAPackage("..repository..")
                .because("контролери не повинні напряму звертатися до репозиторіїв")
                .check(applicationClasses);
    }


    // Сервіси мають мати лише один конструктор
    @Test
    void serviceShouldHaveOnlyOneConstructor() {
        ArchCondition<JavaClass> haveOnlyOneConstructor = new ArchCondition<>("мати лише один конструктор") {
            @Override
            public void check(JavaClass javaClass, ConditionEvents events) {
                int constructorCount = javaClass.getConstructors().size();
                boolean isValid = constructorCount == 1;

                String message = String.format("Клас %s має %d конструктор(ів)", javaClass.getName(), constructorCount);

                events.add(new SimpleConditionEvent(javaClass, isValid, message));
            }
        };

        classes()
                .that().resideInAPackage("..service..")
                .should(haveOnlyOneConstructor)
                .because("рекомендується мати лише один конструктор для залежностей через DI")
                .check(applicationClasses);
    }

    // У контролері не має бути поля для логера
    @Test
    void controllerClassShouldNotContainLoggerField() {
        noFields()
                .that().areDeclaredInClassesThat().resideInAPackage("..controller..")
                .should().haveRawType(org.slf4j.Logger.class)
                .because("логування має бути централізованим")
                .check(applicationClasses);
    }


    // Моделі не повинні викидати або декларувати винятки
    @Test
    void modelClassesShouldNotDeclareExceptions() {
        ArchCondition<JavaMethod> notDeclareExceptions = new ArchCondition<>("not declare exceptions") {
            @Override
            public void check(JavaMethod method, ConditionEvents events) {
                if (!method.getExceptionTypes().isEmpty()) {
                    String message = String.format("Метод %s декларує виключення: %s",
                            method.getFullName(), method.getExceptionTypes());
                    events.add(SimpleConditionEvent.violated(method, message));
                }
            }
        };

        methods()
                .that().areDeclaredInClassesThat().resideInAPackage("..model..")
                .should(notDeclareExceptions)
                .because("модельні класи не повинні декларувати виключення")
                .check(applicationClasses);
    }


    // Перевірка на відсутність циклічних залежностей між шарами
    @Test
    void noCircularDependencies() {
        layeredArchitecture()
                .consideringAllDependencies()
                .layer("Controller").definedBy("..controller..")
                .layer("Service").definedBy("..service..")
                .layer("Repository").definedBy("..repository..")
                .whereLayer("Service").mayOnlyBeAccessedByLayers("Controller")
                .whereLayer("Repository").mayOnlyBeAccessedByLayers("Service")
                .check(applicationClasses);
    }

    // Застереження: не використовувати застарілі класи
    @Test
    void noDeprecatedClassesShouldBeUsed() {
        noClasses()
                .should().dependOnClassesThat().areAnnotatedWith(Deprecated.class)
                .because("не слід використовувати застарілі класи")
                .check(applicationClasses);
    }

}