package br.com.ufcg.splab.recsys.umlexperiment.web;

import java.util.Locale;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import br.com.ufcg.splab.recsys.umlexperiment.web.filter.CorsFilter;

@SpringBootApplication
@ComponentScan(basePackages = { "br.com.ufcg.splab.recsys.umlexperiment.web" })
public class Application extends SpringBootServletInitializer
{
    /**
     * A method to start the application.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args)
    {
        SpringApplication.run(Application.class, args);
    }

    /**
     * Configure the application.
     *
     * @param application A builder for the application context.
     * @return The application builder
     * @see SpringApplicationBuilder
     */
    @Override
    protected SpringApplicationBuilder configure(
        SpringApplicationBuilder application)
    {
        return application.sources(Application.class);
    }

    /**
     * Registers the CorsFilter bean.
     *
     * @return A CorsFilter instance.
     */
    @Bean
    public CorsFilter corsFilter()
    {
        return new CorsFilter();
    }

    @Bean
    public LocaleResolver localeResolver()
    {
        SessionLocaleResolver slr = new SessionLocaleResolver();
        slr.setDefaultLocale(new Locale("pt", "BR"));
        return slr;
    }

    @Bean
    public ServerProperties getServerProperties()
    {
        return new ServerCustomization();
    }
}
