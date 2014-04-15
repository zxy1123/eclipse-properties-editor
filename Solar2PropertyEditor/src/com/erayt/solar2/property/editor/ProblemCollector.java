package com.erayt.solar2.property.editor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.ISynchronizable;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModelExtension;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;

import com.erayt.solar2.property.Solar2PropertyModel;

public class ProblemCollector {
	private Lock lock;
	private Object lockObejct;
	private IAnnotationModel annotionModel;
	private Map<Solar2PropertyAnnotion, Position> addAnnotions;
	private ISourceViewer viewer;
	private static final String MARKTYPE = "com.solar2.property.errorMarker";

	ProblemCollector(ISourceViewer viewer, IAnnotationModel annotationModel) {
		this.viewer = viewer;
		this.annotionModel = annotationModel;
		if (annotationModel instanceof Solar2PropertyModel) {
			lock = ((Solar2PropertyModel) annotationModel).getLock();
		} else if (annotationModel instanceof ISynchronizable) {
			lockObejct = ((ISynchronizable) annotationModel).getLockObject();
		} else {
			lockObejct = annotationModel;
		}
	}

	protected void clearMarker() {
		IFile file = getFile();
		if (file != null) {
			try {
				file.deleteMarkers(MARKTYPE, false, IResource.DEPTH_INFINITE);
			} catch (CoreException e) {
			}
		}
	}

	protected IFile getFile() {
		IFile file = null;
		if (viewer instanceof PropertySourceViewer) {
			IEditorInput editorInput = ((PropertySourceViewer) viewer)
					.getEditor().getEditorInput();
			if (editorInput instanceof IFileEditorInput) {
				file = ((IFileEditorInput) editorInput).getFile();
			}
		}
		return file;
	}

	public void beginReporting() {
		addAnnotions = new HashMap<Solar2PropertyAnnotion, Position>();
		clearMarker();
	}

	public void accept(Solar2PropertyProblem problem) {
		addAnnotions.put(new Solar2PropertyAnnotion(problem), new Position(
				problem.getStart(), problem.getEnd()));
	}

	public void endReporting() {
		if (lock != null) {
			try {
				lock.lock();
				addAnnotions();
			} finally {
				lock.unlock();
			}
		} else {
			synchronized (lockObejct) {
				addAnnotions();
			}
		}
		addAnnotions = null;
	}

	private void addAnnotions() {
		Iterator annotationIterator = annotionModel.getAnnotationIterator();
		List<Annotation> toRemove = new ArrayList<Annotation>();

		while (annotationIterator.hasNext()) {
			Annotation next = (Annotation) annotationIterator.next();
			if (next instanceof Solar2PropertyAnnotion) {
				toRemove.add(next);
			}
		}
		Annotation[] removeAnnotions = toRemove.toArray(new Annotation[toRemove
				.size()]);
		if (annotionModel instanceof IAnnotationModelExtension) {

			((IAnnotationModelExtension) annotionModel).replaceAnnotations(
					removeAnnotions, addAnnotions);
		} else {
			for (Annotation annotation : removeAnnotions) {
				annotionModel.removeAnnotation(annotation);
			}
			for (Annotation annotation : addAnnotions.keySet()) {
				annotionModel.addAnnotation(annotation,
						addAnnotions.get(annotation));
			}
		}
		IFile file = getFile();
		if (file != null) {
			try {
				for (Annotation annotation : addAnnotions.keySet()) {
					IMarker createMarker = file.createMarker(MARKTYPE);
					createMarker.setAttribute(IMarker.SEVERITY,
							IMarker.SEVERITY_ERROR);
					createMarker.setAttribute(IMarker.MESSAGE,
							annotation.getText());
				}
			} catch (CoreException e) {

			}
		}
	}
}
