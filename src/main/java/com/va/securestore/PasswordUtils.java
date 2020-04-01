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

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PasswordUtils
{
	private PasswordUtils()
	{
	}

	private static final SecureRandom RAND = new SecureRandom();
	private static final int ITERATIONS = 65536;
	private static final int KEY_LENGTH = 256;
	private static final String ALGORITHM = "PBKDF2WithHmacSHA256";

	public static byte[] generateRandomBytes(int length)
	{
		if (length < 1)
			throw new IllegalArgumentException("Salt length must by greater than 0");

		byte[] salt = new byte[length];

		RAND.nextBytes(salt);

		return salt;
	}

	public static byte[] hashPassword(char[] clearText, byte[] salt)
	{
		PBEKeySpec spec = new PBEKeySpec(clearText, salt, ITERATIONS, KEY_LENGTH);

		Arrays.fill(clearText, Character.MIN_VALUE);

		try
		{
			SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);

			return factory.generateSecret(spec).getEncoded();
		}
		catch (NoSuchAlgorithmException | InvalidKeySpecException e)
		{
			throw new IllegalArgumentException(e);
		}
		finally
		{
			spec.clearPassword();
		}
	}
}
