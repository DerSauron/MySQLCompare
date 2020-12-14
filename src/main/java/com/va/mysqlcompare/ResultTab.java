/*
 * Copyright (C) 2020 Daniel Volk <mail@volkarts.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.va.mysqlcompare;

import com.va.common.UserInteraction;
import com.va.lcs.LCS;
import com.va.lcs.TokenList;
import com.va.mysqlcompare.CompareResult.Diff;
import com.va.mysqlcompare.CompareResult.FieldDiff;
import com.va.mysqlcompare.CompareResult.KeyDiff;
import com.va.mysqlcompare.CompareResult.ProcedureDiff;
import com.va.mysqlcompare.CompareResult.TableDiff;
import com.va.mysqlcompare.CompareResult.ViewDiff;
import java.awt.Color;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.DefaultListModel;
import javax.swing.ListModel;
import javax.swing.SwingWorker;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static com.va.mysqlcompare.CompareResult.Diff.Mode.DIFFERENT;
import static com.va.mysqlcompare.CompareResult.Diff.Mode.LEFT_ONLY;
import static com.va.mysqlcompare.CompareResult.Diff.Mode.RIGHT_ONLY;

public class ResultTab extends javax.swing.JPanel
{
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(ResultTab.class);

	private final ComparisonTab comparisonTab;
	private final ConnectionsManager conManager;
	private final String databaseA;
	private final String databaseB;
	private int backupSliderPosition = 0;
	private CompareResult result;
	private DDERenderer previewRenderer;

	public ResultTab(ComparisonTab comparisonTab, ConnectionsManager conManager, String databaseA,
		String databaseB)
	{
		this.comparisonTab = comparisonTab;
		this.conManager = conManager;
		this.databaseA = databaseA;
		this.databaseB = databaseB;

		initComponents();
		init();
		load();
	}

	private void init()
	{
		addStylesToDocument(outputAB.getStyledDocument());
		addStylesToDocument(outputBA.getStyledDocument());

		int lineHeight = outputAB.getFontMetrics(outputAB.getFont()).getHeight();

		outputBAScrollArea.getVerticalScrollBar().setModel(
			outputABScrollArea.getVerticalScrollBar().getModel());
		outputABScrollArea.getVerticalScrollBar().setUnitIncrement(lineHeight);
		outputBAScrollArea.getVerticalScrollBar().setUnitIncrement(lineHeight);
		outputBAScrollArea.getHorizontalScrollBar().setModel(
			outputABScrollArea.getHorizontalScrollBar().getModel());

		selectDiffCheckItemStateChanged(null);
		showBABtnActionPerformed(null);
	}

	private void addStylesToDocument(StyledDocument doc)
	{
		Style def = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
		StyleConstants.setFontFamily(def, "Monospaced");
		StyleConstants.setFontSize(def, 12);

		doc.addStyle(StyleContext.DEFAULT_STYLE, def);

		Style yellow = doc.addStyle("yellow", def);
		StyleConstants.setBackground(yellow, new Color(200, 200, 80));

		Style red = doc.addStyle("red", def);
		StyleConstants.setBackground(red, new Color(255, 120, 120));

		Style green = doc.addStyle("green", def);
		StyleConstants.setBackground(green, new Color(100, 255, 100));
	}

	private void load()
	{
		final BlockDialog block = new BlockDialog(mainFrame);

		final SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>()
		{
			@Override
			protected Void doInBackground() throws Exception
			{
				block.showAsync();

				Compare compare = new Compare(conManager);
				UserInteraction interactor = new SwingUserInteraction(mainFrame);

				try
				{
					conManager.connect(interactor);

					setResult(compare.doCompare(databaseA, databaseB));
				}
				catch (Exception e)
				{
					LOG.error("Could not compare tables in " + databaseA + " and " + databaseB, e);
					interactor.showErrorMessage("Could not compare tables in " + databaseA + " and " + databaseB, e);
				}
				finally
				{
					try
					{
						conManager.close();
					}
					catch (SQLException e)
					{
					}
					block.setVisible(false);
				}

				return null;
			}
		};

		worker.execute();
	}

	private void loadList()
	{
		DefaultListModel<ListEntry> listModel = new DefaultListModel<>();

		boolean hasDiffs = false;

		for (CompareResult.Diff diff : result.getDiffs())
		{
			if (diff.getMode() != Diff.Mode.EQUAL)
			{
				hasDiffs = true;
			}

			switch (diff.getType())
			{
				case TABLE:
					addTableDiff(listModel, (TableDiff)diff);
					break;
				case VIEW:
					addViewDiff(listModel, (ViewDiff)diff);
					break;
				case FIELD:
					addFieldDiff(listModel, (FieldDiff)diff);
					break;
				case KEY:
					addKeyDiff(listModel, (KeyDiff)diff);
					break;
				case PROCEDURE:
					addProcedureDiff(listModel, (ProcedureDiff)diff);
			}
		}

		if (!hasDiffs)
		{
			listModel.addElement(new ListEntry(null, "The databases are equal"));
		}

		changeList.setModel(listModel);
		selectItems();
	}

	private void addTableDiff(DefaultListModel<ListEntry> listModel, TableDiff tableDiff)
	{
		switch (tableDiff.getMode())
		{
			case LEFT_ONLY:
				listModel.addElement(new ListEntry(tableDiff, "TABLE `" + tableDiff.getTableInfoA().getName() + "` only exists in A"));
				break;
			case RIGHT_ONLY:
				listModel.addElement(new ListEntry(tableDiff, "TABLE `" + tableDiff.getTableInfoB().getName() + "` only exists in B"));
				break;
			case DIFFERENT:
				listModel.addElement(new ListEntry(tableDiff, "TABLE `" + tableDiff.getTableInfoB().getName() + "` differs in A and B"));
				break;
		}
	}

	private void addViewDiff(DefaultListModel<ListEntry> listModel, ViewDiff viewDiff)
	{
		switch (viewDiff.getMode())
		{
			case LEFT_ONLY:
				listModel.addElement(new ListEntry(viewDiff, "VIEW  `" + viewDiff.getViewInfoA().getName() + "` only exists in A"));
				break;
			case RIGHT_ONLY:
				listModel.addElement(new ListEntry(viewDiff, "VIEW  `" + viewDiff.getViewInfoB().getName() + "` only exists in B"));
				break;
			case DIFFERENT:
				listModel.addElement(new ListEntry(viewDiff, "VIEW  `" + viewDiff.getViewInfoA().getName() + "` differs in A and B"));
				break;
		}
	}

	private void addFieldDiff(DefaultListModel<ListEntry> listModel, FieldDiff fieldDiff)
	{
		switch (fieldDiff.getMode())
		{
			case LEFT_ONLY:
				listModel.addElement(new ListEntry(fieldDiff, "FIELD `" + fieldDiff.getFieldInfoA().getTableName() + "`.`" + fieldDiff.getFieldInfoA().getName() + "` only exists in A"));
				break;
			case RIGHT_ONLY:
				listModel.addElement(new ListEntry(fieldDiff, "FIELD `" + fieldDiff.getFieldInfoB().getTableName() + "`.`" + fieldDiff.getFieldInfoB().getName() + "` only exists in B"));
				break;
			case DIFFERENT:
			{
				String message = "FIELD `" + fieldDiff.getFieldInfoA().getTableName() + "`.`" + fieldDiff.getFieldInfoA().getName() + "` differs in ";
				if (!fieldDiff.isSimpleEquals() || !fieldDiff.isTypeEquals())
				{
					message += "A and B";
				}
				else
				{
					message += "collation only";
				}
				listModel.addElement(new ListEntry(fieldDiff, message));
				break;
			}
		}
	}

	private void addKeyDiff(DefaultListModel<ListEntry> listModel, KeyDiff keyDiff)
	{
		switch (keyDiff.getMode())
		{
			case LEFT_ONLY:
				listModel.addElement(new ListEntry(keyDiff, "KEY   `" + keyDiff.getKeyInfoA().getTableName() + "`.`" + keyDiff.getKeyInfoA().getName() + "` only exists in A"));
				break;
			case RIGHT_ONLY:
				listModel.addElement(new ListEntry(keyDiff, "KEY   `" + keyDiff.getKeyInfoB().getTableName() + "`.`" + keyDiff.getKeyInfoB().getName() + "` only exists in B"));
				break;
			case DIFFERENT:
				listModel.addElement(new ListEntry(keyDiff, "KEY   `" + keyDiff.getKeyInfoA().getTableName() + "`.`" + keyDiff.getKeyInfoA().getName() + "` differs in A and B"));
				break;
		}
	}

	private void addProcedureDiff(DefaultListModel<ListEntry> listModel, ProcedureDiff procedureDiff)
	{
		switch (procedureDiff.getMode())
		{
			case LEFT_ONLY:
				listModel.addElement(new ListEntry(procedureDiff, "PROC  `" + procedureDiff.getProcedureInfoA().getName() + "` only exists in A"));
				break;
			case RIGHT_ONLY:
				listModel.addElement(new ListEntry(procedureDiff, "PROC  `" + procedureDiff.getProcedureInfoB().getName() + "` only exists in B"));
				break;
			case DIFFERENT:
				listModel.addElement(new ListEntry(procedureDiff, "PROC  `" + procedureDiff.getProcedureInfoA().getName() + "` differs in A and B"));
				break;
		}
	}

	public ConnectionsManager getConnectionsManager()
	{
		return conManager;
	}

	public void setResult(CompareResult result)
	{
		this.result = result;
		this.previewRenderer = new DDERenderer(result.getDiffs());
		loadList();
	}

	public CompareResult getResult()
	{
		return result;
	}

	private void rerenderDDE()
	{
		int scrollPos = outputABScrollArea.getVerticalScrollBar().getValue();

		final StyledDocument docAB = outputAB.getStyledDocument();
		try
		{
			docAB.remove(0, docAB.getLength());
		}
		catch (BadLocationException ex)
		{
			LOG.warn(null, ex);
		}
		final StyledDocument docBA = outputBA.getStyledDocument();
		try
		{
			docBA.remove(0, docBA.getLength());
		}
		catch (BadLocationException ex)
		{
			LOG.warn(null, ex);
		}

		if (changeList.getSelectedIndices().length > 0)
		{
			final StringWriter writerAB = new StringWriter();
			final StringWriter writerBA = new StringWriter();
			final LCS<String> lcs = new LCS<>();

			List<ListEntry> entries = changeList.getSelectedValuesList();
			entries.stream().forEachOrdered((entry) ->
			{
				previewRenderer.renderSingle(entry.getDiff(), writerAB, false);
				String stringA = writerAB.toString();

				previewRenderer.renderSingle(entry.getDiff(), writerBA, true);
				String stringB = writerBA.toString();

				Diff.Mode mode = entry.getDiff().getMode();

				if (mode == Diff.Mode.DIFFERENT)
				{
					lcs.compute(TokenList.tokenizeWords(stringA), TokenList.tokenizeWords(stringB));

					lcs.extractSubLists().forEach((sub) ->
					{
						switch (sub.dir)
						{
							case '-':
								appendString(docAB, sub.glue(), docAB.getStyle("yellow"));
								break;
							case '+':
								appendString(docBA, sub.glue(), docAB.getStyle("yellow"));
								break;
							// if (sub.dir == ' ')
							default:
								appendString(docAB, sub.glue(), null);
								appendString(docBA, sub.glue(), null);
								break;
						}
					});
				}
				else if (mode == Diff.Mode.LEFT_ONLY)
				{
					appendString(docAB, stringA, docAB.getStyle("green"));
					appendString(docBA, stringB, docAB.getStyle("red"));
				}
				else if (mode == Diff.Mode.RIGHT_ONLY)
				{
					appendString(docAB, stringA, docAB.getStyle("red"));
					appendString(docBA, stringB, docAB.getStyle("green"));
				}
				else
				{
					appendString(docAB, stringA, null);
					appendString(docBA, stringB, null);
				}

				int lcA = lineCount(stringA);
				int lcB = lineCount(stringB);
				if (lcA < lcB)
				{
					int diff = lcB - lcA;
					appendString(docAB, emptyLines(diff), null);
				}
				else if (lcA > lcB)
				{
					int diff = lcA - lcB;
					appendString(docBA, emptyLines(diff), null);
				}

				writerAB.clear();
				writerBA.clear();
			});
		}
		outputABScrollArea.getVerticalScrollBar().setValue(scrollPos);
	}

	private static int lineCount(String str)
	{
		int lines = 1;

		Matcher m = Pattern.compile("\r\n|\r|\n").matcher(str);
		while (m.find())
		{
			lines++;
		}

		return lines;
	}

	private static String emptyLines(int count)
	{
		StringBuilder output = new StringBuilder();
		for (int i = 0; i < count; ++i)
		{
			output.append("\n");
		}
		return output.toString();
	}

	private void appendString(StyledDocument doc, String string, AttributeSet attrs)
	{
		try
		{
			if (attrs == null)
			{
				attrs = doc.getStyle(StyleContext.DEFAULT_STYLE);
			}

			doc.insertString(doc.getLength(), string, attrs);
		}
		catch (BadLocationException ex)
		{
			LOG.warn(null, ex);
		}
	}

	private void selectItems()
	{
		changeList.getSelectionModel().setValueIsAdjusting(true);
		changeList.clearSelection();
		ListModel<ListEntry> listModel = changeList.getModel();
		for (int i = 0; i < listModel.getSize(); ++i)
		{
			ListEntry entry = listModel.getElementAt(i);
			Diff diff = entry.getDiff();

			if (diff == null)
			{
				continue;
			}

			Diff.Mode mode = diff.getMode();

			boolean different;

			if (diff instanceof FieldDiff)
			{
				FieldDiff fd = (FieldDiff)diff;

				different = (mode == Diff.Mode.DIFFERENT) && selectDiffCheck.isSelected()
					&& (!fd.isSimpleEquals() || !fd.isTypeEquals()
					|| (!fd.isCollationEquals() && collationDiffCheck.isSelected()));
			}
			else
			{
				different = mode == Diff.Mode.DIFFERENT && selectDiffCheck.isSelected();
			}

			if ((mode == Diff.Mode.LEFT_ONLY && selectACheck.isSelected())
				|| (mode == Diff.Mode.RIGHT_ONLY && selectBCheck.isSelected())
				|| different)
			{
				changeList.addSelectionInterval(i, i);
			}
		}
		changeList.getSelectionModel().setValueIsAdjusting(false);
	}

	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {
        java.awt.GridBagConstraints gridBagConstraints;

        directionBtns = new javax.swing.ButtonGroup();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        outputBALabel = new javax.swing.JLabel();
        outputBAScrollArea = new javax.swing.JScrollPane();
        outputBANoWrap = new javax.swing.JPanel();
        outputBA = new javax.swing.JTextPane();
        outputABScrollArea = new javax.swing.JScrollPane();
        outputABNoWrap = new javax.swing.JPanel();
        outputAB = new javax.swing.JTextPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        changeList = new javax.swing.JList<>();
        jPanel1 = new javax.swing.JPanel();
        refreshButton = new javax.swing.JButton();
        selectACheck = new javax.swing.JCheckBox();
        selectBCheck = new javax.swing.JCheckBox();
        selectDiffCheck = new javax.swing.JCheckBox();
        doFilterBtn = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        collationDiffCheck = new javax.swing.JCheckBox();
        showBABtn = new javax.swing.JToggleButton();

        java.awt.GridBagLayout layout = new java.awt.GridBagLayout();
        layout.columnWidths = new int[] {1};
        layout.rowHeights = new int[] {1};
        layout.columnWeights = new double[] {1.0};
        layout.rowWeights = new double[] {1.0};
        setLayout(layout);

        jSplitPane1.setDividerLocation(250);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setResizeWeight(0.3);
        jSplitPane1.setMinimumSize(new java.awt.Dimension(500, 500));

        jPanel2.setLayout(new java.awt.GridBagLayout());

        jLabel2.setText("A -> B");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel2.add(jLabel2, gridBagConstraints);

        outputBALabel.setText("B -> A");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel2.add(outputBALabel, gridBagConstraints);

        outputBANoWrap.setLayout(new java.awt.BorderLayout());
        outputBANoWrap.add(outputBA, java.awt.BorderLayout.CENTER);

        outputBAScrollArea.setViewportView(outputBANoWrap);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel2.add(outputBAScrollArea, gridBagConstraints);

        outputABNoWrap.setLayout(new java.awt.BorderLayout());
        outputABNoWrap.add(outputAB, java.awt.BorderLayout.CENTER);

        outputABScrollArea.setViewportView(outputABNoWrap);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel2.add(outputABScrollArea, gridBagConstraints);

        jSplitPane1.setRightComponent(jPanel2);

        changeList.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        changeList.addListSelectionListener(new javax.swing.event.ListSelectionListener()
        {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt)
            {
                changeListValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(changeList);

        jSplitPane1.setLeftComponent(jScrollPane1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(jSplitPane1, gridBagConstraints);

        refreshButton.setText("Refresh");
        refreshButton.setMinimumSize(new java.awt.Dimension(100, 25));
        refreshButton.setPreferredSize(new java.awt.Dimension(100, 25));
        refreshButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                refreshButtonActionPerformed(evt);
            }
        });

        selectACheck.setSelected(true);
        selectACheck.setText("Select A only");

        selectBCheck.setText("Select B only");

        selectDiffCheck.setSelected(true);
        selectDiffCheck.setText("Select diffs");
        selectDiffCheck.addItemListener(new java.awt.event.ItemListener()
        {
            public void itemStateChanged(java.awt.event.ItemEvent evt)
            {
                selectDiffCheckItemStateChanged(evt);
            }
        });

        doFilterBtn.setText("Select");
        doFilterBtn.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                doFilterBtnActionPerformed(evt);
            }
        });

        jLabel1.setText("Selection");

        collationDiffCheck.setText("Include collation diffs");

        showBABtn.setText("Show B -> A");
        showBABtn.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                showBABtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(refreshButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(doFilterBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel1)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(selectDiffCheck)
                            .addComponent(selectACheck)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(21, 21, 21)
                                .addComponent(collationDiffCheck))
                            .addComponent(selectBCheck)))
                    .addComponent(showBABtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(refreshButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(selectACheck)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(selectDiffCheck)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(collationDiffCheck)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(selectBCheck)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(doFilterBtn)
                .addGap(18, 18, 18)
                .addComponent(showBABtn)
                .addContainerGap(448, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(jPanel1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void refreshButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_refreshButtonActionPerformed
    {//GEN-HEADEREND:event_refreshButtonActionPerformed
		load();
    }//GEN-LAST:event_refreshButtonActionPerformed

    private void changeListValueChanged(javax.swing.event.ListSelectionEvent evt)//GEN-FIRST:event_changeListValueChanged
    {//GEN-HEADEREND:event_changeListValueChanged
		if (evt.getValueIsAdjusting())
		{
			return;
		}

		rerenderDDE();
    }//GEN-LAST:event_changeListValueChanged

    private void doFilterBtnActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_doFilterBtnActionPerformed
    {//GEN-HEADEREND:event_doFilterBtnActionPerformed
		selectItems();
    }//GEN-LAST:event_doFilterBtnActionPerformed

    private void selectDiffCheckItemStateChanged(java.awt.event.ItemEvent evt)//GEN-FIRST:event_selectDiffCheckItemStateChanged
    {//GEN-HEADEREND:event_selectDiffCheckItemStateChanged
		collationDiffCheck.setEnabled(selectDiffCheck.isSelected());
    }//GEN-LAST:event_selectDiffCheckItemStateChanged

    private void showBABtnActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_showBABtnActionPerformed
    {//GEN-HEADEREND:event_showBABtnActionPerformed
        boolean selected = showBABtn.isSelected();

		outputBALabel.setVisible(selected);
		outputBAScrollArea.setVisible(selected);
    }//GEN-LAST:event_showBABtnActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList<ListEntry> changeList;
    private javax.swing.JCheckBox collationDiffCheck;
    private javax.swing.ButtonGroup directionBtns;
    private javax.swing.JButton doFilterBtn;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTextPane outputAB;
    private javax.swing.JPanel outputABNoWrap;
    private javax.swing.JScrollPane outputABScrollArea;
    private javax.swing.JTextPane outputBA;
    private javax.swing.JLabel outputBALabel;
    private javax.swing.JPanel outputBANoWrap;
    private javax.swing.JScrollPane outputBAScrollArea;
    private javax.swing.JButton refreshButton;
    private javax.swing.JCheckBox selectACheck;
    private javax.swing.JCheckBox selectBCheck;
    private javax.swing.JCheckBox selectDiffCheck;
    private javax.swing.JToggleButton showBABtn;
    // End of variables declaration//GEN-END:variables

	private class ListEntry
	{
		private final Diff diff;
		private final String text;

		public ListEntry(Diff diff, String text)
		{
			this.diff = diff;
			this.text = text;
		}

		public Diff getDiff()
		{
			return diff;
		}

		public String getText()
		{
			return text;
		}

		@Override
		public String toString()
		{
			return text;
		}
	}

	private class StringWriter implements DDERenderer.OutputWriter
	{
		public final StringBuilder string = new StringBuilder();

		@Override
		public void print(String string)
		{
			this.string.append(string);
		}

		@Override
		public void println(String string)
		{
			this.string.append(string).append("\n");
		}

		public void clear()
		{
			this.string.delete(0, this.string.length());
		}

		@Override
		public String toString()
		{
			return this.string.toString();
		}
	}
}
