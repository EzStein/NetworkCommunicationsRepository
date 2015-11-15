package crypto;

import java.io.Serializable;
import java.math.*;

/**
 * Holds two values. The modulus of the public key and the encryption exponent.
 * @author Ezra
 *
 */
public class PublicKey implements Serializable
{
	/**
	 * ???
	 */
	private static final long serialVersionUID = 1L;
	private BigInteger modulus;
	private BigInteger encryptionExponent;
	
	/**
	 * Constructs a public key from two strings. This class is immutable.
	 * @param modulus
	 * @param encryptionExponent
	 */
	public PublicKey(String modulus, String encryptionExponent)
	{
		this.modulus = new BigInteger(modulus);
		this.encryptionExponent = new BigInteger(encryptionExponent);
	}
	
	/**
	 * Constructs a public key from two BigIntegers.
	 * @param modulus
	 * @param encryptionExponent
	 */
	public PublicKey(BigInteger modulus, BigInteger encryptionExponent)
	{
		this.modulus = modulus;
		this.encryptionExponent = encryptionExponent;
	}
	
	/**
	 * Returns the modulus of this publicKey.
	 * @return the modulus of this publicKey.
	 */
	public BigInteger getModulus()
	{
		return modulus;
	}
	
	/**
	 * Returns the encryption exponent of this publicKey.
	 * @return the encryption exponent of this publicKey.
	 */
	public BigInteger getEncryptionExponent()
	{
		return encryptionExponent;
	}
	
	public String toString()
	{
		return modulus.toString() + " " + encryptionExponent.toString();
	}
}
