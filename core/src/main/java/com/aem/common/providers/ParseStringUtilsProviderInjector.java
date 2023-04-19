package com.aem.common.providers;

import com.aem.common.annotations.ParseStringUtils;
import com.aem.common.utils.JCRUtils;
import com.drew.lang.annotations.NotNull;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.spi.DisposalCallbackRegistry;
import org.apache.sling.models.spi.Injector;
import org.apache.sling.models.spi.injectorspecific.InjectAnnotationProcessor2;
import org.apache.sling.models.spi.injectorspecific.StaticInjectAnnotationProcessorFactory;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;

import javax.jcr.Node;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;
import java.util.Optional;

@Component(
	immediate = true, service = {Injector.class, StaticInjectAnnotationProcessorFactory.class},
	property = {Constants.SERVICE_RANKING + ":Integer=7000"})
public class ParseStringUtilsProviderInjector implements Injector, StaticInjectAnnotationProcessorFactory {

	static final String NAME = "page-parsestringutils-provider-injector";

	@Override
	public @NotNull String getName() {
		return NAME;
	}

	@Override
	public Object getValue(@NotNull Object adaptable, String fieldName, @NotNull Type type, @NotNull AnnotatedElement annotatedElement, @NotNull DisposalCallbackRegistry disposalCallbackRegistry) {
		if (adaptable instanceof SlingHttpServletRequest && annotatedElement.isAnnotationPresent(ParseStringUtils.class)) {
			ParseStringUtils annotation = annotatedElement.getAnnotation(ParseStringUtils.class);
			String propertyName = annotation.value().isEmpty() ? fieldName : annotation.value();

			Optional<String> optional = Optional.of((SlingHttpServletRequest) adaptable)
				.map(SlingHttpServletRequest::getResource)
				.map(resource -> resource.adaptTo(Node.class))
				.map(node -> JCRUtils.getValue(node, propertyName))
				.map(String::trim);

			return optional.orElse("");
		}

		return "";
	}

	@Override
	public InjectAnnotationProcessor2 createAnnotationProcessor(AnnotatedElement element) {
		ParseStringUtils annotation = element.getAnnotation(ParseStringUtils.class);
		if (annotation != null) {
			return new RequestedPageMetadataProviderAnnotationProcessor();
		}
		return null;
	}
}
