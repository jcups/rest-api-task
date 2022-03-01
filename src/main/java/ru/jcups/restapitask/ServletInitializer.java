package ru.jcups.restapitask;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.SessionTrackingMode;
import java.util.EnumSet;

public class ServletInitializer extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        logger.info("ServletInitializer.configure");
        logger.info("configure() called with: application = [" + application + "]");
        return application.sources(RestApiTaskApplication.class);
    }

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        logger.info("ServletInitializer.onStartup");
        logger.info("onStartup() called with: servletContext = [" + servletContext + "]");
        servletContext.setSessionTrackingModes(EnumSet.of(SessionTrackingMode.COOKIE));
        servletContext.getSessionCookieConfig().setHttpOnly(true);
//        servletContext.getSessionCookieConfig().setSecure(true);
    }
}
