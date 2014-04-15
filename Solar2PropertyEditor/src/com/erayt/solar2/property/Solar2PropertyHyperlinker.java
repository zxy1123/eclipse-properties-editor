package com.erayt.solar2.property;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.propertiesfileeditor.IPropertiesFilePartitions;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPartitioningException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

import com.erayt.solar2.property.editor.PropertySourceViewer;


/**
 * @author zhou
 *
 */
public class Solar2PropertyHyperlinker extends AbstractHyperlinkDetector {

	public Solar2PropertyHyperlinker() {
	}

	@Override
	public IHyperlink[] detectHyperlinks(ITextViewer textViewer,
			IRegion region, boolean canShowMultipleHyperlinks) {
		IFile file = null;
		IJavaProject project = null;
		if (textViewer instanceof PropertySourceViewer) {
			PropertySourceViewer viewer = (PropertySourceViewer) textViewer;
			if (viewer.getEditor() != null) {
				IEditorInput editorInput = viewer.getEditor().getEditorInput();
				if (editorInput instanceof IFileEditorInput) {
					file = ((IFileEditorInput) editorInput).getFile();
				}
			}
		}

		if (file == null || file.getProject() == null) {
			return null;
		}
		project = JavaCore.create(file.getProject());
		IDocument doc = textViewer.getDocument();

		if (doc instanceof IDocumentExtension3) {
			try {
				ITypedRegion partition = ((IDocumentExtension3) doc)
						.getPartition(
								IPropertiesFilePartitions.PROPERTIES_FILE_PARTITIONING,
								region.getOffset(), false);
				if (partition.getType().equals(
						IPropertiesFilePartitions.PROPERTY_VALUE)) {
					final IRegion valueRegion = findWord(doc,
							region.getOffset());
					String value = doc.get(valueRegion.getOffset(),
							valueRegion.getLength());
					IType findType = project.findType(value);
					if (findType == null) {
						return null;
					}
					IFile target = (IFile) findType.getUnderlyingResource();
					if (target == null) {
						return null;
					}
					IHyperlink link = new JavaEditorOpenHyperlink(target,
							valueRegion);

					return new IHyperlink[] { link };
				}
			} catch (BadLocationException e) {
			} catch (BadPartitioningException e) {
			} catch (JavaModelException e) {
			}
		}
		return null;
	}

	private IRegion findWord(IDocument document, int offset) {
		int start = -2;
		int end = -1;

		try {

			int pos = offset;
			char c;

			while (pos >= 0) {
				c = document.getChar(pos);
				if (!Character.isUnicodeIdentifierPart(c) && c != '.')
					break;
				--pos;
			}

			start = pos;

			pos = offset;
			int length = document.getLength();

			while (pos < length) {
				c = document.getChar(pos);
				if (!Character.isUnicodeIdentifierPart(c) && c != '.')
					break;
				++pos;
			}

			end = pos;

		} catch (BadLocationException x) {
		}

		if (start >= -1 && end > -1) {
			if (start == offset && end == offset)
				return new Region(offset, 0);
			else if (start == offset)
				return new Region(start, end - start);
			else
				return new Region(start + 1, end - start - 1);
		}

		return null;
	}

	static class JavaEditorOpenHyperlink implements IHyperlink {
		private IFile target;
		private IRegion hyperlinkRegion;

		JavaEditorOpenHyperlink(IFile target, IRegion hyperlinkRegion) {
			this.target = target;
			this.hyperlinkRegion = hyperlinkRegion;
		}

		@Override
		public void open() {
			try {
				PlatformUI
						.getWorkbench()
						.getActiveWorkbenchWindow()
						.getActivePage()
						.openEditor(new FileEditorInput(target),
								"org.eclipse.jdt.ui.CompilationUnitEditor");
			} catch (PartInitException e) {
			}

		}

		@Override
		public String getTypeLabel() {
			return "open delecare";
		}

		@Override
		public String getHyperlinkText() {
			return "open delecare";
		}

		@Override
		public IRegion getHyperlinkRegion() {
			// TODO Auto-generated method stub
			return hyperlinkRegion;
		}
	}
}
