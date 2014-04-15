package com.erayt.solar2;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.content.IContentDescriber;
import org.eclipse.core.runtime.content.IContentDescription;

public class Solar2PropertiesDescriber implements IContentDescriber {
	private static final QualifiedName[] SUPPORTED_OPTIONS = new QualifiedName[] {
			IContentDescription.CHARSET, IContentDescription.BYTE_ORDER_MARK };
	private static final char CR = '\r';
	private static final char LF = '\n';
	private static final String SOLAR2PROPERTY = "#!solar2";
	private static final int LINELENG = 100;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.content.IContentDescriber#describe(java.io.
	 * InputStream, org.eclipse.core.runtime.content.IContentDescription)
	 */
	@Override
	public int describe(InputStream contents, IContentDescription description)
			throws IOException {
		byte[] bom = getByteOrderMark(contents);
		contents.reset();
		// String encoding = "UTF-8";
		if (bom != null) {
			// if (bom == IContentDescription.BOM_UTF_16BE) {
			// encoding = "UTF-16BE";
			// } else if (bom == IContentDescription.BOM_UTF_16LE) {
			// encoding = "UTF-16LE";
			// }
			contents.skip(bom.length);
		}
		byte[] line = new byte[LINELENG];
		int read;
		int count = 0;
		int index = 0;
		while ((read = contents.read()) != -1 && count++ < LINELENG) {
			if (read == CR) {
				int next = contents.read();
				if (next == LF) {
					break;
				}

			} else if (read == LF) {
				break;
			}
			line[index++] = (byte) read;
		}
		String firstLine = new String(line, "utf-8");
		if (firstLine.startsWith(SOLAR2PROPERTY)) {
			return VALID;
		}
		return INVALID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.runtime.content.IContentDescriber#getSupportedOptions()
	 */
	@Override
	public QualifiedName[] getSupportedOptions() {
		return SUPPORTED_OPTIONS;
	}

	public static byte[] getByteOrderMark(InputStream input) throws IOException {
		int first = input.read();
		if (first == 0xEF) {
			// look for the UTF-8 Byte Order Mark (BOM)
			int second = input.read();
			int third = input.read();
			if (second == 0xBB && third == 0xBF)
				return IContentDescription.BOM_UTF_8;
		} else if (first == 0xFE) {
			// look for the UTF-16 BOM
			if (input.read() == 0xFF)
				return IContentDescription.BOM_UTF_16BE;
		} else if (first == 0xFF) {
			if (input.read() == 0xFE)
				return IContentDescription.BOM_UTF_16LE;
		}
		return null;
	}

}
