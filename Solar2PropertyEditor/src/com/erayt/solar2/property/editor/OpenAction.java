package com.erayt.solar2.property.editor;

import org.eclipse.jdt.internal.ui.propertiesfileeditor.PropertiesFileEditor;
import org.eclipse.jdt.internal.ui.propertiesfileeditor.PropertyKeyHyperlinkDetector;
import org.eclipse.jdt.ui.actions.SelectionDispatchAction;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.IFileEditorInput;

public class OpenAction extends SelectionDispatchAction {
	private PropertyEditor fEditor;

	/**
	 * Creates a new <code>OpenAction</code>.
	 * 
	 * @param editor
	 *            the Properties file editor which provides the context
	 *            information for this action
	 */
	public OpenAction(PropertyEditor editor) {
		super(editor.getEditorSite());
		fEditor = editor;
		setText("open");
		setToolTipText("open file");

		// XXX: Must be removed once support for JARs is available (see class
		// Javadoc for details).
		setEnabled(fEditor.getEditorInput() instanceof IFileEditorInput);
	}

	/*
	 * @see
	 * org.eclipse.jdt.ui.actions.SelectionDispatchAction#selectionChanged(org
	 * .eclipse.jface.text.ITextSelection)
	 */
	@Override
	public void selectionChanged(ITextSelection selection) {
		setEnabled(checkEnabled(selection));
	}

	private boolean checkEnabled(ITextSelection selection) {
		if (selection == null || selection.isEmpty())
			return false;

		// XXX: Must be changed to IStorageEditorInput once support for JARs is
		// available (see class Javadoc for details)
		return fEditor.getEditorInput() instanceof IFileEditorInput;
	}

	@Override
	public void run(ITextSelection selection) {

		if (!checkEnabled(selection))
			return;

		IRegion region = new Region(selection.getOffset(),
				selection.getLength());
		PropertyKeyHyperlinkDetector detector = new PropertyKeyHyperlinkDetector();
		detector.setContext(fEditor);
		IHyperlink[] hyperlinks = detector.detectHyperlinks(
				fEditor.internalGetSourceViewer(), region, false);

		if (hyperlinks != null && hyperlinks.length == 1)
			hyperlinks[0].open();

	}
}
