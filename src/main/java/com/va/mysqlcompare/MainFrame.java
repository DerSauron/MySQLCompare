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
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.plaf.basic.BasicButtonUI;
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
		int pos = resultTabs.getTabCount();
		resultTabs.insertTab(caption, null, tab, null, pos);
		resultTabs.setTabComponentAt(pos, new ButtonTabComponent());
		resultTabs.setSelectedIndex(pos);
	}

	synchronized public void removeTab(JPanel tab)
	{
		resultTabs.remove(tab);
	}

	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        resultTabs = new javax.swing.JTabbedPane();
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

        newCompareMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
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

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.CTRL_MASK));
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

        quitMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_MASK));
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
            .addComponent(resultTabs, javax.swing.GroupLayout.DEFAULT_SIZE, 596, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(resultTabs, javax.swing.GroupLayout.DEFAULT_SIZE, 347, Short.MAX_VALUE)
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
    private javax.swing.JPopupMenu.Separator connectionsMenuItem;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JMenuItem newCompareMenuItem;
    private javax.swing.JMenuItem quitMenuItem;
    private javax.swing.JTabbedPane resultTabs;
    // End of variables declaration//GEN-END:variables

	private class ButtonTabComponent extends JPanel
	{
		private static final long serialVersionUID = 1L;

		public ButtonTabComponent()
		{
			super(new FlowLayout(FlowLayout.LEFT, 0, 0));
			init();
		}

		private void init()
		{
			setOpaque(false);

			//make JLabel read titles from JTabbedPane
			JLabel label = new JLabel()
			{
				private static final long serialVersionUID = 1L;

				@Override
				public String getText()
				{
					int i = resultTabs.indexOfTabComponent(ButtonTabComponent.this);
					if (i != -1)
					{
						return resultTabs.getTitleAt(i);
					}
					return null;
				}
			};

			add(label);
			label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));

			JButton button = new TabButton();
			add(button);

			setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
		}

		private class TabButton extends JButton implements ActionListener
		{
			private static final long serialVersionUID = 1L;

			public TabButton()
			{
				init();
			}

			private void init()
			{
				int size = 17;
				setPreferredSize(new Dimension(size, size));
				setToolTipText("close this tab");
				setUI(new BasicButtonUI());
				setContentAreaFilled(false);
				setFocusable(false);
				setBorder(BorderFactory.createEtchedBorder());
				setBorderPainted(false);
				addMouseListener(buttonMouseListener);
				setRolloverEnabled(true);
				addActionListener(this);
			}

			@Override
			public void actionPerformed(ActionEvent e)
			{
				int i = resultTabs.indexOfTabComponent(ButtonTabComponent.this);
				if (i != -1)
				{
					resultTabs.remove(i);
				}
			}

			@Override
			public void updateUI()
			{
			}

			@Override
			protected void paintComponent(Graphics g)
			{
				super.paintComponent(g);
				Graphics2D g2 = (Graphics2D)g.create();
				//shift the image for pressed buttons
				if (getModel().isPressed())
				{
					g2.translate(1, 1);
				}
				g2.setStroke(new BasicStroke(2));
				g2.setColor(Color.BLACK);
				if (getModel().isRollover())
				{
					g2.setColor(Color.BLUE);
				}
				int delta = 6;
				g2.drawLine(delta, delta, getWidth() - delta - 1, getHeight() - delta - 1);
				g2.drawLine(getWidth() - delta - 1, delta, delta, getHeight() - delta - 1);
				g2.dispose();
			}
		}

		private final MouseListener buttonMouseListener = new MouseAdapter()
		{

			@Override
			public void mouseEntered(MouseEvent e)
			{
				Component component = e.getComponent();
				if (component instanceof AbstractButton)
				{
					AbstractButton button = (AbstractButton)component;
					button.setBorderPainted(true);
				}
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				Component component = e.getComponent();
				if (component instanceof AbstractButton)
				{
					AbstractButton button = (AbstractButton)component;
					button.setBorderPainted(false);
				}
			}
		};
	}
}
