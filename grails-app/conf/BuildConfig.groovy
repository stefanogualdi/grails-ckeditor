grails.project.work.dir = "target"

grails.project.target.level = 1.6
grails.project.source.level = 1.6

grails.project.dependency.resolution = {
    inherits "global" // inherit Grails' default dependencies
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    repositories {
        grailsPlugins()
        grailsHome()
        grailsCentral()

        mavenLocal()
        mavenCentral()
    }

    plugins {
        build ":tomcat:$grailsVersion"
        build ':release:2.2.1', ':rest-client-builder:1.0.3', {
            export = false
        }
    }

    dependencies {
    }

}
