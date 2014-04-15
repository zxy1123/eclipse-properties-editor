package com.erayt.solar2.property.editor;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

public class Solar2CompleteProposal implements ICompletionProposal,
		ICompletionProposalExtension {
	private String replaceString;
	private int offset;
	private int length;

	public Solar2CompleteProposal(String replaceString, int offset, int length) {
		this.replaceString = replaceString;
		this.offset = offset;
		this.length = length;
	}

	@Override
	public void apply(IDocument document) {
		apply(document, (char) 0, offset);
	}

	@Override
	public Point getSelection(IDocument document) {
		return null;
	}

	@Override
	public String getAdditionalProposalInfo() {
		return null;
	}

	@Override
	public String getDisplayString() {
		return replaceString;
	}

	@Override
	public Image getImage() {
		return JFaceResources.getImage(Dialog.DLG_IMG_MESSAGE_INFO);
	}

	@Override
	public IContextInformation getContextInformation() {
		return null;
	}

	@Override
	public void apply(IDocument document, char trigger, int offset) {
		try {
			if (trigger != (char) 0) {
				StringBuilder sb = new StringBuilder(trigger);
				document.replace(this.offset, length ,
						sb.append(replaceString).toString());
			} else {
				document.replace(this.offset, length, replaceString);
			}
		} catch (BadLocationException e) {
		}
	}

	@Override
	public boolean isValidFor(IDocument document, int offset) {
		return true;
	}

	@Override
	public char[] getTriggerCharacters() {
		return new char[] { '.' };
	}

	@Override
	public int getContextInformationPosition() {
		return 0;
	}

}
