package org.gualdi.grails.plugins.ckeditor.utils

import org.codehaus.groovy.grails.plugins.PluginManagerHolder

/**
 * @author Stefano Gualdi <stefano.gualdi@gmail.com>
 */

class PluginUtils {

	static getPluginResourcePath(contextPath, pluginName) {
		String pluginVersion = PluginManagerHolder?.pluginManager?.getGrailsPlugin(pluginName)?.version
		return "${contextPath}/plugins/${pluginName.toLowerCase()}-$pluginVersion"
	}
}