package com.aem.core.models;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;

import com.aem.common.annotations.ParseStringUtils;

import lombok.Getter;
import lombok.NonNull;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Model(adaptables = SlingHttpServletRequest.class,
	adapters = {ComponentExporter.class},
	resourceType = Text.RESOURCE_TYPE,
	defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
	extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class Text extends ReusableFields implements ComponentExporter {

	static final String RESOURCE_TYPE = "aem/components/content/text";

	@ParseStringUtils
	private String richtext;

	@NonNull
	@Override
	public String getExportedType() {
		return RESOURCE_TYPE;
	}
}