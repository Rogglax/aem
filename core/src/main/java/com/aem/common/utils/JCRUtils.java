package com.aem.common.utils;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.WCMMode;
import com.day.cq.commons.jcr.JcrConstants;
import com.google.common.collect.Lists;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import lombok.extern.slf4j.Slf4j;

import javax.jcr.*;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;
import javax.jcr.query.QueryManager;
import java.util.List;
import java.util.Objects;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.text.SimpleDateFormat;

import static com.day.cq.tagging.TagConstants.PN_TAGS;
import static com.day.cq.wcm.api.NameConstants.NT_PAGE;
import static com.day.cq.commons.jcr.JcrConstants.JCR_DESCRIPTION;
import static com.day.cq.commons.jcr.JcrConstants.JCR_PRIMARYTYPE;

@Slf4j
public class JCRUtils {

	private JCRUtils(){}

	public static final String LOG_MESSAGE = "JCRUtils. {}";
	public static final Integer PORTO_STORE_DEFAULT_ID = 1;

	public static String getPropertyFromNode(Node node, String propertyName) {
		try {
			return node.hasProperty(propertyName) && node.getProperty(propertyName) != null
				? node.getProperty(propertyName).getValue().toString()
				: StringUtils.EMPTY;
		} catch (RepositoryException e) {
			log.error(LOG_MESSAGE, e.getMessage());
			return StringUtils.EMPTY;
		}
	}

	public static Property getPropertyNode(Node node, String propertyName) {
		try {
			return node.hasProperty(propertyName) && node.getProperty(propertyName) != null
				? node.getProperty(propertyName)
				: null;
		} catch (RepositoryException e) {
			log.error(LOG_MESSAGE, e.getMessage());
			return null;
		}
	}

	public static String getResourcePrimaryType(final Resource resource) {
		return resource.getValueMap().get(JCR_PRIMARYTYPE) == null ? null
			: resource.getValueMap().get(JCR_PRIMARYTYPE).toString();
	}

	public static boolean resourceIsJcrContentNode(final Resource resource) {
		return StringUtils.equals(getResourcePrimaryType(resource), "cq:PageContent");
	}

	public static boolean checkIsJcrContentNodeFromResource(final Resource resource) {
		Node node = resource.adaptTo(Node.class);
		try {
			return Objects.requireNonNull(node).isNodeType("cq:PageContent");
		} catch (RepositoryException e) {
			log.error(LOG_MESSAGE, e.getMessage());
			return false;
		}
	}

	public static boolean checkNoIndex(final Resource resource) {
		ValueMap valueMap = resource.getValueMap();
		if (valueMap.containsKey("noindex")) {
			Object object = valueMap.get("noindex", Boolean.class);
			return object != null && (boolean) object;
		} else {
			return false;
		}
	}

	public static boolean iScqPage(final Resource resource) {
		String primaryType = resource.getValueMap().get(JCR_PRIMARYTYPE) == null ? null
			: resource.getValueMap().get(JCR_PRIMARYTYPE).toString();
		if (StringUtils.isBlank(primaryType)) {
			return false;
		} else {
			return primaryType.equals(NT_PAGE);
		}
	}

	public static NodeIterator getNodeIteratorFromQuery(
		final ResourceResolver resourceResolver, final String queryStr,
		final String queryType) throws RepositoryException {

		return getNodeIteratorFromQuery(resourceResolver, queryStr, queryType, null);
	}

	public static NodeIterator getNodeIteratorFromQuery(
		final ResourceResolver resourceResolver, final String queryStr,
		final String queryType, final Long limit) throws RepositoryException {

		Session session = resourceResolver.adaptTo(Session.class);
		QueryManager queryManager = Objects.requireNonNull(session).getWorkspace().getQueryManager();
		Query query = queryManager.createQuery(queryStr, queryType);

		if (limit != null) {
			query.setLimit(limit);
		}

		QueryResult queryResult = query.execute();
		return queryResult.getNodes();
	}

	public static String getPageLastModifiedDate(Page page) {
		SimpleDateFormat outputDf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return outputDf.format(page.getLastModified().getTime());
	}

	public static String getPageSection(Page page) {
		return page.getProperties().get("section") == null ? ""
			: page.getProperties().get("section").toString();
	}

	public static String getSearchResulImageAlt(Page page) {
		return page.getProperties().get("page_image_alt") == null ? ""
			: page.getProperties().get("page_image_alt").toString();
	}

	public static String getPageContentSubTitle(Page page) {
		return page.getProperties().get(JCR_DESCRIPTION) == null ? ""
			: page.getProperties().get(JCR_DESCRIPTION).toString();
	}

	public static String[] getPageTagsArray(Page page) {
		String[] list = page.getProperties().get(PN_TAGS) == null ? new String[0]
			: (String[]) page.getProperties().get(PN_TAGS);
		return (list == null) ? new String[0] : list;
	}

	public static String getPageButtonTarget(Page page) {
		return page.getProperties().get("button_target") == null ? ""
			: page.getProperties().get("button_target").toString();
	}

	public static String getPageButtonText(Page page) {
		return page.getProperties().get("button_text") == null ? ""
			: page.getProperties().get("button_text").toString();
	}

	public static String getPageButtonUrl(Page page) {
		return page.getProperties().get("button_url") == null ? ""
			: page.getProperties().get("button_url").toString();
	}

	public static Iterator<Resource> getIteratorFromResource(Resource resource, String propertyName) {
		try {
			Resource list = resource.getChild(propertyName);
			if (list != null) {
				Iterable<Resource> iterable = list.getChildren();
				return iterable.iterator();
			} else {
				return null;
			}
		} catch (RuntimeException e) {
			log.error(LOG_MESSAGE, e.getMessage());
			return null;
		}
	}

	public static boolean getPropertyBooleanFromNode(Node node, String propertyName) {
		try {
			return (node.hasProperty(propertyName) && node.getProperty(propertyName) != null)
				&& node.getProperty(propertyName).getBoolean();
		} catch (RepositoryException e) {
			log.error(LOG_MESSAGE, e.getMessage());
			return false;
		}
	}

	public static Page findRoot(Page resourcePage) {
		Page rootPage = resourcePage;
		while ((rootPage != null) && (!isRoot(rootPage))) {
			rootPage = rootPage.getParent();
		}
		return rootPage;
	}

	public static boolean isRoot(Page page) {
		Resource res = page.getContentResource();
		if (res == null) return false;
		boolean isRoot = false;
		ValueMap vm = res.adaptTo(ValueMap.class);
		Object object = vm != null ? vm.get("navRoot", Boolean.class) : null;
		isRoot = object != null && (boolean) object;
		return isRoot;
	}

	public static boolean getBooleanPropertyFromPage(Page page, String propertyName) {
		if (StringUtils.isBlank(propertyName)) return false;
		Resource res = page.getContentResource();
		if (res == null) return false;
		boolean value = false;
		ValueMap vm = res.adaptTo(ValueMap.class);
		Object object = vm != null ? vm.get(propertyName, Boolean.class) : null;
		value = object != null && (boolean) object;
		return value;
	}

	public static boolean isAuthorInstanceFromSlingModel(SlingHttpServletRequest request) {
		WCMMode wcmMode = WCMMode.fromRequest(request);
		int port = request.getServerPort();
		return (port == 4502) || (wcmMode.equals(WCMMode.ANALYTICS) || wcmMode.equals(WCMMode.EDIT)
			|| wcmMode.equals(WCMMode.PREVIEW) || wcmMode.equals(WCMMode.DESIGN));
	}

	public static boolean getBooleanPropertyFromResource(final Resource resource, final String propertyName) {
		try {
			if (StringUtils.isBlank(propertyName)) return false;
			if (resource.getValueMap().get(propertyName) == null) {
				return false;
			} else {
				Object object = resource.getValueMap().get(propertyName, Boolean.class);
				return object != null && (boolean) object;
			}
		} catch (ClassCastException e) {
			log.error(LOG_MESSAGE, e.getMessage());
			return false;
		}
	}

	public static String getPropertyFromResource(final Resource resource, final String propertyName) {
		try {
			if (StringUtils.isBlank(propertyName)) return null;
			return resource.getValueMap().get(propertyName) == null ? null
				: resource.getValueMap().get(propertyName, String.class);
		} catch (ClassCastException e) {
			log.error(LOG_MESSAGE, e.getMessage());
			return null;
		}
	}

	public static String getNodeName(Node node) {
		try {
			return node.getName();
		} catch (RepositoryException ex) {
			log.error(LOG_MESSAGE, ex.getMessage());
		}
		return "";
	}

	public static Property getProperty(Node node, String propertyName) {
		try {
			if (node.hasProperty(propertyName)) {
				return node.getProperty(propertyName);
			}
		} catch (RepositoryException repositoryException) {
			log.error(LOG_MESSAGE, repositoryException.getMessage());
		}
		return null;
	}

	public static String getValue(Property property) {
		try {
			if (property != null) {
				return property.getValue().getString();
			}
		} catch (RepositoryException repositoryException) {
			log.error(LOG_MESSAGE, repositoryException.getMessage());
		}
		return null;
	}

	public static String getValue(Node node, String attributeName) {
		try {
			if (node != null && node.hasProperty(attributeName)) {
				Property property = node.getProperty(attributeName);
				if (property != null) {
					return property.getValue().getString().trim();
				}
			}
		} catch (IllegalStateException | RepositoryException e) {
			log.error(LOG_MESSAGE, e.getMessage());
		}
		return "";
	}

	public static String getValueWithReplaceSpace(Node node, String attributeName) {
		return getValue(node, attributeName).replace(" ", "");
	}

	public static List<String> getValues(Node node, String attributeName) {
		List<String> values = Lists.newArrayList();
		try {
			if (node != null && node.hasProperty(attributeName)) {
				Property property = node.getProperty(attributeName);
				if (property != null) {
					if (property.isMultiple()) {
						for (Value value : property.getValues()) {
							values.add(value.getString());
						}
					} else {
						values.add(property.getValue().getString());
					}
				}
			}
		} catch (RepositoryException e) {
			log.error(LOG_MESSAGE, e.getMessage());
		}
		return values;
	}

	public static LinkedHashMap<String, String> getChildNodesValuePair(Node parent, String attribute, String key, String value) {
		LinkedHashMap<String, String> map = new LinkedHashMap<>();

		try {
			if (parent != null && parent.hasNode(attribute)) {
				Node data = parent.getNode(attribute);
				NodeIterator i = data.getNodes();

				while (i.hasNext()) {
					Node child = i.nextNode();
					map.put(
						child.hasProperty(key) ? JCRUtils.getValueWithReplaceSpace(child, key) : "",
						child.hasProperty(value) ? JCRUtils.getValueWithReplaceSpace(child, value) : "");
				}
			}
		} catch (IllegalStateException | RepositoryException e) {
			log.error(LOG_MESSAGE, e.getMessage());
		}

		return map;
	}

	public static Integer getID(Page page, String property) {
		Resource pageResource = page.adaptTo(Resource.class);
		try {
			if (pageResource != null) {
				Resource jcrContent = pageResource.getChild(JcrConstants.JCR_CONTENT);
				if (jcrContent == null) {
					log.debug("Header.getID() - jcrContent == null : returning default id");
					return PORTO_STORE_DEFAULT_ID;
				}
				String id = jcrContent.getValueMap().get(property) == null ? null : jcrContent.getValueMap().get(property).toString();
				return (StringUtils.isBlank(id) ? PORTO_STORE_DEFAULT_ID : Integer.valueOf(id));
			}
			log.debug("Header.getID() - pageResource == null : returning default id");
			return PORTO_STORE_DEFAULT_ID;
		} catch (RuntimeException e) {
			log.error("Header.getID() - an exception occurred {}", e.getMessage());
			return PORTO_STORE_DEFAULT_ID;
		}
	}
}

