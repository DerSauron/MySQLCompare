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
import com.va.securestore.PasswordStore;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class ConnectionsManager implements AutoCloseable
{
	private static final Logger LOG = Logger.getLogger(ConnectionsManager.class.getName());

	private final ConnectionInfo serverA;
	private final ConnectionInfo serverB;
	private final PasswordStore passwordStore;
	private Connection connectionA = null;
	private Connection connectionB = null;

	private final AtomicInteger connectionCount = new AtomicInteger(0);

	public ConnectionsManager(ConnectionInfo serverA, ConnectionInfo serverB, PasswordStore passwordStore)
	{
		this.serverA = serverA;
		this.serverB = serverB;
		this.passwordStore = passwordStore;
	}

	public boolean connect(UserInteraction userInteraction) throws SQLException
	{
		synchronized (passwordStore)
		{
			if (!passwordStore.isOpen())
			{
				if (!passwordStore.open(userInteraction))
					return false;
			}
		}

		if (connectionCount.getAndAdd(1) == 0)
		{
			connectionA = connect(serverA, userInteraction);
			connectionB = connect(serverB, userInteraction);
		}

		return connectionA != null && connectionB != null;
	}

	private Connection connect(ConnectionInfo serverInfo, UserInteraction userInteraction)
		throws SQLException
	{
		while (true)
		{
			try
			{
				StringBuilder url = new StringBuilder();
				url.append("jdbc:mysql://").append(serverInfo.getHostname());
				if (serverInfo.getPort() != 0)
				{
					url.append(":").append(serverInfo.getPort());
				}
				url.append("/");

				char[] password = passwordStore.loadPassword(ConnectionsList.getPasswordKey(serverInfo));

				Connection connection = DriverManager.getConnection(url.toString(),
					serverInfo.getUsername(), String.valueOf(password));

				return connection;
			}
			catch (SQLException e)
			{
				LOG.warning(e.toString());
				if ("28000".equals(e.getSQLState()) && (userInteraction != null))
				{
					// maybe we should try with another password
					char[] pw = userInteraction.getPassword("Connection password for: " + serverInfo.toString());
					if (pw != null)
					{
						passwordStore.storePassword(ConnectionsList.getPasswordKey(serverInfo), pw);
						continue;
					}
					return null;
				}

				if (userInteraction != null)
				{
					userInteraction.showErrorMessage("Could not connect to Database at " + serverInfo, e);
				}
				throw e;
			}
		}
	}

	public ConnectionInfo getServerA()
	{
		return serverA;
	}

	public ConnectionInfo getServerB()
	{
		return serverB;
	}

	public Connection getConnectionA()
	{
		return connectionA;
	}

	public Connection getConnectionB()
	{
		return connectionB;
	}

	@Override
	public void close() throws SQLException
	{
		if (connectionCount.decrementAndGet() == 0)
		{
			if (connectionA != null)
			{
				connectionA.close();
				connectionA = null;
			}
			if (connectionB != null)
			{
				connectionB.close();
				connectionB = null;
			}
		}
	}
}
