package com.erayt.solar2.property;
import org.eclipse.core.filebuffers.IAnnotationModelFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.text.source.AnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModel;

public class Solar2PropertyModelFactory implements IAnnotationModelFactory {

	@Override
	public IAnnotationModel createAnnotationModel(IPath location) {
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(location);
		if (file != null) {
			return new Solar2PropertyModel(file);
		}
		return new AnnotationModel();
	}

}
