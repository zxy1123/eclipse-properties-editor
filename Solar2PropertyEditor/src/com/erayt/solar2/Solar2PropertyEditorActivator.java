package com.erayt.solar2;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Solar2PropertyEditorActivator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.erayt.solar2.Solar2PropertyEditor"; //$NON-NLS-1$

	// The shared instance
	private static Solar2PropertyEditorActivator plugin;
	
	/**
	 * The constructor
	 */
	public Solar2PropertyEditorActivator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Solar2PropertyEditorActivator getDefault() {
		return plugin;
	}
	
	public static void Log(Exception e){
		IStatus status = new Status(IStatus.ERROR,PLUGIN_ID,IStatus.ERROR,e.getMessage(),e); 
		plugin.getLog().log(status);
	}

}
