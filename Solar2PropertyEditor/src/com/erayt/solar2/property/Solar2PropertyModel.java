/**
 *  
 */
package com.erayt.solar2.property;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.eclipse.core.resources.IResource;
import org.eclipse.ui.texteditor.ResourceMarkerAnnotationModel;

/**
 * @author zhou
 * 
 */
public class Solar2PropertyModel extends ResourceMarkerAnnotationModel {

	private Lock lock;

	/**
	 * @param resource
	 */
	public Solar2PropertyModel(IResource resource) {
		super(resource);
		lock = new ReentrantLock();
	}

	public Lock getLock() {
		return this.lock;
	}

}
