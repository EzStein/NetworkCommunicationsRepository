package crypto;

import java.math.*;
import java.security.*;
import java.util.*;

/**
 * Contains functions used to generate RSA keys and to encrypt blocks using those keys.
 * @author Ezra
 *
 */
public class RSA
{
	/**
	 * Generates public and private keys for RSA encryption.
	 * @return A list of BigIntegers where the first element and second element are the public key, and the third element is the private key.
	 */
	public static BigInteger[] GenerateKeys()
	{
		BigInteger p = BigInteger.probablePrime(512, new SecureRandom());
		BigInteger q = BigInteger.probablePrime(512, new SecureRandom());
		BigInteger modulus = p.multiply(q);
		BigInteger phi = p.subtract(BigInteger.ONE) ;
		phi = phi.multiply(q.subtract(BigInteger.ONE));
		
		Random random = new Random();
		long e = random.nextLong();
		if(e < 1)
		{
			e = e*-1;
		}
		while(! CryptoFunctions.GCD(new BigInteger(e + ""), phi).equals(BigInteger.ONE))
		{
			e = random.nextLong();
			if(e < 1)
			{
				e = e*-1;
			}
		}
		
		BigInteger encryptionExponent = new BigInteger(e + "");
		BigInteger decryptionExponent = CryptoFunctions.extendedGCD(phi,encryptionExponent)[2];
		
		decryptionExponent = decryptionExponent.mod(phi);
		if(decryptionExponent.compareTo(BigInteger.ZERO)== -1)
		{
			decryptionExponent = decryptionExponent.add(phi);
		}
		
		BigInteger[] keys = new BigInteger[3];
		keys[0] = modulus;
		keys[1] = encryptionExponent;
		keys[2] = decryptionExponent;
		return keys;
	}
	
	/**
	 * Encrypts a number (the message) using the public keys. Since encryption is identical to decryption in this scheme,
	 * This method should also be used to decrypt ciphertext using the decryptionExponent in place of the encryptionExponent.
	 * @param message - The number to be encrypted. Must be less than modulus - 1.
	 * @param modulus - One of the public keys.
	 * @param encryptionExponent - Another public key.
	 * @return The ciphertext (another number).
	 */
	public static BigInteger encrypt(BigInteger message, BigInteger modulus, BigInteger encryptionExponent)
	{
		BigInteger ciphertext = message.modPow(encryptionExponent, modulus);
		return ciphertext;
	}
	
	/**
	 * Encrypts this message with this publicKey.
	 * @param message
	 * @param publicKey
	 * @return The cipherText.
	 */
	public static BigInteger encrypt(BigInteger message, PublicKey publicKey)
	{
		return RSA.encrypt(message, publicKey.getModulus(), publicKey.getEncryptionExponent());
	}
}
