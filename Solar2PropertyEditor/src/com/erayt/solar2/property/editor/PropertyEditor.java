package com.erayt.solar2.property.editor;

import java.util.ResourceBundle;

import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.internal.ui.IJavaHelpContextIds;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.javaeditor.ToggleCommentAction;
import org.eclipse.jdt.internal.ui.propertiesfileeditor.IPropertiesFilePartitions;
import org.eclipse.jdt.internal.ui.propertiesfileeditor.PropertiesFileSourceViewerConfiguration;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jdt.ui.actions.IJavaEditorActionDefinitionIds;
import org.eclipse.jdt.ui.actions.JdtActionConstants;
import org.eclipse.jdt.ui.text.JavaTextTools;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.source.AnnotationColumn;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.editors.text.ITextEditorHelpContextIds;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.handlers.IHandlerActivation;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.IShowInTargetList;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;

import com.erayt.solar2.Solar2PropertyEditorActivator;

public class PropertyEditor extends TextEditor {
	private IHandlerService handlerService;
	private final String COMMANDID = "com.erayt.test.command";
//	private IHandlerActivation activateHandler;

	public PropertyEditor() {
//		handlerService = (IHandlerService) PlatformUI.getWorkbench()
//				.getActiveWorkbenchWindow().getActivePage().getActivePart().getSite()
//				.getService(IHandlerService.class);
	}

	protected void initializeEditor() {
		setDocumentProvider(JavaPlugin.getDefault()
				.getPropertiesFileDocumentProvider());
		IPreferenceStore store = JavaPlugin.getDefault()
				.getCombinedPreferenceStore();
		setPreferenceStore(store);
		EditorsUI.getPreferenceStore().setValue("solar2propertystyle",
				"PROBLEM_UNDERLINE");
		EditorsUI.getPreferenceStore().setValue("solar2propertytext", true);
		JavaTextTools textTools = JavaPlugin.getDefault().getJavaTextTools();
		setSourceViewerConfiguration(new PropertySourceViewerConfig(
				textTools.getColorManager(), store, this,
				IPropertiesFilePartitions.PROPERTIES_FILE_PARTITIONING));
		setEditorContextMenuId("#TextEditorContext"); //$NON-NLS-1$
		setRulerContextMenuId("#TextRulerContext"); //$NON-NLS-1$
		setHelpContextId(ITextEditorHelpContextIds.TEXT_EDITOR);
		configureInsertMode(SMART_INSERT, false);
		setInsertMode(INSERT);

		// Need to listen on Editors UI preference store because JDT disables
		// this functionality in its preferences.
		fPropertyChangeListener = new IPropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				if (AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SPACES_FOR_TABS
						.equals(event.getProperty()))
					handlePreferenceStoreChanged(event);
			}
		};
		EditorsUI.getPreferenceStore().addPropertyChangeListener(
				fPropertyChangeListener);
	}

	/** Open action. */
	protected OpenAction fOpenAction;

	/**
	 * Property change listener on Editors UI store.
	 * 
	 * @since 3.7
	 */
	private IPropertyChangeListener fPropertyChangeListener;

	/*
	 * @see org.eclipse.ui.editors.text.TextEditor#initializeEditor()
	 * 
	 * @since 3.4
	 */

	/*
	 * @see org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#
	 * initializeKeyBindingScopes()
	 * 
	 * @since 3.4
	 */
	@Override
	protected void initializeKeyBindingScopes() {
		setKeyBindingScopes(new String[] { "org.eclipse.jdt.ui.propertiesEditorScope" }); //$NON-NLS-1$
	}

	/*
	 * @see org.eclipse.ui.editors.text.TextEditor#createActions()
	 */
	@Override
	protected void createActions() {
		super.createActions();

		IAction action = new ToggleCommentAction(
				ResourceBundle
						.getBundle("org.eclipse.jdt.internal.ui.propertiesfileeditor.ConstructedPropertiesFileEditorMessages"), "ToggleComment.", this); //$NON-NLS-1$
		action.setActionDefinitionId(IJavaEditorActionDefinitionIds.TOGGLE_COMMENT);
		setAction(IJavaEditorActionDefinitionIds.TOGGLE_COMMENT, action);
		markAsStateDependentAction(
				IJavaEditorActionDefinitionIds.TOGGLE_COMMENT, true);
		PlatformUI.getWorkbench().getHelpSystem()
				.setHelp(action, IJavaHelpContextIds.TOGGLE_COMMENT_ACTION);
		configureToggleCommentAction();

		fOpenAction = new OpenAction(this);
		fOpenAction
				.setActionDefinitionId(IJavaEditorActionDefinitionIds.OPEN_EDITOR);
		setAction(JdtActionConstants.OPEN, fOpenAction);
//		activateHandler = handlerService.activateHandler(COMMANDID, new Test());

	}

	/**
	 * Configures the toggle comment action.
	 * 
	 * @since 3.4
	 */
	private void configureToggleCommentAction() {
		IAction action = getAction(IJavaEditorActionDefinitionIds.TOGGLE_COMMENT);
		if (action instanceof ToggleCommentAction) {
			ISourceViewer sourceViewer = getSourceViewer();
			SourceViewerConfiguration configuration = getSourceViewerConfiguration();
			((ToggleCommentAction) action).configure(sourceViewer,
					configuration);
		}
	}

	/*
	 * @see AbstractTextEditor#handlePreferenceStoreChanged(PropertyChangeEvent)
	 */
	@Override
	protected void handlePreferenceStoreChanged(PropertyChangeEvent event) {

		try {

			ISourceViewer sourceViewer = getSourceViewer();
			if (sourceViewer == null)
				return;

			((PropertiesFileSourceViewerConfiguration) getSourceViewerConfiguration())
					.handlePropertyChangeEvent(event);

		} finally {
			super.handlePreferenceStoreChanged(event);
		}
	}

	/*
	 * @see AbstractTextEditor#affectsTextPresentation(PropertyChangeEvent)
	 */
	@Override
	protected boolean affectsTextPresentation(PropertyChangeEvent event) {
		return ((PropertiesFileSourceViewerConfiguration) getSourceViewerConfiguration())
				.affectsTextPresentation(event)
				|| super.affectsTextPresentation(event);
	}

	/*
	 * @see org.eclipse.ui.editors.text.TextEditor#getAdapter(java.lang.Class)
	 */
	@Override
	public Object getAdapter(Class adapter) {
		if (adapter == IShowInTargetList.class) {
			return new IShowInTargetList() {
				public String[] getShowInTargetIds() {
					return new String[] { JavaUI.ID_PACKAGES,
							JavaPlugin.ID_RES_NAV };
				}

			};
		}
		return super.getAdapter(adapter);
	}

	/*
	 * @see org.eclipse.ui.part.WorkbenchPart#getOrientation()
	 * 
	 * @since 3.2
	 */
	@Override
	public int getOrientation() {
		return SWT.LEFT_TO_RIGHT; // properties editors are always left to right
									// by default (see
									// https://bugs.eclipse.org/bugs/show_bug.cgi?id=110986)
	}

	protected ISourceViewer createSourceViewer(Composite parent,
			IVerticalRuler ruler, int styles) {

		fAnnotationAccess = getAnnotationAccess();
		fOverviewRuler = createOverviewRuler(getSharedColors());

		ISourceViewer viewer = new PropertySourceViewer(parent, ruler,
				getOverviewRuler(), isOverviewRulerVisible(), styles, this);
		getSourceViewerDecorationSupport(viewer);

		return viewer;
	}

	/*
	 * @see
	 * org.eclipse.ui.texteditor.StatusTextEditor#updateStatusField(java.lang
	 * .String)
	 */
	@Override
	protected void updateStatusField(String category) {
		super.updateStatusField(category);
		if (getEditorSite() != null) {
			getEditorSite().getActionBars().getStatusLineManager()
					.setMessage(null);
			getEditorSite().getActionBars().getStatusLineManager()
					.setErrorMessage(null);
		}
	}

	/*
	 * @see org.eclipse.ui.texteditor.AbstractTextEditor#getSourceViewer()
	 */
	ISourceViewer internalGetSourceViewer() {
		return getSourceViewer();
	}

	/*
	 * @see org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#
	 * collectContextMenuPreferencePages()
	 * 
	 * @since 3.1
	 */
	@Override
	protected String[] collectContextMenuPreferencePages() {
		String[] ids = super.collectContextMenuPreferencePages();
		String[] more = new String[ids.length + 1];
		more[0] = "org.eclipse.jdt.ui.preferences.PropertiesFileEditorPreferencePage"; //$NON-NLS-1$
		System.arraycopy(ids, 0, more, 1, ids.length);
		return more;
	}

	/*
	 * @see
	 * org.eclipse.ui.editors.text.TextEditor#editorContextMenuAboutToShow(org
	 * .eclipse.jface.action.IMenuManager)
	 * 
	 * @since 3.4
	 */
	@Override
	protected void editorContextMenuAboutToShow(IMenuManager menu) {
		super.editorContextMenuAboutToShow(menu);

		addAction(menu, ITextEditorActionConstants.GROUP_EDIT,
				IJavaEditorActionDefinitionIds.TOGGLE_COMMENT);
	}

	/*
	 * @see org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#
	 * isTabsToSpacesConversionEnabled()
	 * 
	 * @since 3.7
	 */
	@Override
	protected boolean isTabsToSpacesConversionEnabled() {
		// Can't use our own preference store because JDT disables this
		// functionality in its preferences.
		return EditorsUI
				.getPreferenceStore()
				.getBoolean(
						AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SPACES_FOR_TABS);
	}

	/*
	 * @see org.eclipse.ui.editors.text.TextEditor#dispose()
	 * 
	 * @since 3.7
	 */
	@Override
	public void dispose() {
		EditorsUI.getPreferenceStore().removePropertyChangeListener(
				fPropertyChangeListener);
		super.dispose();
	}

}
