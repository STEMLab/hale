/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.hale.rcp.utils.crs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.CompositeRuler;
import org.eclipse.jface.text.source.LineNumberRulerColumn;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import eu.esdihumboldt.hale.WKTPreferencesCRSFactory;

/**
 * Code list preference page
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class CRSPreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage {
	
	private ComboViewer listViewer;
	
	private List<String> codes;

	private SourceViewer wktEditor;
	
	private String lastSelected = null;

	private final Map<String, String> tmpWKTs = new HashMap<String, String>();

	/**
	 * @see PreferencePage#createContents(Composite)
	 */
	@Override
	protected Control createContents(Composite parent) {
		Composite page = new Composite(parent, SWT.NONE);
		
		page.setLayout(new GridLayout(2, true));
		
		Label label = new Label(page, SWT.NONE);
		label.setText("Additional CRSes specified by an EPSG code and a WKT");
		label.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false, 2, 1));
		
		// code list
		listViewer = new ComboViewer(page);
		listViewer.setContentProvider(ArrayContentProvider.getInstance());
		listViewer.setLabelProvider(new LabelProvider());
		listViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
		
		codes = WKTPreferencesCRSFactory.getInstance().getCodes();
		listViewer.setInput(codes);
		
		listViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection selection = event.getSelection();
				if (selection.isEmpty()) {
					updateEditor(null);
				}
				else {
					if (selection instanceof IStructuredSelection) {
						updateEditor((String) ((IStructuredSelection) selection).getFirstElement());
					}
				}
			}
			
		});
		
		// fill wkt map 
		for (String code : codes) {
			tmpWKTs.put(code, WKTPreferencesCRSFactory.getInstance().getWKT(code));
		}
		
		// WKT editor
		final Display display = Display.getCurrent();
		CompositeRuler ruler = new CompositeRuler(3);
		LineNumberRulerColumn lineNumbers = new LineNumberRulerColumn();
		lineNumbers.setBackground(display.getSystemColor(SWT.COLOR_GRAY)); //SWT.COLOR_INFO_BACKGROUND));
		lineNumbers.setForeground(display.getSystemColor(SWT.COLOR_BLACK)); //SWT.COLOR_INFO_FOREGROUND));
		lineNumbers.setFont(JFaceResources.getTextFont());
		ruler.addDecorator(0, lineNumbers);
		
		wktEditor = new SourceViewer(page, ruler, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		wktEditor.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		wktEditor.getTextWidget().setFont(JFaceResources.getTextFont());
		SourceViewerConfiguration conf = new SourceViewerConfiguration();
		wktEditor.configure(conf);
		
		// create initial document
		IDocument doc = new Document();
		doc.set("");
		wktEditor.setInput(doc);
		
		// add button (using a directory dialog)
		Button add = new Button(page, SWT.PUSH);
		add.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		add.setText("Add...");
		add.setToolTipText("Add a CRS");
		add.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				final Display display = Display.getCurrent();
				InputDialog dialog = new InputDialog(display.getActiveShell(), 
						"Add CRS", "Enter the CRS code", 
						WKTPreferencesCRSFactory.AUTHORITY_PREFIX, new IInputValidator() {
							
							@Override
							public String isValid(String newText) {
								if (!newText.startsWith(WKTPreferencesCRSFactory.AUTHORITY_PREFIX)) {
									return "Code must start with " + WKTPreferencesCRSFactory.AUTHORITY_PREFIX;
								}
								
								return null;
							}
						});
				if (dialog.open() == InputDialog.OK) {
					String value = dialog.getValue();
					codes.add(value);
					listViewer.refresh(false);
					listViewer.setSelection(new StructuredSelection(value));
				}
			}
		});
		
		// remove button
		Button remove = new Button(page, SWT.PUSH);
		remove.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		remove.setText("Remove");
		remove.setToolTipText("Remove the selected code");
		remove.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				ISelection selection = listViewer.getSelection();
				if (!selection.isEmpty() && selection instanceof IStructuredSelection) {
					String selected = (String) ((IStructuredSelection) selection).getFirstElement();
					codes.remove(selected);
					listViewer.refresh(false);
					if (!codes.isEmpty()) {
						listViewer.setSelection(new StructuredSelection(codes.get(0)));
					}
					tmpWKTs.remove(selected);
				}
			}
			
		});
		
		// update selection
		if (codes.isEmpty()) {
			listViewer.setSelection(new StructuredSelection());
		}
		else {
			listViewer.setSelection(new StructuredSelection(codes.get(0)));
		}
		
		return page;
	}
	
	/**
	 * Update the editor
	 * 
	 * @param selected the selected feature type
	 */
	protected void updateEditor(String selected) {
		saveCurrent();
		
		IDocument doc = wktEditor.getDocument();
		lastSelected = selected;
		
		if (selected != null) {
			// load WKT for current selection
			String wkt = getWKT(selected);
			doc.set(wkt);
			wktEditor.getControl().setEnabled(true);
			wktEditor.setEditable(true);
		}
		else {
			wktEditor.getControl().setEnabled(false);
			wktEditor.setEditable(false);
			doc.set("");
		}
	}
	
	/**
	 * Get the WKT for the given code
	 * 
	 * @param code the CRS code
	 * 
	 * @return the WKT
	 */
	private String getWKT(String code) {
		String wkt = tmpWKTs.get(code);
		
//		if (wkt == null) {
//			wkt = WKTPreferencesCRSFactory.getInstance().getWKT(code);
//		}
		
		if (wkt == null) {
			wkt = "";
		}
		
		return wkt;
	}

	/**
	 * Save the filter that is currently edited 
	 */
	protected void saveCurrent() {
		IDocument doc = wktEditor.getDocument();
		
		if (lastSelected != null) {
			// save WKT for last selection
			setWKT(lastSelected, doc.get());
		}
	}
	
	/**
	 * Set the WKT for the given code
	 * 
	 * @param code the CRS code
	 * @param wkt the WKT
	 */
	protected void setWKT(String code, String wkt) {
		tmpWKTs.put(code, wkt);
	}

	/**
	 * @see PreferencePage#performOk()
	 */
	@Override
	public boolean performOk() {
		saveCurrent();
		
		List<String> orgCodes = WKTPreferencesCRSFactory.getInstance().getCodes();
		// remove all old ones
		for (String code : orgCodes) {
			WKTPreferencesCRSFactory.getInstance().removeWKT(code);
		}
		
		// add new ones
		for (Entry<String, String> entry : tmpWKTs.entrySet()) {
			String code = entry.getKey();
			String wkt = entry.getValue();
			
			if (wkt != null) {
				wkt = wkt.trim();
				if (!wkt.isEmpty()) {
					WKTPreferencesCRSFactory.getInstance().addWKT(code, wkt);
				}
			}
		}
		
		return true;
	}

	/**
	 * @see IWorkbenchPreferencePage#init(IWorkbench)
	 */
	@Override
	public void init(IWorkbench workbench) {
		// ignore
	}

}
