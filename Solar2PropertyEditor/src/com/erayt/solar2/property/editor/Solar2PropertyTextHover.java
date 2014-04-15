package com.erayt.solar2.property.editor;

import org.eclipse.jdt.internal.ui.propertiesfileeditor.IPropertiesFilePartitions;
import org.eclipse.jface.text.AbstractReusableInformationControlCreator;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPartitioningException;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IInformationControlExtension2;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextHoverExtension;
import org.eclipse.jface.text.ITextHoverExtension2;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.editors.text.EditorsUI;

/**
 * @author zxy
 * 
 *         Most parts of this class is copy from PropertieFileTextHover excepty
 *         getInfo method. The reason why I do this instead of extending
 *         PropertieFileTextHover is that I don't want to see restrict warning.
 */
public class Solar2PropertyTextHover implements ITextHover,
		ITextHoverExtension, ITextHoverExtension2 {

	private int fOffset;

	private final ITextHover fTextHover;

	private HoverControlCreator fHoverControlCreator;

	public Solar2PropertyTextHover(ITextHover textHover) {
		fTextHover = textHover;
	}

	private static final class HoverControlCreator extends
			AbstractReusableInformationControlCreator {

		@Override
		public IInformationControl doCreateInformationControl(Shell parent) {
			return new PropertiesFileHoverControl(parent,
					EditorsUI.getTooltipAffordanceString());
		}
	}

	static class PropertiesFileHoverControl extends DefaultInformationControl
			implements IInformationControlExtension2 {

		public PropertiesFileHoverControl(Shell parent,
				String tooltipAffordanceString) {
			super(parent, tooltipAffordanceString, null);
		}

		public void setInput(Object input) {
			setInformation((String) input);
		}
	}

	public Object getHoverInfo2(ITextViewer textViewer, IRegion hoverRegion) {
		return getHoverInfo(textViewer, hoverRegion);
	}

	public IInformationControlCreator getHoverControlCreator() {
		if (fHoverControlCreator == null)
			fHoverControlCreator = new HoverControlCreator();
		return fHoverControlCreator;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated see {@link ITextHover#getHoverInfo(ITextViewer, IRegion)}
	 */
	public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
		String hoverInfo = fTextHover.getHoverInfo(textViewer, hoverRegion);
		if (hoverInfo != null && hoverInfo.length() > 0) {
			return hoverInfo;
		}

		IDocument doc = textViewer.getDocument();
		try {
			if (doc instanceof IDocumentExtension3) {
				ITypedRegion partition = ((IDocumentExtension3) doc)
						.getPartition(
								IPropertiesFilePartitions.PROPERTIES_FILE_PARTITIONING,
								this.fOffset, false);
				if (partition.getType().equals(IDocument.DEFAULT_CONTENT_TYPE)) {
					String key = doc.get(hoverRegion.getOffset(),
							hoverRegion.getLength());
					Criteria instance = Criteria.getInstance();
					return instance.getInfoByKey(key);
				}
			}

		} catch (BadLocationException e) {
		} catch (BadPartitioningException e) {
			// TODO Auto-generated catch block
		}
		// String unescapedString= null;
		// try {
		// ITypedRegion partition= null;
		// IDocument document= textViewer.getDocument();
		// if (document instanceof IDocumentExtension3)
		// partition=
		// ((IDocumentExtension3)document).getPartition(IPropertiesFilePartitions.PROPERTIES_FILE_PARTITIONING,
		// fOffset, false);
		// if (partition == null)
		// return null;
		//
		// String type= partition.getType();
		// if (!(type.equals(IPropertiesFilePartitions.PROPERTY_VALUE) ||
		// type.equals(IDocument.DEFAULT_CONTENT_TYPE))) {
		// return null;
		// }
		// String escapedString= document.get(partition.getOffset(),
		// partition.getLength());
		// if (type.equals(IPropertiesFilePartitions.PROPERTY_VALUE)) {
		// escapedString= escapedString.substring(1); //see
		// PropertiesFilePartitionScanner()
		// }
		//
		// try {
		// unescapedString= PropertiesFileEscapes.unescape(escapedString);
		// } catch (CoreException e) {
		// return e.getStatus().getMessage();
		// }
		// if (escapedString.equals(unescapedString))
		// return null;
		// } catch (BadLocationException e) {
		// JavaPlugin.log(e);
		// } catch (BadPartitioningException e) {
		// JavaPlugin.log(e);
		// }
		// return unescapedString;
		return null;
	}

	public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
		fOffset = offset;
		return findWord(textViewer.getDocument(), offset);
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
}
