package com.b2wdigital.grails.plugin.gormlogicaldelete

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification

@TestFor(TestService)
@Mock([TestDomain,TestDomainCustom])
class TestServiceSpec extends Specification {

    def setup() {
        DomainClassEnhancer.enhance(grailsApplication.domainClasses)
        grailsApplication.mainContext.addApplicationListener(new PreQueryListener())
    }

    void "saving annotated domain"() {
        given: "I have 3 non-deleted domains"
            createTestDomains(3)

        when: "Save on a new domain is called"
            service.saveTestDomain("test name")

        then: "I should have 4 non-deleted domains"
             TestDomain.list().size() == 4
            !TestDomain.findByName("test name").deleted == !false
    }

    void "deleting annotated domain"() {
        given: "I have non-deleted domains"
            createTestDomains(3)

        when: "I delete one domain"
            service.deleteTestDomain(TestDomain.first())

        then: "I should have X non-deleted domains, and X+1 domains overall"
            TestDomain.findAll().size()+1 == withDeletedTestDomainCount(TestDomain)
    }

    void "custom property domain"() {
        given: "I have non-deleted domains"
            createTestDomainsCustom(3)

        when: "I delete one domain"
            service.deleteTestDomainCustom(TestDomainCustom.first())

        then: "I should have X non-deleted domains, and X+1 domains overall"
            TestDomainCustom.findAll().size()+1 == withDeletedTestDomainCount(TestDomainCustom)
    }

    private int withDeletedTestDomainCount(Class<?> clazz) {
        def count
        TestDomain.withDeleted {
             count = clazz.findAll().size()
        }
        return count
    }

    private void createTestDomains(int qty) {
        qty.times {
            service.saveTestDomain("name " + it)
        }
    }

    private void createTestDomainsCustom(int qty) {
        qty.times {
            service.saveTestDomainCustom("name " + it)
        }
    }
}