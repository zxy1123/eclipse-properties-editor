package com.erayt.solar2.property.editor;

import java.util.Map;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jdt.internal.ui.propertiesfileeditor.IPropertiesFilePartitions;
import org.eclipse.jdt.internal.ui.propertiesfileeditor.PropertiesFileSourceViewerConfiguration;
import org.eclipse.jdt.internal.ui.text.AbstractJavaScanner;
import org.eclipse.jdt.internal.ui.text.CompositeReconcilingStrategy;
import org.eclipse.jdt.ui.text.IColorManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.reconciler.MonoReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.spelling.SpellingReconcileStrategy;
import org.eclipse.ui.texteditor.spelling.SpellingService;

public class PropertySourceViewerConfig extends
		PropertiesFileSourceViewerConfiguration {
	private AbstractJavaScanner fPropertyKeyScanner;
    
	public PropertySourceViewerConfig(IColorManager colorManager,
			IPreferenceStore preferenceStore, ITextEditor editor,
			String partitioning) {
		super(colorManager, preferenceStore, editor, partitioning);
		fPropertyKeyScanner = new PropertyKeyScanner(getColorManager(),
				fPreferenceStore);
	}

	private ITokenScanner scanner;

	public IPresentationReconciler getPresentationReconciler(
			ISourceViewer sourceViewer) {
		PresentationReconciler reconciler = new PresentationReconciler();
		reconciler
				.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));

		DefaultDamagerRepairer dr = new DefaultDamagerRepairer(
				getCommentScanner());
		reconciler.setDamager(dr, IPropertiesFilePartitions.COMMENT);
		reconciler.setRepairer(dr, IPropertiesFilePartitions.COMMENT);

		dr = new DefaultDamagerRepairer(fPropertyKeyScanner);
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

		dr = new DefaultDamagerRepairer(getPropertyValueScanner());
		reconciler.setDamager(dr, IPropertiesFilePartitions.PROPERTY_VALUE);
		reconciler.setRepairer(dr, IPropertiesFilePartitions.PROPERTY_VALUE);

		return reconciler;
	}

	protected RuleBasedScanner getPropertyKeyScanner() {
		return fPropertyKeyScanner;
	}

	@Override
	public IReconciler getReconciler(ISourceViewer sourceViewer) {
		if (!EditorsUI.getPreferenceStore().getBoolean(
				SpellingService.PREFERENCE_SPELLING_ENABLED))
			return null;

		IReconcilingStrategy strategy = new SpellingReconcileStrategy(
				sourceViewer, EditorsUI.getSpellingService()) {
			@Override
			protected IContentType getContentType() {
				return Platform.getContentTypeManager().getContentType(
						"org.eclipse.jdt.core.javaProperties"); //$NON-NLS-1$

			}
		};
		IReconcilingStrategy solar2Strategy = new Solar2PropertyStrategy(
				sourceViewer);
		CompositeReconcilingStrategy compositeStrategy = new CompositeReconcilingStrategy();
		compositeStrategy.setReconcilingStrategies(new IReconcilingStrategy[] {
				strategy, solar2Strategy });
		MonoReconciler reconciler = new MonoReconciler(compositeStrategy, false);
		reconciler.setDelay(500);
		return reconciler;
	}

//	@Override
//	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
//		if (getEditor() != null) {
//			ContentAssistant assistant = new ContentAssistant();
//			assistant
//					.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));
//			SolarPropertyContentAssistProcessor processor = new SolarPropertyContentAssistProcessor(
//					getConfiguredDocumentPartitioning(sourceViewer));
//			assistant.setContentAssistProcessor(processor,
//					IPropertiesFilePartitions.PROPERTY_VALUE);
//			assistant.enableAutoActivation(true);
//			return assistant;
//		}
//		return null;
//	}
	
	@Override
	public ITextHover getTextHover(ISourceViewer sourceViewer, String contentType, int stateMask) {
		return new Solar2PropertyTextHover(super.getTextHover(sourceViewer, contentType));
	}
	
	@Override
	protected Map<String, ITextEditor> getHyperlinkDetectorTargets(ISourceViewer sourceViewer) {
		Map<String, ITextEditor> targets= super.getHyperlinkDetectorTargets(sourceViewer);
		targets.put("com.erayt.solar2.property", getEditor()); //$NON-NLS-1$
		return targets;
	}

}
