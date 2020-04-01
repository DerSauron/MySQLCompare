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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.DefaultListModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseSelectorTab extends javax.swing.JPanel
{
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(DatabaseSelectorTab.class);

	private final MainFrame mainFrame;
	private final ConnectionsManager conManager;

	private boolean aSelected = false;
	private boolean bSelected = false;

	public DatabaseSelectorTab(MainFrame mainFrame, ConnectionsManager conManager)
	{
		this.mainFrame = mainFrame;
		this.conManager = conManager;

		initComponents();
		updateCompareButton();
		load();
	}

	private void load()
	{
		final BlockDialog block = new BlockDialog(mainFrame);

		serverAHeader.setText("Databases from server " + conManager.getServerA() + " (A)");
		serverBHeader.setText("Databases from server " + conManager.getServerB() + " (B)");

		final SwingWorker<Void, Void> loadDatabasesWorker = new SwingWorker<Void, Void>()
		{
			@Override
			protected Void doInBackground() throws Exception
			{
				try
				{
					block.showAsync();

					final UserInteraction interactor = new SwingUserInteraction(mainFrame);

					if (!conManager.connect(interactor))
					{
						conManager.close();
						SwingUtilities.invokeLater(() ->
						{
							mainFrame.removeTab(DatabaseSelectorTab.this);
						});
						return null;
					}

					Thread loadFromA = new Thread(() ->
					{
						try (Statement stmt = conManager.getConnectionA().createStatement())
						{
							DefaultListModel<String> model = new DefaultListModel<>();
							ResultSet result = stmt.executeQuery("SHOW DATABASES");
							while (result.next())
							{
								model.addElement(result.getString(1));
							}
							serverADatabases.setModel(model);
						}
						catch (SQLException e)
						{
							LOG.error("Could not list databases from server " + conManager.getServerA(), e);
							interactor.showErrorMessage("Could not list databases from " + conManager.getServerA(), e);
						}
					});
					loadFromA.start();

					Thread loadFromB = new Thread(() ->
					{
						try (Statement stmt = conManager.getConnectionB().createStatement())
						{
							DefaultListModel<String> model = new DefaultListModel<>();
							ResultSet result = stmt.executeQuery("SHOW DATABASES");
							while (result.next())
							{
								model.addElement(result.getString(1));
							}
							serverBDatabases.setModel(model);
						}
						catch (SQLException e)
						{
							LOG.error("Could not list databases from server " + conManager.getServerB(), e);
							interactor.showErrorMessage("Could not list databases from " + conManager.getServerB(), e);
						}
					});
					loadFromB.start();

					try
					{
						loadFromA.join();
					}
					catch (InterruptedException e)
					{
					}
					try
					{
						loadFromB.join();
					}
					catch (InterruptedException e)
					{
					}

					setEnabled(true);
				}
				catch (SQLException e)
				{
					LOG.error(null, e);
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

		loadDatabasesWorker.execute();
	}

	private void updateCompareButton()
	{
		compareButton.setEnabled(aSelected && bSelected);
	}

	private void startCompare()
	{
		String databaseA = serverADatabases.getSelectedValue();
		String databaseB = serverBDatabases.getSelectedValue();

		if (databaseA != null && databaseB != null)
		{
			mainFrame.addTab(databaseA + " <-> " + databaseB,
				new ResultTab(mainFrame, conManager, databaseA, databaseB));
		}
	}

	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {
        java.awt.GridBagConstraints gridBagConstraints;

        serverAHeader = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        serverADatabases = new javax.swing.JList<>();
        serverBHeader = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        serverBDatabases = new javax.swing.JList<>();
        jPanel1 = new javax.swing.JPanel();
        compareButton = new javax.swing.JButton();
        reloadButton = new javax.swing.JButton();

        setEnabled(false);
        java.awt.GridBagLayout layout = new java.awt.GridBagLayout();
        layout.columnWeights = new double[] {1.0, 1.0, 0.0};
        layout.rowWeights = new double[] {0.0, 1.0};
        setLayout(layout);

        serverAHeader.setText("Databases Server A");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(serverAHeader, gridBagConstraints);

        serverADatabases.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        serverADatabases.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                serverADatabasesMouseClicked(evt);
            }
        });
        serverADatabases.addListSelectionListener(new javax.swing.event.ListSelectionListener()
        {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt)
            {
                serverADatabasesValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(serverADatabases);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(jScrollPane1, gridBagConstraints);

        serverBHeader.setText("Databases Server B");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(serverBHeader, gridBagConstraints);

        serverBDatabases.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        serverBDatabases.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                serverBDatabasesMouseClicked(evt);
            }
        });
        serverBDatabases.addListSelectionListener(new javax.swing.event.ListSelectionListener()
        {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt)
            {
                serverBDatabasesValueChanged(evt);
            }
        });
        jScrollPane2.setViewportView(serverBDatabases);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(jScrollPane2, gridBagConstraints);

        compareButton.setText("Compare");
        compareButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                compareButtonActionPerformed(evt);
            }
        });

        reloadButton.setText("Reload");
        reloadButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                reloadButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(compareButton, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                    .addComponent(reloadButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(compareButton)
                .addGap(18, 18, 18)
                .addComponent(reloadButton)
                .addContainerGap(294, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(jPanel1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void compareButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_compareButtonActionPerformed
    {//GEN-HEADEREND:event_compareButtonActionPerformed
		startCompare();
    }//GEN-LAST:event_compareButtonActionPerformed

    private void serverADatabasesValueChanged(javax.swing.event.ListSelectionEvent evt)//GEN-FIRST:event_serverADatabasesValueChanged
    {//GEN-HEADEREND:event_serverADatabasesValueChanged
		aSelected = serverADatabases.getSelectedIndex() != -1;
		updateCompareButton();
    }//GEN-LAST:event_serverADatabasesValueChanged

    private void serverBDatabasesValueChanged(javax.swing.event.ListSelectionEvent evt)//GEN-FIRST:event_serverBDatabasesValueChanged
    {//GEN-HEADEREND:event_serverBDatabasesValueChanged
		bSelected = serverBDatabases.getSelectedIndex() != -1;
		updateCompareButton();
    }//GEN-LAST:event_serverBDatabasesValueChanged

    private void reloadButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_reloadButtonActionPerformed
    {//GEN-HEADEREND:event_reloadButtonActionPerformed
		load();
    }//GEN-LAST:event_reloadButtonActionPerformed

    private void serverADatabasesMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_serverADatabasesMouseClicked
    {//GEN-HEADEREND:event_serverADatabasesMouseClicked
		if (evt.getClickCount() >= 2 && aSelected && bSelected)
			startCompare();
    }//GEN-LAST:event_serverADatabasesMouseClicked

    private void serverBDatabasesMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_serverBDatabasesMouseClicked
    {//GEN-HEADEREND:event_serverBDatabasesMouseClicked
		if (evt.getClickCount() >= 2 && aSelected && bSelected)
			startCompare();
    }//GEN-LAST:event_serverBDatabasesMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton compareButton;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton reloadButton;
    private javax.swing.JList<String> serverADatabases;
    private javax.swing.JLabel serverAHeader;
    private javax.swing.JList<String> serverBDatabases;
    private javax.swing.JLabel serverBHeader;
    // End of variables declaration//GEN-END:variables
}
