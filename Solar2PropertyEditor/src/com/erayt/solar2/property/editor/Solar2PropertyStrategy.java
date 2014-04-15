package com.erayt.solar2.property.editor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.internal.ui.propertiesfileeditor.IPropertiesFilePartitions;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.reconciler.IReconcilingStrategyExtension;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.ISourceViewer;

public class Solar2PropertyStrategy implements IReconcilingStrategy,
		IReconcilingStrategyExtension {
	private IDocument document;
	private ProblemCollector problemCollector;
	private ISourceViewer viewer;
	private Solar2PropertyChecker checker;

	public Solar2PropertyStrategy(ISourceViewer viewer) {
		this.viewer = viewer;
		this.checker = new Solar2PropertyChecker();
	}

	@Override
	public void setProgressMonitor(IProgressMonitor monitor) {

	}

	@Override
	public void initialReconcile() {
		reconcile();
	}

	@Override
	public void setDocument(IDocument document) {
		this.document = document;
		this.problemCollector = createProblemCollector();

	}

	protected ProblemCollector createProblemCollector() {
		IAnnotationModel model = getAnnotationModel();
		if (model == null) {
			return null;
		}
		return new ProblemCollector(this.viewer, model);
	}

	@Override
	public void reconcile(DirtyRegion dirtyRegion, IRegion subRegion) {
		reconcile();
	}

	@Override
	public void reconcile(IRegion partition) {
		reconcile();
	}

	protected void reconcile() {
		if (getAnnotationModel() == null || problemCollector == null) {
			return;
		}
		try {

			problemCollector.beginReporting();
			ITypedRegion[] computePartitioning = TextUtilities
					.computePartitioning(
							document,
							IPropertiesFilePartitions.PROPERTIES_FILE_PARTITIONING,
							0, document.getLength(), false);
			String key, value;
			for (int i = 0; i < computePartitioning.length; i++) {
				ITypedRegion partition = computePartitioning[i];
				ITypedRegion valuePartition;
				if (partition.getType().equals(IDocument.DEFAULT_CONTENT_TYPE)) {
					key = document.get(partition.getOffset(),
							partition.getLength());
					value = null;
					if (key.trim().isEmpty()) {
						continue;
					}
					if ((i + 1) < computePartitioning.length) {
						valuePartition = computePartitioning[i + 1];
						if (!valuePartition.getType().equals(
								IPropertiesFilePartitions.PROPERTY_VALUE)) {
							continue;
						}
						value = document.get(valuePartition.getOffset(),
								valuePartition.getLength());
						if (value.trim().isEmpty()) {
							continue;
						}
					} else {
						break;
					}

					check(partition, valuePartition);
				}
			}
		} catch (BadLocationException e) {
		} finally {
			finallyCheck();
			problemCollector.endReporting();
		}
	}

	private void finallyCheck() {
		checker.finallyCheck();

	}

	protected IAnnotationModel getAnnotationModel() {
		return viewer.getAnnotationModel();
	}

	private void check(ITypedRegion key, ITypedRegion value) {
		checker.check(document, key, value, problemCollector);
	}

}
