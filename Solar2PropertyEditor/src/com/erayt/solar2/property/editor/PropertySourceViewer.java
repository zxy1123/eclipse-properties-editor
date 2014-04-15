package com.erayt.solar2.property.editor;

import org.eclipse.jface.text.source.IOverviewRuler;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.texteditor.ITextEditor;

public class PropertySourceViewer extends SourceViewer {

	private ITextEditor editor;

	public PropertySourceViewer(Composite parent, IVerticalRuler ruler,
			IOverviewRuler overviewRuler, boolean showAnnotationsOverview,
			int styles, ITextEditor editor) {
		super(parent, ruler, overviewRuler, showAnnotationsOverview, styles);
		this.editor = editor;
	}

	public ITextEditor getEditor() {
		return this.editor;
	}

}
