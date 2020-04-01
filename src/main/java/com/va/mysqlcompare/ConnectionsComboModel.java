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

import javax.swing.ComboBoxModel;

public class ConnectionsComboModel extends ConnectionsListModel implements ComboBoxModel<ConnectionInfo>
{
	private static final long serialVersionUID = 1L;

	private Object selectedItem;

	public ConnectionsComboModel(ConnectionsList connectionsList)
	{
		super(connectionsList);
		selectedItem = null;
	}

	@Override
	public void setSelectedItem(Object anItem)
	{
		selectedItem = anItem;
	}

	@Override
	public Object getSelectedItem()
	{
		return selectedItem;
	}

}
