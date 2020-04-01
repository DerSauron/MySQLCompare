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

import java.io.Serializable;
import java.util.UUID;

public class ConnectionInfo implements Serializable
{
	private static final long serialVersionUID = 1L;

	private final UUID id;
	private String name;
	private String hostname;
	private int port;
	private String username;

	public ConnectionInfo(UUID id)
	{
		this.id = id;
		this.port = 3306;
		this.username = "root";
	}

	public ConnectionInfo(UUID id, String name)
	{
		this(id);
		this.name = name;
	}

	public UUID getId()
	{
		return id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getHostname()
	{
		return hostname;
	}

	public void setHostname(String hostname)
	{
		this.hostname = hostname;
	}

	public int getPort()
	{
		return port;
	}

	public boolean isStandardPort()
	{
		return port == 0 || port == 3306;
	}

	public void setPort(int port)
	{
		this.port = port;
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	@Override
	public String toString()
	{
		return name + " (" + username + "@" + hostname + (!isStandardPort() ? ":" + port : "") + ")";
	}
}
