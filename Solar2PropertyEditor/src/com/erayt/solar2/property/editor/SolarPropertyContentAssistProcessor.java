package com.erayt.solar2.property.editor;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;

public class SolarPropertyContentAssistProcessor implements
		IContentAssistProcessor {

	private String errorMessage = null;
	private String partioning;

	public SolarPropertyContentAssistProcessor(String partioning) {
		this.partioning = partioning;
	}

	@Override
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer,
			int offset) {
		IDocument doc = viewer.getDocument();
		if (doc == null) {
			return null;
		}
		try {
			ITypedRegion partition = TextUtilities.getPartition(doc,
					partioning, offset, false);

			int start = partition.getOffset();
			int length = offset - start ;
			String part = doc.get(start, length);
			char[] charArray = part.toCharArray();
			if (!Character.isWhitespace(charArray[length - 1])) {
				int count = length - 1;
				while (count-- >= 0) {
					char c = charArray[count];
					if (c == '=' || c == ':' || Character.isWhitespace(c)) {
						start += count + 1;
						break;
					}
				}

			} else {
				start = offset;
			}
			length = offset - start;
			return new ICompletionProposal[] {
					new Solar2CompleteProposal("test", start, length),
					new Solar2CompleteProposal("test", start, length) };

		} catch (BadLocationException e) {
			// TODO Auto-generated catch block

		}
		return null;
	}

	@Override
	public IContextInformation[] computeContextInformation(ITextViewer viewer,
			int offset) {
		return null;
	}

	@Override
	public char[] getCompletionProposalAutoActivationCharacters() {
		return new char[] { '.' };
	}

	@Override
	public char[] getContextInformationAutoActivationCharacters() {
		return null;
	}

	@Override
	public String getErrorMessage() {
		if (errorMessage != null) {
			return errorMessage;
		}
		return null;
	}

	@Override
	public IContextInformationValidator getContextInformationValidator() {
		return null;
	}

}
