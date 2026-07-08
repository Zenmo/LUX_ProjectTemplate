import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.filter.ThresholdFilter;
import org.apache.logging.log4j.core.filter.CompositeFilter;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.Layout;
import org.slf4j.LoggerFactory;
import java.util.Map;

import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.builder.api.LayoutComponentBuilder;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import org.apache.logging.log4j.core.config.builder.api.FilterComponentBuilder;

/**
 * This class sets up Log4j2 to log to the console.
 * 
 * AnyLogic ships with SLF4J 1.7.25 (a logging standard) 
 * and Log4j 2.26.0 (a logging implementation).
 * 
 * We add log4j-slf4j-impl 2.26.0 to connect them.
 * 
 * AnyLogic IDE uses Eclipse to connect log4j and slf4j.
 * But this mechanism is not made available to the simulation model.
 * 
 * You can get a logger instance through a org.slf4j.LoggerFactory
 * This is usually done once per class/agent:
 * 
 * import org.slf4j.Logger;
 * import org.slf4j.LoggerFactory;
 * 
 * class MyClass {
 *     private static final Logger logger = LoggerFactory.getLogger(MyClass.class);
 *     
 *     MyClass(int id) {
 *         logger.info("Created an instance of MyClass with id {}", id);
 *     }
 * }
 * 
 * Available levels are trace, debug, info, warn and error.
 * 
 * More info is at https://www.slf4j.org/manual.html
 */
public final class LuxLogging {
	/**
	 * Messages below this log level will not be displayed.
	 */
    private static final Level DEFAULT_LEVEL = Level.INFO;
    
    /**
     * Override log level for specific packages or classes
     */
    private static final Map<String, Level> LEVELS = Map.of(
    		"lux_projecttemplate", Level.INFO,
    		"energy.lux", Level.INFO,
    		"zero_engine", Level.INFO,
    		"zerointerfaceloader", Level.INFO,
    		"digital_twin_results", Level.INFO,
    		// AnyLogic internals
    		"org.eclipse.jetty", Level.WARN,
    		"spark", Level.WARN
    );
    
    /**
     * Internal constants
     */
    private static final String STDOUT_APPENDER_NAME = "LuxStdout";
    private static final String STDERR_APPENDER_NAME = "LuxStderr";

    private LuxLogging() {
    }
    
    /**
     * This should be called as early as possible.
     * 
     * For example inside 
     * {@link com.anylogic.engine.Agent#onBeforeCreate() onBeforeCreate()} 
     * of the root agent or inside a static block.
     */
    public static void configure() {
    	// Don't use the builder because it's a less type safe API.
    	ConfigurationBuilder<BuiltConfiguration> configBuilder = ConfigurationBuilderFactory.newConfigurationBuilder();
    	BuiltConfiguration configuration = configBuilder.build();
    	
    	// remove default appenders
    	configuration.getRootLogger().getAppenders().keySet().forEach(appenderName ->
            configuration.getRootLogger().removeAppender(appenderName)
        );
    	
    	configuration.getRootLogger().setLevel(DEFAULT_LEVEL);

    	PatternLayout layout = createPatternLayout();

    	ConsoleAppender stdoutAppender = createStdoutAppender(layout);    
    	configuration.addAppender(stdoutAppender);
    	configuration.getRootLogger().addAppender(stdoutAppender, Level.ALL, null);
    	
    	ConsoleAppender stderrAppender = createStderrAppender(layout);
    	configuration.addAppender(stdoutAppender);
    	configuration.getRootLogger().addAppender(stderrAppender, Level.WARN, null);

    	for (Map.Entry<String, Level> entry : LEVELS.entrySet()) {
    		addSubLogger(entry.getKey(), entry.getValue(), configuration);
    	}
    	
    	addGrayAppender(configuration);

    	Configurator.reconfigure(configuration);
    	
    	// We can do a reload if the configuration was done too late
    	//LoggerContext context = (LoggerContext) LogManager.getContext(false);
    	//context.updateLoggers();
    }
    
    private static void addSubLogger(String packageOrClass, Level level, Configuration configuration) {
    	var loggerConfig = LoggerConfig.newBuilder()
	        .withLoggerName(packageOrClass)
	        .withLevel(level)
	        .withAdditivity(true)
	        .withConfig(configuration)
	        .build();
    	
    	configuration.addLogger(packageOrClass, loggerConfig);
    }
    
    private static ConsoleAppender createStdoutAppender(PatternLayout layout) {
    	var stdoutAppender = ConsoleAppender.newBuilder()
    	        .setName(STDOUT_APPENDER_NAME)
    	        .setTarget(ConsoleAppender.Target.SYSTEM_OUT)
    	        .setLayout(layout)
    	        .setFilter(ThresholdFilter.createFilter(
    	    	        Level.WARN,
    	    	        Filter.Result.DENY,
    	    	        Filter.Result.NEUTRAL
    	    	))
    	        .build();
    	stdoutAppender.start();
    	return stdoutAppender;
    }
    
    private static ConsoleAppender createStderrAppender(PatternLayout layout) {
    	var appender = ConsoleAppender.newBuilder()
    	        .setName(STDERR_APPENDER_NAME)
    	        .setTarget(ConsoleAppender.Target.SYSTEM_ERR)
    	        .setLayout(layout)
    	        .setFilter(ThresholdFilter.createFilter(
    	                Level.WARN,
    	                Filter.Result.ACCEPT,
    	                Filter.Result.DENY
    	        ))
    	        .build();
    	appender.start();
    	return appender;
    }
    
    /**
     * Print debug messages in gray.
     * Bit of a gimmick.
     */
    private static void addGrayAppender(Configuration configuration) {
    	var layout = configuration.getAppender(STDOUT_APPENDER_NAME).getLayout();
    			
    	GrayConsoleAppender grayAppender = new GrayConsoleAppender(
    	        "GRAY_APPENDER",
    	        null,
    	        layout
    	);
    	grayAppender.addFilter(ThresholdFilter.createFilter(Level.INFO, Filter.Result.DENY, Filter.Result.NEUTRAL));
        configuration.addAppender(grayAppender);
        
        // Just adding it to the root logger seems sufficient to add it to all loggers
        configuration.getRootLogger().addAppender(grayAppender, Level.ALL, null);
        /*for (var loggerConfig: configuration.getLoggers().values()) {
        	loggerConfig.addAppender(grayAppender, Level.DEBUG, null);
        }*/
        
        var stdOutAppender = (AbstractAppender) configuration.getAppender(STDOUT_APPENDER_NAME);
        stdOutAppender.addFilter(
        		CompositeFilter.createFilters(new Filter[] {
        				ThresholdFilter.createFilter(Level.INFO, Filter.Result.ACCEPT, Filter.Result.DENY),
        				stdOutAppender.getFilter()
        		})
        );
    }
    
    private static PatternLayout createPatternLayout() {
    	return PatternLayout.newBuilder()
			// Use "ss.SSS" for sub-second precision
	        .withPattern("%d{yyyy-MM-dd'T'HH:mm:ssXXX} %-5level %logger{1.} - %msg%n")
	        //.withConfiguration(configuration)
	        .build();
    }
}
