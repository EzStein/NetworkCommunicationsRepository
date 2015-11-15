package crypto;

/**
 * Will decrypt ciphertext under different modes.
 * @author Ezra Stein
 * @version 1.0
 * @since 2015
 */
public class Decryptor
{
	/**
	 * Refernces the use of DES for decryption.
	 */
	public static final int DES_DECRYPTION = 1;
	
	/**
	 * Decrypts the ciphertext using the ECB mode and the given decryption technique.
	 * @param ciphertext - ciphertext to be decrypted.
	 * @param key - key for decryption.
	 * @param decryptionType - the type of decryption algorithm.
	 * @return the plaintext.
	 */
	public static boolean[] ECBMode(boolean[] ciphertext, boolean[] key, int decryptionType)
	{
		boolean[][] ciphertextBlocks = CryptoFunctions.split(ciphertext, 64);
		boolean[][] plaintextBlocks = new boolean[ciphertextBlocks.length][64];
		int counter = 0;
		for(boolean[] block: ciphertextBlocks)
		{
			plaintextBlocks[counter] = decrypt(block,key,decryptionType);
			counter++;
		}
		return CryptoFunctions.join(plaintextBlocks);
	}
	
	/**
	 * Decrypts the ciphertext using the ECB mode and the given decryption technique.
	 * @param ciphertext - ciphertext to be decrypted.
	 * @param key - key for decryption.
	 * @param decryptionType - the type of decryption algorithm.
	 * @return the plaintext.
	 */
	public static String ECBMode(String ciphertext, boolean[] key, int decryptionType)
	{
		return CryptoFunctions.toChars(Decryptor.ECBMode(CryptoFunctions.toBinary(ciphertext), key, decryptionType));
	}
	
	/**
	 * Decrypts the ciphertext using the CBC mode, the key, the decryption algorithm and the IV.
	 * @param ciphertext - ciphertext to be decrypted.
	 * @param key - key for decryption.
	 * @param IV - The initialization vector.
	 * @param decryptionType - the type of decryption algorithm.
	 * @return the plaintext.
	 */
	public static boolean[] CBCMode(boolean[] ciphertext, boolean[] key, boolean[] IV, int decryptionType)
	{
		boolean[][] ciphertextBlocks = CryptoFunctions.split(ciphertext, 64);
		boolean[][] plaintextBlocks = new boolean[ciphertextBlocks.length][64];
		ciphertextBlocks = CryptoFunctions.addBlockToFront(ciphertextBlocks, IV);
		for(int i = 0; i<= plaintextBlocks.length-1; i++)
		{
			plaintextBlocks[i] = CryptoFunctions.XOR(ciphertextBlocks[i],decrypt(ciphertextBlocks[i+1],key,decryptionType));
		}
		return CryptoFunctions.join(plaintextBlocks);
	}
	
	/**
	 * Decrypts one block of ciphertext using the given decryptionType.
	 * @param ciphertextBlock - the ciphertext to be decrypted.
	 * @param key - the key for decryption.
	 * @param decryptionType - the type of decryption to use.
	 * @return the plaintext for one block of ciphertext.
	 */
	public static boolean[] decrypt(boolean[] ciphertextBlock, boolean[] key, int decryptionType)
	{
		if(decryptionType == DES_DECRYPTION)
		{
			return DES.decrypt(ciphertextBlock, key);
		}
		else
		{
			return null;
		}
	}
}
