package crypto;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Provides several pseudorandom bit generators for use in encryption.
 * @author Ezra Stein
 * @version 1.0
 * @since 2015
 */
public class RandomFunctions
{
	/**
	 * Generates a given number of random 64 bit blocks.
	 * @param seed - A random and secret seed (64 bit).
	 * @param key1 - The first key for EDE DES encryption (64 bit).
	 * @param key2 - The second key for EDE DES encryption (64 bit).
	 * @param blockNumber - The number of blocks to generate.
	 * @return a list of random blocks.
	 */
	public static boolean[][] ANSI(boolean[] seed, boolean[] key1, boolean[] key2, int blockNumber)
	{
		boolean[] intermediateValue = DES.tripleEncrypt(CryptoFunctions.padToBack(CryptoFunctions.toBits(CryptoFunctions.changeBase(""+System.currentTimeMillis(),10,2)),64), key1, key2, key1);
		boolean[] randomBlock;
		boolean[][] randomBlocks = new boolean[blockNumber][0];
		for(int i = 0; i<= blockNumber-1; i++)
		{
			randomBlock = DES.tripleEncrypt(CryptoFunctions.XOR(intermediateValue, seed), key1, key2, key1);
			seed = DES.tripleEncrypt(CryptoFunctions.XOR(randomBlock, intermediateValue), key1, key2, key1);
			randomBlocks[i] = randomBlock;
		}
		return randomBlocks;
	}
	
	/**
	 * Produces a random BigInteger with exactly bitLenght size.
	 * @param bitLength - The bit length.
	 * @param random - A random generator.
	 * @return A random BigInteger
	 */
	public static BigInteger randomBigInteger(int bitLength, SecureRandom random)
	{
		BigInteger randomNum = new BigInteger(bitLength, random);
		BigInteger two = new BigInteger("2");
		while(randomNum.compareTo(two.pow(bitLength-1)) < 0)
		{
			randomNum = new BigInteger(bitLength, random);
		}
		return randomNum;
	}
}
