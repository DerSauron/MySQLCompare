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
package com.va.securestore;

import java.util.Base64;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PasswordUtilsTest
{
	public PasswordUtilsTest()
	{
	}

	@Test
	public void testGenerateRandomBytes()
	{
		assertEquals(16, PasswordUtils.generateRandomBytes(16).length);
		assertEquals(32, PasswordUtils.generateRandomBytes(32).length);
		assertEquals(7, PasswordUtils.generateRandomBytes(7).length);
	}

	@Test
	public void testHashPassword()
	{
		final char[] password = "7_°:qb=SF}lyy`".toCharArray();
		final byte[] salt = Base64.getDecoder().decode("8vomqHqH59D9oHEu3yTG2g==");
		final byte[] hash = Base64.getDecoder().decode("P4hU/XqUz8s9INYSG43BbCYDgphzTRasT9XLsOi/KUU=");

		byte[] actual = PasswordUtils.hashPassword(password, salt);

		assertArrayEquals(hash, actual);
	}

}
