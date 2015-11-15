package crypto;

/**
 * Contains the implementation of the Data Encryption Standard encryption scheme.
 * Will encrypt 64 bit blocks of data with a 64 bit key.
 * @author Ezra Stein
 * @version 1.0
 * @since 2015
 *
 */
public class DES
{
	private static final int[] PC1 = {57, 49, 41, 33, 25, 17, 9, 1, 58, 50,
									42, 34, 26, 18, 10, 2, 59, 51, 43, 35,
									27, 19, 11, 3, 60, 52, 44, 36, 63, 55,
									47, 39, 31, 23, 15, 7, 62, 54, 46, 38,
									30, 22, 14, 6, 61, 53, 45, 37, 29, 21,
									13, 5, 28, 20, 12, 4};
	
	private static final int[] PC2 = {14, 17, 11, 24, 1, 5, 3, 28, 15, 6, 21,
									10, 23, 19, 12, 4, 26, 8, 16, 7, 27, 20,
									13, 2, 41, 52, 31, 37, 47, 55, 30, 40, 51,
									45, 33, 48, 44, 49, 39, 56, 34, 53, 46, 42,
									50, 36, 29, 32};
	
	private static final int[] IP = {58, 50, 42, 34, 26, 18, 10, 2, 60, 52, 44,
									36, 28, 20, 12, 4, 62, 54, 46, 38, 30, 22,
									14, 6, 64, 56, 48, 40, 32, 24, 16, 8, 57,
									49, 41, 33, 25, 17, 9, 1, 59, 51, 43, 35,
									27, 19, 11, 3, 61, 53, 45, 37, 29, 21, 13,
									5, 63, 55, 47, 39, 31, 23, 15, 7};
	
	private static final int[] IPINVERSE = {40, 8, 48, 16, 56, 24, 64, 32, 39, 7,
										   47, 15, 55, 23, 63, 31, 38, 6, 46, 14,
										   54, 22, 62, 30, 37, 5, 45, 13, 53, 21,
										   61, 29, 36, 4, 44, 12, 52, 20, 60, 28,
										   35, 3, 43, 11, 51, 19, 59, 27, 34, 2, 42,
										   10, 50, 18, 58, 26, 33, 1, 41, 9, 49, 17, 57, 25};
	
	private static final int[] PERMUTATION = {16, 7, 20, 21, 29, 12, 28, 17, 1, 15, 23, 26,
											5, 18, 31, 10, 2, 8, 24, 14, 32, 27, 3, 9, 19, 13,
											30, 6, 22, 11, 4, 25};
	
	
	private static final int[] EXPANSION = {32, 1, 2, 3, 4, 5, 4, 5, 6, 7, 8, 9, 8,
										 9, 10, 11, 12, 13, 12, 13, 14, 15, 16, 17,
										 16, 17, 18, 19, 20, 21, 20, 21, 22, 23, 24,
										 25, 24, 25, 26, 27, 28, 29, 28, 29, 30, 31, 32, 1};
	
	private static final int[][] S1 = {{14, 0, 4, 15}, {4, 15, 1, 12}, {13, 7, 14, 8},
								{1, 4, 8, 2}, {2, 14, 13, 4}, {15, 2, 6, 9},
								{11, 13, 2, 1}, {8, 1, 11, 7}, {3, 10, 15, 5},
								{10, 6, 12, 11}, {6, 12, 9, 3}, {12, 11, 7, 14},
								{5, 9, 3, 10}, {9, 5, 10, 0}, {0, 3, 5, 6}, {7, 8, 0, 13}};
	
	private static final int[][] S2 = {{15, 3, 0, 13}, {1, 13, 14, 8}, {8, 4, 7, 10},
									{14, 7, 11, 1}, {6, 15, 10, 3}, {11, 2, 4, 15},
									{3, 8, 13, 4}, {4, 14, 1, 2}, {9, 12, 5, 11},
									{7, 0, 8, 6}, {2, 1, 12, 7}, {13, 10, 6, 12},
									{12, 6, 9, 0}, {0, 9, 3, 5}, {5, 11, 2, 14}, {10, 5, 15, 9}};
	
	private static final int[][] S3 = {{10, 13, 13, 1}, {0, 7, 6, 10}, {9, 0, 4, 13},
									{14, 9, 9, 0}, {6, 3, 8, 6}, {3, 4, 15, 9},
									{15, 6, 3, 8}, {5, 10, 0, 7}, {1, 2, 11, 4},
									{13, 8, 1, 15}, {12, 5, 2, 14}, {7, 14, 12, 3},
									{11, 12, 5, 11}, {4, 11, 10, 5}, {2, 15, 14, 2}, {8, 1, 7, 12}};
	
	private static final int[][] S4 = {{7, 13, 10, 3}, {13, 8, 6, 15}, {14, 11, 9, 0},
									{3, 5, 0, 6}, {0, 6, 12, 10}, {6, 15, 11, 1}, {9, 0, 7, 13},
									{10, 3, 13, 8}, {1, 4, 15, 9}, {2, 7, 1, 4}, {8, 2, 3, 5},
									{5, 12, 14, 11}, {11, 1, 5, 12}, {12, 10, 2, 7}, {4, 14, 8, 2}, {15, 9, 4, 14}};
	
	private static final int[][] S5 = {{2, 14, 4, 11}, {12, 11, 2, 8}, {4, 2, 1, 12}, {1, 12, 11, 7},
									{7, 4, 10, 1}, {10, 7, 13, 14}, {11, 13, 7, 2}, {6, 1, 8, 13},
									{8, 5, 15, 6}, {5, 0, 9, 15}, {3, 15, 12, 0}, {15, 10, 5, 9},
									{13, 3, 6, 10}, {0, 9, 3, 4}, {14, 8, 0, 5}, {9, 6, 14, 3}};
	
	private static final int[][] S6 = {{12, 10, 9, 4}, {1, 15, 14, 3}, {10, 4, 15, 2}, {15, 2, 5, 12},
									{9, 7, 2, 9}, {2, 12, 8, 5}, {6, 9, 12, 15}, {8, 5, 3, 10},
									{0, 6, 7, 11}, {13, 1, 0, 14}, {3, 13, 4, 1}, {4, 14, 10, 7},
									{14, 0, 1, 6}, {7, 11, 13, 0}, {5, 3, 11, 8}, {11, 8, 6, 13}};
	
	private static final int[][] S7 = {{4, 13, 1, 6}, {11, 0, 4, 11}, {2, 11, 11, 13}, {14, 7, 13, 8},
									{15, 4, 12, 1}, {0, 9, 3, 4}, {8, 1, 7, 10}, {13, 10, 14, 7},
									{3, 14, 10, 9}, {12, 3, 15, 5}, {9, 5, 6, 0}, {7, 12, 8, 15},
									{5, 2, 0, 14}, {10, 15, 5, 2}, {6, 8, 9, 3}, {1, 6, 2, 12}};
	
	private static final int[][] S8 = {{13, 1, 7, 2}, {2, 15, 11, 1}, {8, 13, 4, 14}, {4, 8, 1, 7},
									{6, 10, 9, 4}, {15, 3, 12, 10}, {11, 7, 14, 8}, {1, 4, 2, 13},
									{10, 12, 0, 15}, {9, 5, 6, 12}, {3, 6, 10, 9}, {14, 11, 13, 0},
									{5, 0, 15, 3}, {0, 14, 3, 5}, {12, 9, 5, 6}, {7, 2, 8, 11}};
	
	private static final int[][][] Substitutions = {S1,S2,S3,S4,S5,S6,S7,S8};
	
	/**
	 * Encrypts the data using the Data Encryption Standard and the key. It involves sixteen rounds.
	 * @param data - The bits to be encrypted.
	 * @param key - the key used for encryption.
	 * @return the encrypted data.
	 */
	public static boolean[] encrypt(boolean[] data, boolean[] key)
	{
		if(data.length != 64 || key.length != 64)
		{
			System.out.println("WRONG SIZE");
		}
		data = CryptoFunctions.permute(data, IP);
		
		boolean[] right = new boolean[32];
		boolean[] left = new boolean[32];
		boolean[] newRight = new boolean[32];
		boolean[] newLeft = new boolean[32];
		
		for(int i = 0; i<= 63; i++)
		{
			if(i<=31)
			{
				left[i] = data[i];
			}
			else if(i>=32)
			{
				right[i-32] = data[i];
			}
		}
		
		
		
		for(int i = 1; i<= 16; i++)
		{
			newRight = CryptoFunctions.XOR(left,roundFunction(right, generateSubKey(key, i)));
			newLeft = right;
			
			left = newLeft;
			right = newRight;
		}
		boolean[][] dummy = {right, left};
		data = CryptoFunctions.join(dummy);
		data = CryptoFunctions.permute(data, IPINVERSE);
		return data;
	}
	
	/**
	 * Decrypts the data using the Data Encryption Standard and the key. It involves sixteen rounds.
	 * @param data - The bits to be decrypted.
	 * @param key - the key used for decryption.
	 * @return the decrypted data.
	 */
	public static boolean[] decrypt(boolean[] data, boolean[] key)
	{
		if(data.length != 64 || key.length != 64)
		{
			System.out.println("WRONG SIZE");
		}
		data = CryptoFunctions.permute(data, IP);
		
		
		
		boolean[] right = new boolean[32];
		boolean[] left = new boolean[32];
		boolean[] newRight = new boolean[32];
		boolean[] newLeft = new boolean[32];
		
		
		
		for(int i = 0; i<= 63; i++)
		{
			if(i<=31)
			{
				left[i] = data[i];
			}
			else if(i>=32)
			{
				right[i-32] = data[i];
			}
		}
		for(int i = 1; i<= 16; i++)
		{
			newRight = CryptoFunctions.XOR(left,roundFunction(right, generateSubKey(key, 16-i+1)));
			newLeft = right;
			left = newLeft;
			right = newRight;
		}
		boolean[][] dummy = {right, left};
		data = CryptoFunctions.join(dummy);
		data = CryptoFunctions.permute(data, IPINVERSE);
		return data;
	}
	
	/**
	 * Will generate the sub key for a specified round number;
	 * @param key - the initial 64 bit key.
	 * @param roundNumber - the current round (1-16);
	 * @return the 48 bit sub key for a given round number.
	 */
	public static boolean[] generateSubKey(boolean[] key, int roundNumber)
	{
		boolean[] compressedKey = new boolean[56];
		
		compressedKey = CryptoFunctions.permute(key, PC1);
		boolean[] leftKey =new boolean[28];
		boolean[] rightKey = new boolean[28];
		
		for(int i = 0; i<= 55; i++)
		{
			if(i<=27)
			{
				leftKey[i] = compressedKey[i];
			}
			else if(i>=28)
			{
				rightKey[i-28] = compressedKey[i];
			}
		}
		
		for(int i = 1; i <= roundNumber; i++)
		{
			if(i==1||i==2||i==9||i==16)
			{
				leftKey = CryptoFunctions.bitShiftLeft(leftKey, 1);
				rightKey = CryptoFunctions.bitShiftLeft(rightKey, 1);
			}
			else
			{
				leftKey = CryptoFunctions.bitShiftLeft(leftKey, 2);
				rightKey = CryptoFunctions.bitShiftLeft(rightKey, 2);
			}
		}
		
		boolean[][] dummy = {leftKey, rightKey};
		compressedKey = CryptoFunctions.join(dummy);
		boolean[] subKey = new boolean[48];
		subKey = CryptoFunctions.permute(compressedKey, PC2);
		return subKey;
		
	}
	
	/**
	 * For each round in the encryption process, this function is called. The function involves expansion of the 32 bit half.
	 * A XOR with the subkey, a substitution of the result and a final permutation.
	 * @param data - a 32 bit string.
	 * @param subkey - a 48 bit subkey.
	 * @return the result of this intermediate function.
	 */
	public static boolean[] roundFunction(boolean[] data, boolean[] subkey)
	{
		data = CryptoFunctions.permute(data, EXPANSION);
		data = CryptoFunctions.XOR(data, subkey);
		
		boolean[][] splicedData = new boolean[8][6];
		int counter = 0;
		for(int i = 0; i<=7; i++)
		{
			for(int j = 0; j<=5; j++)
			{
				splicedData[i][j] = data[counter];
				counter++;
			}
		}
		
		boolean[][] compressedData = new boolean[8][4];
		for(int i = 0; i<= 7; i++)
		{	
			int b1;
			int b6;
			
			if(splicedData[i][0])
			{
				b1 = 1;
			}
			else
			{
				b1 = 0;
			}
			
			if(splicedData[i][5])
			{
				b6 = 1;
			}
			else
			{
				b6 = 0;
			}
			int row = 2*b1+b6;
			boolean[] innerBlock = new boolean[4];
			innerBlock[0] = splicedData[i][1];
			innerBlock[1] = splicedData[i][2];
			innerBlock[2] = splicedData[i][3];
			innerBlock[3] = splicedData[i][4];
			int column = Integer.parseInt(CryptoFunctions.changeBase(CryptoFunctions.toString(innerBlock),2,10));
			
			//System.out.println(key.length + " " + right.length);
			compressedData[i] = CryptoFunctions.padToFront(CryptoFunctions.toBits(CryptoFunctions.changeBase(Substitutions[i][column][row] + "", 10, 2)),4);
			
		}
		data = CryptoFunctions.join(compressedData);
		
		data = CryptoFunctions.permute(data, PERMUTATION);
		return data;
	}
	
	/**
	 * Encrypts the text with EDE triple encryption using three keys (64 bits).
	 * @param plaintextBlock - the block of plaintext to be encrypted (64 bits).
	 * @param key1
	 * @param key2
	 * @param key3
	 * @return the ciphertext block.
	 */
	public static boolean[] tripleEncrypt(boolean[] plaintextBlock, boolean[] key1, boolean[] key2, boolean[] key3)
	{
		return encrypt(decrypt(encrypt(plaintextBlock,key1),key2),key3);
	}
}
