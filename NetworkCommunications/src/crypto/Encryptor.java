package crypto;

/**
 * Contains support for multiple block cipher encryption modes.
 * @author Ezra Stein
 * @version 1.0
 * @since 2015
 */
public class Encryptor
{
	/**
	 * Used if this encryptor uses DES in it modes.
	 */
	public static final int DES_ENCRYPTION = 1;
	
	/**
	 * Encrypts the plaintext using the ECB mode and the given encryption type.
	 * It first splits the text into 64 bit blocks and encrypts each block.
	 * @param plaintext - The text to be encrypted.
	 * @param key - The key used for encryption.
	 * @param encryptionType - The type of encryption to use.
	 * @return the corresponding ciphertext.
	 */
	public static boolean[] ECBMode(boolean[] plaintext, boolean[] key, int encryptionType)
	{
		boolean[][] plaintextBlocks = CryptoFunctions.split(plaintext, 64);
		boolean[][] ciphertextBlocks = new boolean[plaintextBlocks.length][64];
		int counter = 0;
		for(boolean[] block: plaintextBlocks)
		{
			ciphertextBlocks[counter] = encrypt(block,key,encryptionType);
			counter++;
		}
		return CryptoFunctions.join(ciphertextBlocks);
	}
	
	/**
	 * Encrypts the plaintext using the ECB mode and the given encryption type.
	 * It first splits the text into 64 bit blocks and encrypts each block.
	 * @param plaintext - The text to be encrypted.
	 * @param key - The key used for encryption.
	 * @param encryptionType - The type of encryption to use.
	 * @return the corresponding ciphertext.
	 */
	public static String ECBMode(String plaintext, boolean[] key, int encryptionType)
	{
		return CryptoFunctions.toChars(Encryptor.ECBMode(CryptoFunctions.toBinary(plaintext), key, encryptionType));
	}
	
	/**
	 * Encrypts the plaintext using the CBC mode and the given encryption algorithm.
	 * @param plaintext - Plaintext for encryption.
	 * @param key - The key used in encryption.
	 * @param IV - The initialization vector for the mode.
	 * @param encryptionType - Encryption Algorithm used.
	 * @return the ciphertext.
	 */
	public static boolean[] CBCMode(boolean[] plaintext, boolean[] key, boolean[] IV, int encryptionType)
	{
		boolean[][] plaintextBlocks = CryptoFunctions.split(plaintext, 64);
		boolean[][] ciphertextBlocks = new boolean[plaintextBlocks.length+1][64];
		ciphertextBlocks[0] = IV;
		for(int i = 0; i<= plaintextBlocks.length-1; i++)
		{
			ciphertextBlocks[i+1] = encrypt(CryptoFunctions.XOR(plaintextBlocks[i],ciphertextBlocks[i]),key,encryptionType);
		}
		/*Removes the IV from the blocks*/
		ciphertextBlocks[0] = new boolean[0];
		return CryptoFunctions.join(ciphertextBlocks);
	}
	
	/**
	 * Encrypts one block of plaintext using the given encryptionType.
	 * @param plaintextBlock - the plaintext to be encrypted.
	 * @param key - the key for encryption.
	 * @param encryptionType - the type of encryption to use.
	 * @return the ciphertext for one block of plaintext.
	 */
	public static boolean[] encrypt(boolean[] plaintextBlock, boolean[] key, int encryptionType)
	{
		if(encryptionType == DES_ENCRYPTION)
		{
			return DES.encrypt(plaintextBlock, key);
		}
		else
		{
			return null;
		}
	}
}
