package org.example.employee;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.Service;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardVirtualThreadExecutor;
import org.apache.coyote.http11.Http11NioProtocol;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.stereotype.Component;

//@Component
public class MyTomcatWebServerFactoryCustomizer implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {

    @Override
    public void customize(final TomcatServletWebServerFactory factory) {

//        factory.addConnectorCustomizers(connector -> connector.setAllowTrace(false));

        final var protocol = new Http11NioProtocol();
//        protocol.setKeepAliveTimeout(3_000);
//        protocol.setAcceptCount(200);
        final var executor = new StandardVirtualThreadExecutor();
        executor.setName(StandardVirtualThreadExecutor.class.getSimpleName());
        try {
            executor.start();
        } catch (LifecycleException e) {
            throw new RuntimeException(e);
        }
        protocol.setExecutor(executor);
        final var connector = new Connector(protocol);
        connector.setPort(8040);
        factory.addAdditionalTomcatConnectors(connector);

//        factory.addConnectorCustomizers(customizer -> customizer.setpr);
//        var counter = 0;
//        for (var tomcatConnectorCustomizer : factory.getTomcatConnectorCustomizers()) {
//            counter++;
//            tomcatConnectorCustomizer.customize((connector) -> connector.);
//        }
//        var blah = counter;
//        final Service[] service = new Service[1];
//        StandardVirtualThreadExecutor standardVirtualThreadExecutor;
//        factory.
//        factory.addAdditionalTomcatConnectors();
//        factory.addConnectorCustomizers(connector -> service[0] = connector.getService());
    }
}
