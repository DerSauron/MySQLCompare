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
import javax.swing.JPanel;
import org.slf4j.LoggerFactory;

public class MainFrame extends javax.swing.JFrame
{
	private static final long serialVersionUID = 1L;
	private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(MainFrame.class);

	private final Store store;
	private final ConnectionsList connections = new ConnectionsList();

	public MainFrame(Store store)
	{
		initComponents();
		this.store = store;
		init();
	}

	private void init()
	{
		setLocationRelativeTo(null);
		setExtendedState(getExtendedState() | MAXIMIZED_BOTH);

		connections.load(store.data);
	}

	synchronized public void addTab(String caption, JPanel tab)
	{
		int pos = comparsionTabs.getTabCount();
		comparsionTabs.insertTab(caption, null, tab, null, pos);
		comparsionTabs.setTabComponentAt(pos, new ButtonTabComponent(comparsionTabs));
		comparsionTabs.setSelectedIndex(pos);
	}

	synchronized public void removeTab(JPanel tab)
	{
		comparsionTabs.remove(tab);
	}

	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        comparsionTabs = new javax.swing.JTabbedPane();
        jMenuBar1 = new javax.swing.JMenuBar();
        compareMenu = new javax.swing.JMenu();
        newCompareMenuItem = new javax.swing.JMenuItem();
        connectionsMenuItem = new javax.swing.JPopupMenu.Separator();
        jMenuItem1 = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        quitMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("MySQL Compare");
        setIconImage(Main.getAppIcon());
        setPreferredSize(new java.awt.Dimension(800, 600));

        compareMenu.setMnemonic('c');
        compareMenu.setText("Compare");

        newCompareMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        newCompareMenuItem.setMnemonic('n');
        newCompareMenuItem.setText("New Compare");
        newCompareMenuItem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                newCompareMenuItemActionPerformed(evt);
            }
        });
        compareMenu.add(newCompareMenuItem);
        compareMenu.add(connectionsMenuItem);

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItem1.setText("Edit Server Connections");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jMenuItem1ActionPerformed(evt);
            }
        });
        compareMenu.add(jMenuItem1);
        compareMenu.add(jSeparator1);

        quitMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        quitMenuItem.setMnemonic('q');
        quitMenuItem.setText("Quit");
        quitMenuItem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                quitMenuItemActionPerformed(evt);
            }
        });
        compareMenu.add(quitMenuItem);

        jMenuBar1.add(compareMenu);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(comparsionTabs, javax.swing.GroupLayout.DEFAULT_SIZE, 596, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(comparsionTabs, javax.swing.GroupLayout.DEFAULT_SIZE, 347, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void quitMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_quitMenuItemActionPerformed
    {//GEN-HEADEREND:event_quitMenuItemActionPerformed
		dispose();
    }//GEN-LAST:event_quitMenuItemActionPerformed

    private void newCompareMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_newCompareMenuItemActionPerformed
    {//GEN-HEADEREND:event_newCompareMenuItemActionPerformed
		NewCompareDialog dlg = new NewCompareDialog(connections, store.passwords, this);
		dlg.setVisible(true);
    }//GEN-LAST:event_newCompareMenuItemActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jMenuItem1ActionPerformed
    {//GEN-HEADEREND:event_jMenuItem1ActionPerformed
        ConnectionsEditor dlg = new ConnectionsEditor(connections, store, this);
		dlg.setVisible(true);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu compareMenu;
    private javax.swing.JTabbedPane comparsionTabs;
    private javax.swing.JPopupMenu.Separator connectionsMenuItem;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JMenuItem newCompareMenuItem;
    private javax.swing.JMenuItem quitMenuItem;
    // End of variables declaration//GEN-END:variables

}
