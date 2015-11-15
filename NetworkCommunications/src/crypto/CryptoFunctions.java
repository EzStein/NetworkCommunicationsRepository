package crypto;

import java.math.BigInteger;
import java.util.HashMap;

/**
 * Provides a list of static mathematical functions commonly used in cryptography.
 * @author Ezra Stein
 * @version 1.0
 * @since 2015
 *
 */
public class CryptoFunctions
{
	/**
	 * Numbers the upper case letters of the alphabet for use in bases 11 through 36.
	 */
	public static final String[] numberedCharacters= {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
	/**
	 * Hashes each letter to a corresponding number.
	 */
	public static final HashMap<String, Integer> letterToNumber;
	static {
		HashMap<String, Integer> aMap = new HashMap<String,Integer>();
		int i = 10;
		for(String letter: numberedCharacters)
		{
			aMap.put(letter, i);
			i++;
		}
		letterToNumber = aMap;
	}
	
	/**
	 * Converts this list of characters into a string.
	 * @param chars - The list of characters to be converted.
	 * @return a concatenation of chars.
	 */
	public static String toString(char[] chars)
	{
		String out = "";
		for(char character:chars)
		{
			out+=character;
		}
		return out;
	}
	
	/**
	 * Returns whether this string is a number;
	 * @param string
	 * @return true if it can be parsed into a double.
	 */
	public static boolean isNumberic(String string)
	{
		try
		{
			Double.parseDouble(string);
		}
		catch(NumberFormatException nfe)
		{
			return false;
		}
		return true;
	}
	
	/**
	 * Converts this value to base ten.
	 * @param numberBaseA
	 * @param baseA
	 * @return the number as a BigInteger in base ten.
	 */
	private static BigInteger toBaseTen(String numberBaseA, int baseA)
	{
		BigInteger numberBase10 = new BigInteger("1");
		int i = 1;
		for(String s: numberBaseA.split(""))
		{
			if(CryptoFunctions.isNumberic(s))
			{
				numberBase10 = numberBase10.add((new BigInteger(s)).multiply((new BigInteger("" +baseA)).pow(numberBaseA.split("").length - i)));
			}
			else
			{
				numberBase10 = numberBase10.add((new BigInteger(letterToNumber.get(s).toString())).multiply((new BigInteger("" +baseA)).pow(numberBaseA.split("").length - i)));
			}
			i++;
		}
		return numberBase10.subtract(new BigInteger("1"));
	}
	
	/**
	 * Converts the numberBaseA from baseA to baseB.
	 * @param numberBaseA - a number in baseA
	 * @param baseA - The initial base.
	 * @param baseB - The final base.
	 * @return The number in baseB.
	 */
	public static String changeBase(String numberBaseA, int baseA, int baseB)
	{
		if(numberBaseA.equals("0"))
		{
			return "0";
		}
		BigInteger numberBase10 = CryptoFunctions.toBaseTen(numberBaseA, baseA);		
		BigInteger remainder = BigInteger.ZERO;
		String toString = "";
		BigInteger newBase = new BigInteger(baseB+"");
		while(numberBase10.compareTo(new BigInteger((baseB-1)+"")) >= 0)
		{
			remainder = numberBase10.mod(newBase);
			numberBase10 = (numberBase10.divide(newBase));
			if(remainder.compareTo(new BigInteger("9")) <=0)
			{
				toString = remainder + toString;
			}
			else
			{
				toString = numberedCharacters[remainder.intValue()-10] + toString;
			}
		}
		remainder = numberBase10;
		if(remainder.intValue() != 0)
		{
			if(remainder.intValue() <=9)
			{
				toString = remainder + toString;
			}
			else
			{
				toString = numberedCharacters[remainder.intValue()-10] + toString;
			}
		}
		if(toString.equals(""))
		{
			toString = "0";
		}
		return toString;
	}
	
	/**
	 * Converts this binaryData to a BigInteger for ease of use.
	 * @param binaryNumber - A binary number.
	 * @return The equivalent number in base ten.
	 */
	public static BigInteger toBaseTen(boolean[] binaryNumber)
	{
		BigInteger integer = BigInteger.ZERO;
		int counter = binaryNumber.length-1;
		for(boolean bit: binaryNumber)
		{
			if(bit)
			{
				integer =  (integer.add(new BigInteger("" +(int) Math.pow(2, counter))));
			}
			counter--;
		}
		return integer;
	}
	
	/**
	 * Converts this bigInteger to a binary number for ease of use.
	 * @param num - The number to be converted.
	 * @return the base two number.
	 */
	public static boolean[] toBaseTwo(BigInteger num)
	{
		BigInteger remainder;
		String toString = "";
		while(num.compareTo(BigInteger.ONE) >= 0)
		{
			remainder = num.mod(new BigInteger("2"));
			num = num.divide(new BigInteger("2"));
			toString = remainder.toString() + toString;
		}
		remainder = num;
		if(remainder.compareTo(BigInteger.ZERO) != 0)
		{
			toString = remainder + toString;
		}
		return toBits(toString);
	}
	
	/**
	 * Returns this binary data as a string. Only changes the data type. It does not interpret the information.
	 * @param data - A list of bits.
	 * @return The corresponding string.
	 */
	public static String toString(boolean[] data)
	{
		String toString = "";
		for(boolean bit : data)
		{
			if(bit)
			{
				toString = toString + "1";
			}
			else
			{
				toString = toString + "0";
			}
		}
		return toString;
	}
	
	/**
	 * Gives a boolean array to represent this string of bits. Only changes the data type. It does not interpret the information.
	 * @param data - A string of bits.
	 * @return A boolean array of bits.
	 */
	public static boolean[] toBits(String data)
	{
		if(data.length() == 0)
		{
			return new boolean[0];
		}
		boolean[] bits = new boolean[data.length()];
		int i = 0;
		for(String s : data.split(""))
		{
			if(s.equals("0"))
			{
				bits[i] = false;
			}
			else
			{
				bits[i] = true;
			}
			
			i++;
		}
		return bits;
	}
	
	/**
	 * Gives the binary representation of this string.
	 * @param characterSet - A string to be converted.
	 * @return A binary sequence of this string.
	 */
	public static boolean[] toBinary(String characterSet)
	{
		byte[] bytes = characterSet.getBytes();
		StringBuilder binary = new StringBuilder();
		for(byte b : bytes)
		{
			int val = b;
			for(int i = 0; i < 8; i++)
			{
				binary.append((val & 128) == 0 ? 0 : 1);
				val <<= 1;
			}
		}
		return CryptoFunctions.toBits(binary.toString());
	}
	
	/**
	 * Returns the text value of this binary data.
	 * @param data - The data to be converted.
	 * @return The corresponding text.
	 */
	public static String toChars(boolean[] data)
	{
		String s = CryptoFunctions.toString(data);
		String output = "";
		for(int i = 0; i<= s.length() - 8 ; i+= 8)
		{
			int k = Integer.parseInt(s.substring(i,i+8),2);
			output += (char) k;
		}
		return output;
	}
	
	/**
	 * Finds the bitwise AND of the two binary sequences. 1,1 > 1; 1,0 > 0; 0,1 > 0; 0,0 > 0;
	 * @param bitsA - First sequence.
	 * @param bitsB - Second sequence.
	 * @return The AND.
	 */
	public static boolean[] AND(boolean[] bitsA, boolean[] bitsB)
	{
		boolean[] output = new boolean[bitsA.length];
		if(bitsA.length != bitsB.length)
		{
			return null;
		}
		for(int i = 0; i<= bitsA.length-1;i++)
		{
			output[i] = bitsA[i] && bitsB[i];
		}
		return output;
	}
	
	/**
	 * Finds the bitwise OR of the two binary sequences. 1,1 > 1; 1,0 > 1; 0,1 > 1; 0,0 > 0;
	 * @param bitsA - First sequence.
	 * @param bitsB - Second sequence.
	 * @return The OR.
	 */
	public static boolean[] OR(boolean[] bitsA, boolean[] bitsB)
	{
		boolean[] output = new boolean[bitsA.length];
		if(bitsA.length != bitsB.length)
		{
			return null;
		}
		for(int i = 0; i<= bitsA.length-1;i++)
		{
			output[i] = bitsA[i] || bitsB[i];
		}
		return output;
	}
	
	/**
	 * Finds the bitwise XOR of the two binary sequences. 1,1 > 0; 1,0 > 1; 0,1 > 1; 0,0 > 0;
	 * @param bitsA - First sequence.
	 * @param bitsB - Second sequence.
	 * @return The XOR.
	 */
	public static boolean[] XOR(boolean[] bitsA, boolean[] bitsB)
	{
		boolean[] output = new boolean[bitsA.length];
		if(bitsA.length != bitsB.length)
		{
			return null;
		}
		for(int i = 0; i<= bitsA.length-1;i++)
		{
			output[i] = bitsA[i] ^ bitsB[i];
		}
		return output;
	}
	
	/**
	 * Returns a boolean whose value is the complement of this.
	 * @param bits - The bits to be flipped.
	 * @return The complement.
	 */
	public static boolean[] NOT(boolean[] bits)
	{
		boolean[] output = new boolean[bits.length];
		for(int i = 0; i<= bits.length-1; i++)
		{
			output[i] = ! bits[i];
		}
		return output;
	}
	
	/**
	 * Adds the values a and b over the modulus;
	 * @param a
	 * @param b
	 * @param modulus 
	 * @return a + b mod modulus;
	 */
	public static boolean[] ADD(boolean[] a, boolean[] b, BigInteger modulus)
	{
		BigInteger a1 = new BigInteger(CryptoFunctions.changeBase(CryptoFunctions.toString(a),2,10));
		BigInteger b1 =new BigInteger(CryptoFunctions.changeBase(CryptoFunctions.toString(b),2,10));;
		return CryptoFunctions.toBaseTwo(a1.add(b1).mod(modulus));
	}
	
	/**
	 * Sums the terms over modulus.
	 * @param terms
	 * @param modulus 
	 * @return the sum over modulus.
	 */
	public static boolean[] ADD(boolean[][] terms, BigInteger modulus)
	{
		boolean[] out = new boolean[1];
		out[0] = false;
		for(boolean[] term: terms)
		{
			out = CryptoFunctions.ADD(out, term, modulus);
		}
		return out;
	}
	
	/**
	 * Splits the data into blocks of a given length. Pads the last block with zeros until it is the correct length.
	 * @param data - Data to be split.
	 * @param blockLength - the length of each block.
	 * @return a list of blocks.
	 */
	public static boolean[][] split(boolean[] data, int blockLength)
	{
		boolean[][] plaintextBlocks;
		if(data.length%blockLength==0)
		{
			plaintextBlocks = new boolean[data.length/blockLength][blockLength];
		}
		else
		{
			plaintextBlocks = new boolean[(int)Math.floor(data.length/blockLength)+1][blockLength];
		}
		
		int counter = 0;
		for(int i = 0; i<= plaintextBlocks.length-2; i++)
		{
			for(int j = 0; j <= blockLength-1; j++)
			{
				plaintextBlocks[i][j] = data[counter];
				counter++;
			}
		}
		
		boolean[] lastBlock = new boolean[data.length-counter];
		for(int j = 0; j <= data.length-counter-1; j++)
		{
			lastBlock[j] = data[j+counter];
		}
		
		plaintextBlocks[plaintextBlocks.length-1] = CryptoFunctions.padToBack(lastBlock, blockLength);
		return plaintextBlocks;
	}
	
	/**
	 * Permutes the bits to a different order as specified by the permutationArray.
	 * @param bits - The data to be permuted.
	 * @param permutationArray - An array of numbers. Each number in the array (from 1 to the size)
	 * corresponds to a bit number. permutationArray {2,3,1} puts the second bit first, the third bit second and the first bit last.
	 * {1,2,3} is the identity permutation.
	 * @return the permuted data.
	 */
	public static boolean[] permute(boolean[] bits, int[] permutationArray)
	{
		boolean[] permutedBits = new boolean[permutationArray.length];
		for(int i = 0; i<= permutationArray.length - 1; i++)
		{
			permutedBits[i] = bits[permutationArray[i]-1];
		}
		return permutedBits;
	}
	
	/**
	 * Adds zeros to the front of this boolean[] object until it is the correct size.
	 * @param data - data to be padded.
	 * @param size - the size of the result.
	 * @return the padded data at a given size.
	 */
	public static boolean[] padToFront(boolean[] data, int size)
	{
		boolean[] paddedData = new boolean[size];
		for(int i = 0; i<= size - data.length-1; i++)
		{
			paddedData[i] = false;
		}
		
		int counter = size - data.length;
		for(boolean bit : data)
		{
			paddedData[counter] = bit;
			counter++;
		}
		return paddedData;
	}
	
	/**
	 * Adds zeros to the back of this boolean[] object until it is the correct size.
	 * @param data - data to be padded.
	 * @param size - the size of the result.
	 * @return the padded data at a given size.
	 */
	public static boolean[] padToBack(boolean[] data, int size)
	{
		boolean[] paddedData = new boolean[size];
		for(int i = 0; i<= data.length-1; i++)
		{
			paddedData[i] = data[i];
		}
		
		for(int i = data.length; i<= size-1; i++)
		{
			paddedData[i] = false;
		}
		
		return paddedData;
	}
	
	/**
	 * Joins the list of boolean pieces into one data stream.
	 * @param data - the data to be joined.
	 * @return the joined data.
	 */
	public static boolean[] join(boolean[][] data)
	{
		
		int counter = 0;
		for(int i = 0; i<= data.length-1;i++)
		{
			for(int j = 0; j<= data[i].length-1;j++)
			{
				counter++;
			}
			
		}
		boolean[] joinedData = new boolean[counter];
		counter = 0;
		for(int i = 0; i<= data.length-1;i++)
		{
			for(int j = 0; j<= data[i].length-1;j++)
			{
				joinedData[counter] = data[i][j];
				counter++;
			}
			
		}
		
		return joinedData;
	}
	
	/**
	 * Shifts the bits left by a certain number of units.
	 * @param data - Data to be shifted.
	 * @param shiftUnits - Number of units to shift the data. Must be less than the length of the data.
	 * @return The shifted data.
	 */
	public static boolean[] bitShiftLeft(boolean[] data, int shiftUnits)
	{
		boolean[] shiftedData = new boolean[data.length];
		for(int i = 0; i<= data.length-1;i++)
		{
			if(i-shiftUnits<0)
			{
				shiftedData[data.length + i-shiftUnits] = data[i];
			}
			else
			{
				shiftedData[i-shiftUnits] = data[i];
			}
		}
		return shiftedData;
	}
	
	/**
	 * Adds a block to the front of this array.
	 * @param blockArray - The array. 
	 * @param addedBlock - The block to add. Usually an IV.
	 * @return the result of the addition.
	 */
	public static boolean[][] addBlockToFront(boolean[][] blockArray, boolean[] addedBlock)
	{
		boolean[][] result = new boolean[blockArray.length+1][0];
		result[0] = addedBlock;
		for(int i = 0; i<= blockArray.length-1;i++)
		{
			result[i+1] = blockArray[i];
		}
		return result;
	}
	
	/**
	 * Shifts the bits right by a certain number of units.
	 * @param data - Data to be shifted.
	 * @param shiftUnits - Number of units to shift the data. Must be less than the length of the data.
	 * @return The shifted data.
	 */
	public static boolean[] bitShiftRight(boolean[] data, int shiftUnits)
	{
		boolean[] shiftedData = new boolean[data.length];
		for(int i = 0; i<= data.length-1;i++)
		{
			if(i+shiftUnits>=data.length)
			{
				shiftedData[i+shiftUnits-data.length] = data[i];
			}
			else
			{
				shiftedData[i+shiftUnits] = data[i];
			}
		}
		return shiftedData;
	}
	
	/**
	 * Finds the greatest common divisor of two BigInteger numbers.
	 * @param valA
	 * @param valB
	 * @return The greatest common divisor.
	 */
	public static BigInteger GCD(BigInteger valA, BigInteger valB)
	{
		BigInteger remainder;
		if(valA.compareTo(valB)==-1)
		{
			remainder = valB;
			valB = valA;
			valA = remainder;
		}
		
		while(! valB.equals(BigInteger.ZERO))
		{
			remainder = valA.mod(valB);
			valA = valB;
			valB = remainder;
		}
		return valA;
	}
	
	/**
	 * Uses the extended euclidean algorithm to compute the GCD of two BigIntegers as well as two numbers x and y
	 * satisfying valA * x + valB * y = GCD. In this algorithm, valA must be greater than valB.
	 * @param valA
	 * @param valB
	 * @return A list of BigIntegers where element zero is the GCD, and elements 1 and 2 are x and y respectively.
	 */
	public static BigInteger[] extendedGCD(BigInteger valA, BigInteger valB)
	{
		BigInteger x1,x2,y1,y2,x,y,r,q,divisor;
		if(valA.compareTo(valB)==-1)
		{
			System.out.println("ERROR");
		}
		
		x1 = BigInteger.ZERO;
		x2 = BigInteger.ONE;
		y1 = BigInteger.ONE;
		y2 = BigInteger.ZERO;
		while(valB.compareTo(BigInteger.ZERO) == 1)
		{
			q = valA.divide(valB);
			r = valA.subtract(q.multiply(valB));
			x = x2.subtract(q.multiply(x1));
			y = y2.subtract(q.multiply(y1));
			valA = valB;
			valB = r;
			x2 = x1;
			x1 = x;
			y2 = y1;
			y1 = y;
		}
		divisor = valA;
		x = x2;
		y = y2;
		BigInteger[] values = new BigInteger[3];
		values[0] = divisor;
		values[1] = x;
		values[2] = y;
		return values;
	}
}
