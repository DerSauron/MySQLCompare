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

import javax.swing.GroupLayout;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class ComparisonTab extends javax.swing.JPanel
{
	private static final long serialVersionUID = 1L;

	private final MainFrame mainFrame;
	private final DatabaseSelectorTab selectorTab;

    public ComparisonTab(MainFrame mainFrame, ConnectionsManager connectionsManager)
	{
		this.mainFrame = mainFrame;
		this.selectorTab = new DatabaseSelectorTab(this, connectionsManager);

		initComponents();
		init();
    }

	private void init()
	{
		int pos = tabs.getTabCount();
		tabs.insertTab("Database selection", null, selectorTab, null, pos);
		tabs.setSelectedIndex(pos);
	}

	public MainFrame getMainFrame()
	{
		return mainFrame;
	}

	synchronized public void addTab(String caption, JPanel tab)
	{
		int pos = tabs.getTabCount();
		tabs.insertTab(caption, null, tab, null, pos);
		tabs.setTabComponentAt(pos, new ButtonTabComponent(tabs));
		tabs.setSelectedIndex(pos);
	}

	synchronized public void removeTab(JPanel tab)
	{
		tabs.remove(tab);

		if (tabs.getTabCount() == 0)
			mainFrame.removeTab(this);
	}

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        tabs = new JTabbedPane();

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(tabs, GroupLayout.DEFAULT_SIZE, 608, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(tabs, GroupLayout.DEFAULT_SIZE, 471, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JTabbedPane tabs;
    // End of variables declaration//GEN-END:variables

}
