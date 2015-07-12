package nebhistory.config;

import java.util.EnumSet;

import javax.inject.Inject;
import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import nebhistory.controller.HistoryMessageController;
import nebhistory.filter.CORSFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.MimeMappings;
import org.springframework.boot.context.embedded.ServletContextInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class WebConfigurer implements ServletContextInitializer, EmbeddedServletContainerCustomizer {

	private static final Logger LOGGER = LoggerFactory.getLogger(WebConfigurer.class);

    @Inject
    private Environment env;

    @Override
    public void customize(ConfigurableEmbeddedServletContainer container) {
        MimeMappings mappings = new MimeMappings(MimeMappings.DEFAULT);
        mappings.add("html", "text/html;charset=utf-8");
        mappings.add("json", "text/html;charset=utf-8");
        container.setMimeMappings(mappings);
    }

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
        EnumSet<DispatcherType> disps = EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.ASYNC);        
        initCorsFilter(servletContext, disps);
		
	}
	
    private void initCorsFilter(ServletContext servletContext, EnumSet<DispatcherType> disps) {
        LOGGER.debug("Registering CORS Filter");
        FilterRegistration.Dynamic corsFilter = servletContext.addFilter("corsFilter", CORSFilter.class);
        corsFilter.addMappingForUrlPatterns(null, false, "/*");
    }  	
}
