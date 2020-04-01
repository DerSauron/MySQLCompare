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

import javax.swing.AbstractListModel;

public class ConnectionsListModel extends AbstractListModel<ConnectionInfo>
{
	private static final long serialVersionUID = 1L;

	private final ConnectionsList connectionsList;

	public ConnectionsListModel(ConnectionsList connectionsList)
	{
		this.connectionsList = connectionsList;
	}

	public ConnectionsList getConnectionsList()
	{
		return connectionsList;
	}

	@Override
	public int getSize()
	{
		return connectionsList.size();
	}

	@Override
	public ConnectionInfo getElementAt(int index)
	{
		return connectionsList.get(index);
	}

	public void addElement(ConnectionInfo ci)
	{
		connectionsList.add(ci);
		fireIntervalAdded(this, connectionsList.size(), connectionsList.size());
	}

	public void removeElement(int index)
	{
		connectionsList.remove(index);
		fireIntervalRemoved(this, index, index);
	}

	public void fireNameChanged(int index)
	{
		fireContentsChanged(this, index, index);
	}
}
