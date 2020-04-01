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
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.SwingUtilities;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

public class SwingUserInteraction implements UserInteraction
{

	private final Component parent;

	public SwingUserInteraction(Component parent)
	{
		this.parent = parent;
	}

	@Override
	public char[] getPassword(String realm)
	{
		JPanel panel = new JPanel();
		JLabel label = new JLabel(realm);
		panel.add(label);
		JPasswordField pass = new JPasswordField(10);
		pass.addAncestorListener(new RequestFocusListener());
		panel.add(pass);
		String[] options = new String[]
		{
			"OK", "Cancel"
		};
		int option = JOptionPane.showOptionDialog(parent, panel, "Specify password",
			JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
		if (option == 0)
		{
			return pass.getPassword();
		}
		else
		{
			return null;
		}
	}

	@Override
	public void showErrorMessage(String msg, Throwable t)
	{
		JPanel messageBox = new JPanel();
		messageBox.setLayout(new BoxLayout(messageBox, BoxLayout.PAGE_AXIS));
		JLabel message = new JLabel("<html><b>" + msg + "</b>");
		messageBox.add(message);
		if (t != null)
		{
			JLabel detail;
			while (t != null)
			{
				messageBox.add(Box.createRigidArea(new Dimension(5, 5)));
				detail = new JLabel("<html>Caused by: <i>" + t.getClass().getName() + "</i><br>"
					+ t.getMessage());
				messageBox.add(detail);
				t = t.getCause();
			}
		}

		JOptionPane.showMessageDialog(parent, messageBox, "Error occured", JOptionPane.ERROR_MESSAGE);
	}

	private static class RequestFocusListener implements AncestorListener
	{
		@Override
		public void ancestorAdded(AncestorEvent event)
		{
			SwingUtilities.invokeLater(() -> {
				JComponent comp = event.getComponent();
				comp.requestFocusInWindow();
				comp.removeAncestorListener(this);
			});
		}

		@Override
		public void ancestorMoved(AncestorEvent event)
		{
		}

		@Override
		public void ancestorRemoved(AncestorEvent event)
		{
		}
	}
}
