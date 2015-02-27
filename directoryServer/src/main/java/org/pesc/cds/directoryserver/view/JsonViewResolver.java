package org.pesc.cds.directoryserver.view;

import java.sql.Timestamp;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.module.SimpleModule;
import org.codehaus.jackson.map.ser.CustomSerializerFactory;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.json.MappingJacksonJsonView;

/**
 * This JSON view resolver is used in the Thymeleaf view resolver, which is a 
 * content negotiation view resolver. It can resolve what type of output (view)
 * to use based on what the request requested. So this would be one view resolver
 * in a list of view resolvers. This class also uses the JSONMapper class.
 * @author Wes Owen
 *
 */
public class JsonViewResolver implements ViewResolver {
	private static final Log log = LogFactory.getLog(JsonViewResolver.class);
	
	private static View buildJsonView() {
		MappingJacksonJsonView view = new MappingJacksonJsonView();
		view.setPrettyPrint(true);
		
		JSONMapper jmap = new JSONMapper();
		
		CustomSerializerFactory factory = new CustomSerializerFactory();
		factory.addSpecificMapping(Timestamp.class, new JsonTimestampSerializer());
		jmap.setSerializerFactory(factory);
		
		SimpleModule mod = new SimpleModule("JsonTimestampDeserializer", new Version(1, 0, 0, null));
		mod.addDeserializer(Timestamp.class, new JsonTimestampDeserializer());
		jmap.registerModule(mod);
		
		jmap.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		jmap.configure(SerializationConfig.Feature.INDENT_OUTPUT, true);
		jmap.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, true);
		jmap.setDateFormat(null);
		
		view.setObjectMapper(jmap);
		return view;
	}
	
	
	public View resolveViewName(String viewName, Locale locale) throws Exception {
		return JsonViewResolver.createStandardJsonView();
	}
	
	public static View createStandardJsonView() {
		return buildJsonView();
	}
}
