package com.erayt.solar2.property.editor;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.ui.PlatformUI;

public class Test extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		return MessageDialog.open(MessageDialog.INFORMATION, PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell(), "Test", "Test",
				SWT.NONE);
	}

}
