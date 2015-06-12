package com.b2wdigital.grails.plugin.gormlogicaldelete

import org.grails.datastore.mapping.query.event.PreQueryEvent
import org.springframework.context.ApplicationListener

class PreQueryListener implements ApplicationListener<PreQueryEvent> {

    @Override
    void onApplicationEvent(PreQueryEvent event) {
        def domainClass = event.query.entity.javaClass
        if(DomainClassEnhancer.classProperties[domainClass]) {
            if(!event.query.session.getSessionProperty(DomainClassEnhancer.PHYSICAL_SESSION)) {
                event.query.eq(
                        DomainClassEnhancer.classProperties[domainClass].property,
                        !DomainClassEnhancer.classProperties[domainClass].deletedState
                )
            }
        }
    }
}