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
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.plaf.basic.BasicButtonUI;

class ButtonTabComponent extends JPanel
{
	private static final long serialVersionUID = 1L;

	private final JTabbedPane tabBar;

	public ButtonTabComponent(JTabbedPane tabBar)
	{
		super(new FlowLayout(FlowLayout.LEFT, 0, 0));

		this.tabBar = tabBar;

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
				int i = tabBar.indexOfTabComponent(ButtonTabComponent.this);
				if (i != -1)
				{
					return tabBar.getTitleAt(i);
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
			int i = tabBar.indexOfTabComponent(ButtonTabComponent.this);
			if (i != -1)
			{
				tabBar.remove(i);
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
