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

import com.va.securestore.Store;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.UUID;

public class ConnectionsEditor extends javax.swing.JDialog
{
	private static final long serialVersionUID = 1L;

	private final Store store;
	private final ConnectionsList connectionsList;
	private final ConnectionsListModel connectionsListModel;
	private final SaveHandler saveHandler;

	public ConnectionsEditor(ConnectionsList connectionsList, Store store, java.awt.Frame parent)
	{
		super(parent, true);

		this.store = store;
		this.connectionsList = connectionsList;
		this.connectionsListModel = new ConnectionsListModel(connectionsList);
		this.saveHandler = new SaveHandler();
		initComponents();
		init();
	}

	private void init()
	{
		setLocationRelativeTo(getParent());

		connectionsListUIValueChanged(null);

		connectionName.addActionListener(saveHandler);
		connectionName.addFocusListener(saveHandler);
		hostname.addActionListener(saveHandler);
		hostname.addFocusListener(saveHandler);
		port.addActionListener(saveHandler);
		port.addFocusListener(saveHandler);
		username.addActionListener(saveHandler);
		username.addFocusListener(saveHandler);
		password.addActionListener(saveHandler);
		password.addFocusListener(saveHandler);
	}

	private void disableFields()
	{
		connectionName.setText("");
		connectionName.setEnabled(false);
		hostname.setText("");
		hostname.setEnabled(false);
		port.setText("");
		port.setEnabled(false);
		username.setText("");
		username.setEnabled(false);
		password.setText("");
		password.setEnabled(false);
	}

	private void propagateInfo(ConnectionInfo ci)
	{
		connectionName.setText(ci.getName());
		connectionName.setEnabled(true);
		hostname.setText(ci.getHostname());
		hostname.setEnabled(true);
		port.setText(String.valueOf(ci.getPort()));
		port.setEnabled(true);
		username.setText(ci.getUsername());
		username.setEnabled(true);
		password.setText("");
		password.setEnabled(true);
		password.setToolTipText("Type new password to change. Leave empty to keep to current password.");
	}

	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        connectionsListUI = new javax.swing.JList<>();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        hostname = new javax.swing.JTextField();
        port = new javax.swing.JTextField();
        username = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        addConnectionBtn = new javax.swing.JButton();
        deleteConnectionBtn = new javax.swing.JButton();
        connectionName = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        password = new javax.swing.JPasswordField();
        jSeparator1 = new javax.swing.JSeparator();
        closeBtn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Edit Server Connections");
        setModal(true);

        jLabel1.setText("Connections");

        connectionsListUI.setModel(connectionsListModel);
        connectionsListUI.addListSelectionListener(new javax.swing.event.ListSelectionListener()
        {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt)
            {
                connectionsListUIValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(connectionsListUI);

        jLabel2.setText("Details");

        jLabel3.setText("Host");

        jLabel4.setText("Port");

        jLabel5.setText("Username");

        jLabel6.setText("Password");

        addConnectionBtn.setText("+");
        addConnectionBtn.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                addConnectionBtnActionPerformed(evt);
            }
        });

        deleteConnectionBtn.setText("-");
        deleteConnectionBtn.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                deleteConnectionBtnActionPerformed(evt);
            }
        });

        jLabel7.setText("Name");

        closeBtn.setText("Close");
        closeBtn.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                closeBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(25, 25, 25)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel2)
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(12, 12, 12)
                                        .addComponent(jLabel7)
                                        .addGap(18, 18, 18)
                                        .addComponent(connectionName))))
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel5)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel6))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(hostname)
                                    .addComponent(username)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(port, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 193, Short.MAX_VALUE))
                                    .addComponent(password)))))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(addConnectionBtn)
                        .addGap(18, 18, 18)
                        .addComponent(deleteConnectionBtn)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(closeBtn))
                    .addComponent(jSeparator1))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(connectionName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(hostname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(port, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(username, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(password, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6)))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(deleteConnectionBtn)
                    .addComponent(addConnectionBtn))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(closeBtn)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void addConnectionBtnActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_addConnectionBtnActionPerformed
    {//GEN-HEADEREND:event_addConnectionBtnActionPerformed
		connectionsListModel.addElement(new ConnectionInfo(UUID.randomUUID(), "<New connection>"));
		connectionsListUI.setSelectedIndex(connectionsListModel.getSize() - 1);
    }//GEN-LAST:event_addConnectionBtnActionPerformed

    private void deleteConnectionBtnActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_deleteConnectionBtnActionPerformed
    {//GEN-HEADEREND:event_deleteConnectionBtnActionPerformed
		int selections[] = connectionsListUI.getSelectedIndices();
		for (int s : selections)
		{
			connectionsListModel.removeElement(s);
		}
    }//GEN-LAST:event_deleteConnectionBtnActionPerformed

    private void connectionsListUIValueChanged(javax.swing.event.ListSelectionEvent evt)//GEN-FIRST:event_connectionsListUIValueChanged
    {//GEN-HEADEREND:event_connectionsListUIValueChanged
		int selections[] = connectionsListUI.getSelectedIndices();

		if (selections.length == 1)
		{
			propagateInfo(connectionsListUI.getSelectedValue());
		}
		else
		{
			disableFields();
		}

		deleteConnectionBtn.setEnabled(selections.length == 0);
    }//GEN-LAST:event_connectionsListUIValueChanged

    private void closeBtnActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_closeBtnActionPerformed
    {//GEN-HEADEREND:event_closeBtnActionPerformed
        dispose();
    }//GEN-LAST:event_closeBtnActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addConnectionBtn;
    private javax.swing.JButton closeBtn;
    private javax.swing.JTextField connectionName;
    private javax.swing.JList<ConnectionInfo> connectionsListUI;
    private javax.swing.JButton deleteConnectionBtn;
    private javax.swing.JTextField hostname;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JPasswordField password;
    private javax.swing.JTextField port;
    private javax.swing.JTextField username;
    // End of variables declaration//GEN-END:variables

	private class SaveHandler implements ActionListener, FocusListener
	{
		private void saveBack()
		{
			ConnectionInfo ci = connectionsListUI.getSelectedValue();
			if (ci == null)
			{
				return;
			}

			ci.setName(connectionName.getText());
			ci.setHostname(hostname.getText());
			try
			{
				int p = Integer.parseInt(port.getText());
				ci.setPort(p);
			}
			catch (NumberFormatException e)
			{
				port.setText(String.valueOf(ci.getPort()));
			}
			ci.setUsername(username.getText());

			connectionsList.save(store.data);

			char[] passwd = password.getPassword();
			if (passwd != null && passwd.length > 0)
			{
				if (!store.passwords.isOpen())
				{
					store.passwords.open(new SwingUserInteraction(ConnectionsEditor.this));
				}

				store.passwords.storePassword(ConnectionsList.getPasswordKey(ci), passwd);
			}

			connectionsListModel.fireNameChanged(connectionsListUI.getSelectedIndex());
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			saveBack();
		}

		@Override
		public void focusLost(FocusEvent e)
		{
			saveBack();
		}

		@Override
		public void focusGained(FocusEvent e)
		{
		}
	}
}
