package grails.boot

import grails.artefact.Artefact
import grails.boot.config.GrailsConfiguration
import org.springframework.boot.context.embedded.AnnotationConfigEmbeddedWebApplicationContext
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import spock.lang.Specification

/**
 * Created by graemerocher on 28/05/14.
 */
class EmbeddedContainerWithGrailsSpec extends Specification {

    AnnotationConfigEmbeddedWebApplicationContext context

    void cleanup() {
        context.close()
    }

    void "Test that you can load Grails in an embedded server config"() {
        when:"An embedded server config is created"
            this.context = new AnnotationConfigEmbeddedWebApplicationContext(Application)

        then:"The context is valid"
            context != null
            new URL("http://localhost:${context.embeddedServletContainer.port}/foo/bar").text == 'hello world'
            new URL("http://localhost:${context.embeddedServletContainer.port}/foos").text == 'all foos'
    }

    @Configuration
    static class Application extends GrailsConfiguration {
        @Override
        Collection<Class> classes() {
            [FooController, UrlMappings]
        }
        @Bean
        public EmbeddedServletContainerFactory containerFactory() {
            return new TomcatEmbeddedServletContainerFactory(0);
        }
    }

}

@Artefact("Controller")
class FooController {
    def bar() {
        render "hello world"
    }
    def list() {
        render "all foos"
    }
}

class UrlMappings {
    static mappings = {
        "/$controller/$action?/$id?(.$format)?"()
        "/foos"(controller:'foo', action:"list")
    }
}
