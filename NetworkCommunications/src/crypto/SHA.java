package crypto;

import java.math.BigInteger;

/**
 * Defines methods for the Secure Hash Function.
 * @author Ezra Stein
 * @version 1.0
 * @since 2015
 */
public class SHA
{
	private static final boolean[] A = CryptoFunctions.padToFront(CryptoFunctions.toBits(CryptoFunctions.changeBase("67452301", 16,2)),32);
	private static final boolean[] B = CryptoFunctions.padToFront(CryptoFunctions.toBits(CryptoFunctions.changeBase("EFCDAB89", 16,2)),32);
	private static final boolean[] C = CryptoFunctions.padToFront(CryptoFunctions.toBits(CryptoFunctions.changeBase("98BADCFE", 16,2)),32);
	private static final boolean[] D = CryptoFunctions.padToFront(CryptoFunctions.toBits(CryptoFunctions.changeBase("10325476", 16,2)),32);
	private static final boolean[] E = CryptoFunctions.padToFront(CryptoFunctions.toBits(CryptoFunctions.changeBase("C3D2E1F0", 16,2)),32);
	private static final boolean[] K1 = CryptoFunctions.padToFront(CryptoFunctions.toBits(CryptoFunctions.changeBase("5A827999", 16,2)),32);
	private static final boolean[] K2 = CryptoFunctions.padToFront(CryptoFunctions.toBits(CryptoFunctions.changeBase("6ED9EBA1", 16,2)),32);
	private static final boolean[] K3 = CryptoFunctions.padToFront(CryptoFunctions.toBits(CryptoFunctions.changeBase("8F1BBCDC", 16,2)),32);
	private static final boolean[] K4 = CryptoFunctions.padToFront(CryptoFunctions.toBits(CryptoFunctions.changeBase("CA62C1D6", 16,2)),32);
	private static final BigInteger MODULUS;
	static {
		BigInteger two = new BigInteger("2");
		MODULUS = two.pow(32);
	}
	
	/**
	 * Breaks this 512 bit sequence into 16 32 bit blocks
	 * @param bits
	 * @return the sequence split into blocks.
	 */
	public static boolean[][] split16(boolean[] bits)
	{
		boolean[][] blocks = new boolean[16][32];
		int counter = 0;
		for(int i = 0; i<= 15; i++)
		{
			for(int j = 0; j<=31; j++)
			{
				blocks[i][j] = bits[counter];
				counter++;
			}
		}
		return blocks;
	}
	
	/**
	 * Splits the 16 32-bit blocks into 80 32-bit blocks.
	 * @param blocks 16 32-bit blocks.
	 * @return the 80 32-bit blocks.
	 */
	public static boolean[][] split80(boolean[][] blocks)
	{
		boolean[][] splitBlocks = new boolean[80][32];
		for(int i = 0; i<=15; i++)
		{
			splitBlocks[i] = blocks[i];
		}
		for(int i = 16; i<=79; i++)
		{
			boolean[] a = CryptoFunctions.XOR(splitBlocks[i-3], splitBlocks[i-8]);
			boolean[] b = CryptoFunctions.XOR(a, splitBlocks[i-14]);
			boolean[] c = CryptoFunctions.XOR(b, splitBlocks[i-16]);
			splitBlocks[i] = CryptoFunctions.bitShiftLeft(c, 1);
		}
		return splitBlocks;
	}
	
	/**
	 * A convenience method that hashes the plaintext string to a hexadecimal output.
	 * @param text
	 * @return - The hash in hex.
	 */
	public static String hash(String text)
	{
		return CryptoFunctions.changeBase(CryptoFunctions.toString(SHA.hash(CryptoFunctions.toBinary(text))),2,16);
	}
	
	/**
	 * Hashes any sized text by padding it to the right size, and spliting it into blocks of size 512.
	 * @param text
	 * @return the hash (160 bits).
	 */
	public static boolean[] hash(boolean[] text)
	{
		int length = text.length;
		if(length%512 != 448)
		{
			boolean[] one = {true};
			boolean[][] a = {text, one};
			text = CryptoFunctions.join(a);
			if(length%512 < 448)
			{
				text = CryptoFunctions.padToBack(text, length + 448-length%512);
			}
			else
			{
				text = CryptoFunctions.padToBack(text, 512+length + 448-length%512);
			}
		}
		boolean[] lengthInBits = CryptoFunctions.toBaseTwo(new BigInteger(""+length));
		lengthInBits = CryptoFunctions.padToFront(lengthInBits, 64);
		boolean[][] b = {text, lengthInBits};
		text = CryptoFunctions.join(b);
		
		
		boolean[][] blocks = SHA.split512(text);
		boolean[][] IV = {A,B,C,D,E};
		for(boolean[] block: blocks)
		{
			IV = SHA.mainLoop(IV, SHA.split80(SHA.split16(block)));
		}
		return CryptoFunctions.join(IV);
	}
	
	/**
	 * Splits the text, which must be a multiple of 512, into blocks of size 512.
	 * @param text - the text to be split.
	 * @return a list of blocks.
	 */
	public static boolean[][] split512(boolean[] text)
	{
		boolean[][] out = new boolean[(int) Math.floor(text.length/512)][512];
		int counter = 0;
		for(int i = 0; i<= (int) Math.floor(text.length/512)-1; i++)
		{
			for(int j = 0; j<= 511; j++)
			{
				out[i][j] = text[counter];
				counter++;
			}
		}
		return out;
	}
	
	/**
	 * Uses the IV to calculate a hash for this block of 512 bits.
	 * @param IV
	 * @param blocks
	 * @return a new IV for calculating the next block.
	 */
	public static boolean[][] mainLoop(boolean[][] IV, boolean[][] blocks)
	{
		boolean[] temp;
		boolean[] a = IV[0];
		boolean[] b = IV[1];
		boolean[] c = IV[2];
		boolean[] d = IV[3];
		boolean[] e = IV[4];
		for(int i = 0; i<= 19; i++)
		{
			boolean[][] terms = {
					CryptoFunctions.bitShiftLeft(a, 5),
					SHA.functionA(b, c, d),
					e,
					blocks[i],
					K1};
			temp = CryptoFunctions.padToFront(CryptoFunctions.ADD(terms, MODULUS),32);
			e =d;
			d = c;
			c = CryptoFunctions.bitShiftLeft(b, 30);
			b = a;
			a = temp;
		}
		for(int i = 20; i<= 39; i++)
		{
			boolean[][] terms = {
					CryptoFunctions.bitShiftLeft(a, 5),
					SHA.functionB(b, c, d),
					e,
					blocks[i],
					K2};
			temp = CryptoFunctions.padToFront(CryptoFunctions.ADD(terms, MODULUS),32);
			e =d;
			d = c;
			c = CryptoFunctions.bitShiftLeft(b, 30);
			b = a;
			a = temp;
		}
		for(int i = 40; i<= 59; i++)
		{
			boolean[][] terms = {
					CryptoFunctions.bitShiftLeft(a, 5),
					SHA.functionC(b, c, d),
					e,
					blocks[i],
					K3};
			temp = CryptoFunctions.padToFront(CryptoFunctions.ADD(terms, MODULUS),32);
			e =d;
			d = c;
			c = CryptoFunctions.bitShiftLeft(b, 30);
			b = a;
			a = temp;
		}
		for(int i = 60; i<= 79; i++)
		{
			boolean[][] terms = {
					CryptoFunctions.bitShiftLeft(a, 5),
					SHA.functionB(b, c, d),
					e,
					blocks[i],
					K4};
			temp = CryptoFunctions.padToFront(CryptoFunctions.ADD(terms, MODULUS),32);
			e =d;
			d = c;
			c = CryptoFunctions.bitShiftLeft(b, 30);
			b = a;
			a = temp;
		}
		a=CryptoFunctions.padToFront(CryptoFunctions.ADD(a,IV[0], MODULUS),32);
		b=CryptoFunctions.padToFront(CryptoFunctions.ADD(b,IV[1],MODULUS),32);
		c=CryptoFunctions.padToFront(CryptoFunctions.ADD(c,IV[2],MODULUS),32);
		d=CryptoFunctions.padToFront(CryptoFunctions.ADD(d,IV[3],MODULUS),32);
		e=CryptoFunctions.padToFront(CryptoFunctions.ADD(e,IV[4],MODULUS),32);
		boolean[][] out = {a,b,c,d,e};
		return out;
	}
	
	/**
	 * An arbitrary function on x,y,z.
	 * @param x
	 * @param y
	 * @param z
	 * @return the output.
	 */
	public static boolean[] functionA(boolean[] x, boolean[] y, boolean[] z)
	{
		boolean[] a = CryptoFunctions.AND(x, y);
		boolean[] b = CryptoFunctions.AND(CryptoFunctions.NOT(x), z);
		return CryptoFunctions.OR(a,b);
	}
	
	/**
	 * An arbitrary function on x,y,z.
	 * @param x
	 * @param y
	 * @param z
	 * @return the output.
	 */
	public static boolean[] functionB(boolean[] x, boolean[] y, boolean[] z)
	{
		boolean[] a = CryptoFunctions.XOR(x, y);
		return CryptoFunctions.XOR(a, z);
	}
	
	/**
	 * An arbitrary function on x,y,z.
	 * @param x
	 * @param y
	 * @param z
	 * @return the output.
	 */
	public static boolean[] functionC(boolean[] x, boolean[] y, boolean[] z)
	{
		boolean[] a = CryptoFunctions.AND(x, y);
		boolean[] b = CryptoFunctions.AND(x, z);
		boolean[] c = CryptoFunctions.AND(y, z);
		return CryptoFunctions.OR(CryptoFunctions.OR(a, b),c);
	}
}
