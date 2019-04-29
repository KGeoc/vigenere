package vigenere;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public class VigenereCipher {

	/**
	 * holds chi table for each position
	 * 
	 * @author qstr2
	 *
	 */
	public class ChiTable {

		int chiValue;
		int shift;
		int posit;

		ChiTable(int chiValue, int shift, int posit) {
			this.chiValue = chiValue;
			this.shift = shift;
			this.posit = posit;
		}

		@Override
		public String toString() {
			return "ChiTable [chiValue=" + chiValue + ", shift=" + shift + ", posit=" + posit + "]";
		}
	}

	public class Dictionary {
		private Set<String> wordsSet;

		public Dictionary() throws IOException {

			Path path = Paths.get("words.txt");
			byte[] readBytes = Files.readAllBytes(path);
			String wordListContents = new String(readBytes, "UTF-8");
			String[] words = wordListContents.split("\n");
			wordsSet = new HashSet<>();
			Collections.addAll(wordsSet, words);
		}

		public boolean contains(String word) {
			return wordsSet.contains(word);
		}
	}

	/**
	 * holds each individuals chi table
	 * 
	 * @author qstr2
	 *
	 */
	private class ChiTablePos {

		private List<ChiTable> chiTable = new ArrayList<>();

		int position;

		ChiTablePos(int position) {
			this.position = position;
		}

		public void addToChi(int chiValue, int shift) {
			chiTable.add(new ChiTable(chiValue, shift, position));
		}

		public int getShift(int x) {
			return chiTable.get(x).shift;
		}

		public void sortChiTable() {

			Collections.sort(chiTable, new Comparator<ChiTable>() {
				@Override
				public int compare(ChiTable obj1, ChiTable obj2) {
					return Integer.compare(obj1.chiValue, obj2.chiValue);
				}
			});

		}

		@Override
		public String toString() {
			return "ChiTablePos [position=" + position + ", chiTable=" + chiTable + "]\n";
		}
	}

	/**
	 * contains number of occurences for individual letter at each position
	 * 
	 * @author qstr2
	 */
	private class IndivCount {

		/**
		 * contains specific letter and its specific occurences
		 * 
		 * @author qstr2
		 */
		private class IndivList {

			public char val;
			public int occurence = 0;

			IndivList(char val) {
				this.val = val;
				occurence++;

			}

			/**
			 * inserts new character into empty IndivList
			 * 
			 * @param x character being inserted
			 */
			IndivList(int x) {
				val = (char) (x);
			}

			/**
			 * verifies that character is the same, if so increases occurence
			 * 
			 * @param x character to be verified
			 * @return
			 */
			public boolean CheckSame(char x) {
				if (val == x) {
					occurence++;
					return true;
				}
				return false;
			}

			public int getOccurence() {
				return occurence;
			}

			@Override
			public String toString() {
				return "[value=" + val + ", occurence=" + occurence + "]\t";
			}
		}

		public List<IndivList> seq = new ArrayList<>();

		/**
		 * location of characters at position 'x' in relation to key length.
		 * 
		 */
		public int location;

		public int totalChars = 0;

		IndivCount(int x) {
			location = x;

			for (int i = 65; i < 91; i++) {
				seq.add(new IndivList(i));
			}
		}

		public boolean addChar(char x) {
			if (x == ' ')
				return false;
			totalChars++;
			for (IndivList temp : seq) {
				if (temp.CheckSame(x)) {
					return true;
				}
			}
			seq.add(new IndivList(x));
			return false;
		}

		/**
		 * get the character in position x of the list and see how far it is from the
		 * letter E
		 * 
		 * @param x character interpreted as a number
		 * @return distance of character x from letter E
		 */
		public int getDistfromE(int x) {
			int dist = seq.get(x).val - 'E';
			return ((dist > 0) ? dist : dist + 26);
		}

		public void sortByAmt() {
			Collections.sort(seq, new Comparator<IndivList>() {
				public int compare(IndivList obj1, IndivList obj2) {
					return Integer.compare(obj2.occurence, obj1.occurence);
				}
			});
		}

		public void sortByLetter() {
			Collections.sort(seq, new Comparator<IndivList>() {
				public int compare(IndivList obj1, IndivList obj2) {
					return Integer.compare(obj1.val, obj2.val);
				}
			});
		}

		@Override
		public String toString() {
			return "indivCount [seq=" + seq + ", location=" + location + "]";
		}

	}

	/**
	 * SeqList contains number of occurences of all String lengths
	 * 
	 * @author qstr2
	 *
	 */
	private class SeqList {

		/**
		 * Sequence contains each occurence for a specific string length
		 * 
		 * @author qstr2
		 *
		 */
		private class Sequence {

			public String seq;
			public int occurence = 1;
			public List<Integer> locations = new ArrayList<>();

			public List<Integer> distance = new ArrayList<>();

			Sequence(String seq, int locat) {
				this.seq = seq;
				locations.add(locat);
			}

			public boolean CheckSame(String x, int locat) {
				if (seq.equals(x)) {
					occurence++;
					locations.add(locat);
					findDistance();
					return true;
				}
				return false;
			}

			public void findDistance() {
				distance.add(locations.get(locations.size() - 1) - locations.get(locations.size() - 2));
				return;
			}

			public List<Integer> getDistances() {
				return locations;
			}

			public int getOccurence() {
				return occurence;
			}

			@Override
			public String toString() {
				return "[seq=" + seq + ", occurence=" + occurence + "]\t";
			}

		}

		public List<Sequence> seq = new ArrayList<>();

		int length;

		SeqList(int x) {
			length = x;
		}

		public boolean addString(String x, int locat) {
			for (Sequence temp : seq) {
				if (temp.CheckSame(x, locat)) {
					return true;
				}
			}
			seq.add(new Sequence(x, locat));
			return false;
		}

		/**
		 * filters a list to remove strings <= minOccurence useful for reducing size
		 * 
		 * @param minOccurence
		 */
		public void filter(int minOccurence) {

			Predicate<Sequence> personPredicate = p -> p.getOccurence() <= minOccurence;
			seq.removeIf(personPredicate);

		}

		/**
		 * returns key length by finding the most occurring phrases in the text block
		 * @return key length
		 */
		public int kasiski() {

			Map<Integer, Integer> map = new HashMap<>();

			List<Integer> relations = new ArrayList<>();
			for (Sequence temp : seq) {
				if (temp.occurence >= 3) {
					for (int i = 1; i < temp.distance.size(); i++) {
						int greatDiv = GCD(temp.distance.get(i), temp.distance.get(0));
						if (greatDiv != 1) {
							if (map.containsKey(greatDiv)) {
								map.put(greatDiv, map.get(greatDiv) + 1);
							} else {
								map.put(greatDiv, 1);
							}

						}
						/*
						 * if(relations.containsKey(greatDiv)){ relations.put(greatDiv,
						 * relations.get(greatDiv) + 1); } else { relations.put(greatDiv, 1); }
						 */

					}

				}
			}
			int maxOccuring = 0;
			int dist = 0;
			for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
				if (entry.getValue() > maxOccuring) {
					maxOccuring = entry.getValue();
					dist = entry.getKey();
				}
			}
			return dist;

		}

		/**
		 * find lcm between numbers and then arrange in lists. Use that to find probable
		 * key length
		 * 
		 * @return
		 */
		public String showDistances() {
			StringBuilder asdf = new StringBuilder();
			for (Sequence temp : seq) {
				if (temp.occurence == 1)
					break;
				else
					asdf.append(temp.seq + temp.distance.toString() + " ");
			}
			return asdf.toString();
		}

		public void sort() {
			Collections.sort(seq, new Comparator<Sequence>() {
				@Override
				public int compare(Sequence obj2, Sequence obj1) {
					return Integer.compare(obj1.occurence, obj2.occurence);
				}
			});
		}

		@Override
		public String toString() {
			StringBuilder asdf = new StringBuilder();
			asdf.append("\nLength " + length + "\n");
			for (Sequence temp : seq) {
				if (temp.occurence == 1)
					break;
				asdf.append(temp.toString());
			}

			return asdf.toString();
		}

		private int GCD(int x, int y) {
			if (y == 0)
				return x;
			return GCD(y, x % y);
		}

	}

	/**
	 * the actual trie. contains bIsEnd to confirm if a word has been reached at
	 * current position
	 * 
	 * @author qstr2
	 *
	 */
	class TrieNode {
		public TrieNode(char ch) {
			value = ch;
			children = new HashMap<>();
			bIsEnd = false;
		}

		public HashMap<Character, TrieNode> getChildren() {
			return children;
		}

		public char getValue() {
			return value;
		}

		public void setIsEnd(boolean val) {
			bIsEnd = val;
		}

		public boolean isEnd() {
			return bIsEnd;
		}

		private char value;
		private HashMap<Character, TrieNode> children;
		private boolean bIsEnd;
	}

	/**
	 * implements a trie for storage of words
	 * 
	 * @author qstr2
	 *
	 */
	public class Trie {
		// Constructor
		public Trie() {
			root = new TrieNode((char) 0);
		}

		/**
		 * Method to insert a new word to Trie
		 * 
		 * @param word new word being inserted
		 */
		public void insert(String word) {

			// Find length of the given word
			int length = word.length();
			TrieNode crawl = root;

			// Traverse through all characters of given word
			for (int level = 0; level < length; level++) {
				HashMap<Character, TrieNode> child = crawl.getChildren();
				char ch = word.charAt(level);

				// If there is already a child for current character of given
				// word
				if (child.containsKey(ch))
					crawl = child.get(ch);
				else // Else create a child
				{
					TrieNode temp = new TrieNode(ch);
					child.put(ch, temp);
					crawl = temp;
				}
			}

			// Set bIsEnd true for last character

			crawl.setIsEnd(true);
		}

		/**
		 * The main method that finds out all words attainable from input
		 * 
		 * @param input String that will be investigated to see if it starts with any
		 *              words
		 * @return stringInt containing how far down the string was traversed, and a
		 *         list containing the location of each word formed
		 */
		public WordDepth getMatchingPrefix(String input) {
			String result = ""; // Initialize resultant string
			int length = input.length(); // Find length of the input string

			List<Integer> wordLocs = new ArrayList<Integer>();
			// Initialize reference to traverse through Trie
			TrieNode crawl = root;
			// Iterate through all characters of input string 'str' and traverse
			// down the Trie
			int level, prevMatch = 0;
			for (level = 0; level < length; level++) {
				// Find current character of str
				char ch = input.charAt(level);

				// HashMap of current Trie node to traverse down
				HashMap<Character, TrieNode> child = crawl.getChildren();

				// See if there is a Trie edge for the current character
				if (child.containsKey(ch)) {
					result += ch; // Update result
					crawl = child.get(ch); // Update crawl to move down in Trie

					// If this is end of a word, then update prevMatch
					if (crawl.isEnd()) {
						prevMatch = level + 1;
						wordLocs.add(prevMatch);
						///////////////// need to add location for words here
					}
				} else
					break;
			}

			// If the last processed character did not match end of a word,
			// return the previously matching prefix
			return new WordDepth(level, wordLocs);

		}

		/**
		 * The main method that finds out all words attainable from input
		 * 
		 * @param input String that will be investigated to see if it starts with any
		 *              words
		 * @return returns only the depth/length of word
		 */
		public int getMatchingPrefixInt(String input) {
			String result = ""; // Initialize resultant string
			int length = input.length(); // Find length of the input string

			// Initialize reference to traverse through Trie
			TrieNode crawl = root;

			// Iterate through all characters of input string 'str' and traverse
			// down the Trie
			int level, prevMatch = 0;
			for (level = 0; level < length; level++) {
				// Find current character of str
				char ch = input.charAt(level);

				// HashMap of current Trie node to traverse down
				HashMap<Character, TrieNode> child = crawl.getChildren();

				// See if there is a Trie edge for the current character
				if (child.containsKey(ch)) {
					result += ch; // Update result
					crawl = child.get(ch); // Update crawl to move down in Trie

					// If this is end of a word, then update prevMatch
					if (crawl.isEnd())
						prevMatch = level + 1;
				} else
					break;
			}

			// If the last processed character did not match end of a word,
			// return the previously matching prefix
			return level;
		}

		private TrieNode root;
	}

	// Testing class
	public Trie dict = new Trie();

	/**
	 * inserts each word into the trie
	 * 
	 * @param fileName Stringname for word dictionary
	 */
	public void implementTrie(String fileName) {

		File file = new File(fileName);
		String line = null;
		try {
			// FileReader reads text files in the default encoding.
			FileReader fileReader = new FileReader(file);

			// Always wrap FileReader in BufferedReader.
			BufferedReader bufferedReader = new BufferedReader(fileReader);

			while ((line = bufferedReader.readLine()) != null) {
				// reads each line and analyzes use of each letter
				dict.insert(line);
			}
			// Always close files.
			bufferedReader.close();
		} catch (FileNotFoundException ex) {
			System.out.println("Unable to open file '" + file + "'");
			return;
		} catch (IOException ex) {
			System.out.println("Error reading file '" + file + "'");
			return;
		}

	}

	private StringBuffer cipheredText = new StringBuffer();;

	private String cipherBet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toLowerCase();

	private String normalBet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toLowerCase();

	private int keyLength;

	/**
	 * occurence of each letter a-z
	 */
	private double[] letterFreq = { 0.08167, 0.01492, 0.02782, 0.04253, 0.12702, 0.02228, 0.02015, 0.06094, 0.06966,
			0.00153, 0.00772, 0.04025, 0.02406, 0.06749, 0.07507, 0.01929, 0.00095, 0.05987, 0.06327, 0.09056, 0.02758,
			0.00978, 0.0236, 0.0015, 0.01974, 0.00074 };

	/**
	 * all strings that are repeated and the amount of their occurrence
	 */
	private List<SeqList> occurenceList = new ArrayList<>();

	private HashMap<Character, Integer> alphaCount = new HashMap<Character, Integer>();

	private List<Integer> moveCharX = new ArrayList<>();

	private List<IndivCount> indivChars = new ArrayList<>();

	private Dictionary wordList;

	private List<ChiTablePos> chiTableIndiv = new ArrayList<>();
	private List<ChiTable> chiTableFull = new ArrayList<>();

	private StringBuffer abridgedText = new StringBuffer();

	public List<Integer> recursiveList = new ArrayList<Integer>();

	public VigenereCipher() {

		loadDictionary();

	}

	public VigenereCipher(String fileName) {
		loadDictionary();
		readAndStore(fileName);
	}

	/**
	 * checks for each occurence of a similar string of text at a length of
	 * occurenceLength
	 * 
	 * @param occurenceLength length of a String to calculate similar text
	 */
	public void checkOccurences(int occurenceLength) {
		int minimumList = 2;
		StringBuffer trimmedCipheredText = new StringBuffer();
		trimmedCipheredText.append(cipheredText.toString().replace(" ", ""));
		// need to remove spaces to allow data to be easily read
		occurenceList.clear();
		for (int i = minimumList; i <= occurenceLength; i++) {
			occurenceList.add(new SeqList(i));
		}
		for (int i = 0; i <= trimmedCipheredText.length() - occurenceLength; i++) {
			// cipherSubStr = trimmedCipheredText.substring(i, i +
			// occurenceLength);

			String subTrimText = trimmedCipheredText.substring(i, i + occurenceLength);
			for (int j = 0; j < occurenceList.size(); j++) {
				// for (SeqList temp : occurenceList) {
				occurenceList.get(j).addString(subTrimText.substring(0, j + minimumList), i);

			}
		}
		for (SeqList temp : occurenceList) {
			temp.sort();
		}

		// System.out.println(occurenceList.toString());

	}

	public double chiSquare() {
		////// TODO chi square mostly works, need to arrange each chi square by
		////// lowest values, put them all in a list
		///// and replace the most recent index with the newest most popular

		int lengthOfIndex;
		double chiValue = 0;
		double lowestChi = 99999;
		int lowestIndex = 0;

		for (int i = 0; i < keyLength; i++) {
			chiTableIndiv.add(new ChiTablePos(i));
		}

		for (int k = 0; k < keyLength; k++) {///// make this workl for each
												///// position
			/// all chi values

			lowestChi = 99999;
			lowestIndex = 0;
			lengthOfIndex = indivChars.get(k).totalChars;
			for (int j = 0; j < 26; j++) {
				chiValue = 0;

				// chi value for every x letter
				for (int i = 0; i < 26; i++) {
					// gets chi value for each character
					chiValue += java.lang.Math
							.pow((indivChars.get(k).seq.get(i).occurence - letterFreq[(i + j) % 25] * lengthOfIndex), 2)
							/ (letterFreq[(i + j) % 25] * lengthOfIndex);
				}
				if (chiValue < lowestChi) {
					lowestChi = chiValue;
					lowestIndex = j;
				}
				chiTableIndiv.get(k).addToChi((int) chiValue, j);

				chiTableFull.add(new ChiTable((int) chiValue, j, k));

				// System.out.println("Chi value is "+chiValue + " position
				// "+k);

			}

			System.out.println("lowest Chi value is " + lowestChi + " Index " + lowestIndex);

		}
		Collections.sort(chiTableFull, new Comparator<ChiTable>() {
			@Override
			public int compare(ChiTable obj1, ChiTable obj2) {
				return Integer.compare(obj1.chiValue, obj2.chiValue);
			}
		});
		for (int i = 0; i < chiTableIndiv.size(); i++) {
			chiTableIndiv.get(i).sortChiTable();
		}
		return 0;
	}

	public String decrypt() {

		for (int i = 0; i < keyLength; i++) {
			moveCharX.add(chiTableIndiv.get(i).getShift(0));
		}

		String asdf = "";
		for (int i = 0; i < moveCharX.size(); i++) {
			System.out.print(getCharForNumber(moveCharX.get(i)));

			asdf += getCharForNumber(moveCharX.get(i));

		}

		for (int h = 0; h < chiTableFull.size(); h++) {
			moveCharX.set(chiTableFull.get(h).posit, chiTableFull.get(h).shift);
			asdf = "";
			for (int i = 0; i < moveCharX.size(); i++) {

				asdf += getCharForNumber(moveCharX.get(i));

			}

			if (levenshteinDistance(asdf, "WEMYROOH") < 7) {
				System.out.print(asdf);
				System.out.println("\n" + levenshteinDistance(asdf, "WEMYROOH"));
				System.out.println(miniDecrypt());
			}
		}
		// System.out.println(findIndexOfCoincidence());
		return " ";
	}

	/**
	 * removes list entries from occurenceList that have only 1 entry
	 */
	public void filter() {
		//
		// System.out.println(occurenceList.get(1).toString());
		occurenceList.get(5).filter(3);
		// System.out.println(occurenceList.get(1).toString());
	}

	public double findIndexOfCoincidence() {
		double index = 0;
		double totalCount = 0;
		for (Integer I : alphaCount.values()) {
			index += (I * (I - 1));
			totalCount += I;
		}
		return ((index / (totalCount * (totalCount - 1))));
	}

	public String findUnique(int length) {
		///// doesn't work as intended
		// length is the maximum size of a word to find
		/// need to get most used word of length length and see if nearby words
		// are similar leven==1
		length -= 2;
		String currentTry = "asdf";
		for (int i = 0; i < occurenceList.get(length).seq.size(); i++) {
			currentTry = occurenceList.get(length).seq.get(i).seq;
			System.out.println(currentTry);
			if (noneSimilar(currentTry)) {
				System.out.println("None Similar, word was " + currentTry);
				break;
			}

			// System.out.println("Word Similar");
		}

		return currentTry;
	}

	public int getKeyLength() {
		return keyLength;
	}

	/**
	 * creates a list for the occurrence of every letter at position dist
	 * 
	 * @param dist distance to separate each list
	 */
	public void indivOccurence(int dist) {
		// creates a list for each i'th letter
		for (int i = 0; i < dist; i++)
			indivChars.add(new IndivCount(i));

		for (int i = 0; i < cipheredText.length(); i++) {
			char ch = cipheredText.charAt(i);
			indivChars.get(i % dist).addChar(ch);
		}
		for (IndivCount temp : indivChars) {
			temp.sortByAmt();
			// System.out.println(temp.location + "\n" + temp.toString());

		}
		/*
		 * for (int i = 0; i < ; i++) { for (IndivCount temp : indivChars) {
		 * moveCharX.add(temp.getDistfromE(i)); }
		 * 
		 * System.out.println("dist from x for seqPos " + i + moveCharX.toString());
		 * System.out.println(decrypt()); moveCharX.clear();
		 * 
		 * }
		 */ // need to take all these guys, put them into a list and then
			// increment
			// each character that many times and see what happens

	}

	/**
	 * inserts spaces after numSpaces of characters
	 * 
	 * @param numSpaces distance to place a space after
	 */
	public void insertSpaces(int numSpaces) {

		if (cipheredText.length() % numSpaces != 0) {
			System.out.println("Number of characters to space does not evenly separate text");
		}

		// System.out.println(ciphBuf.length());
		numSpaces++; // because of the insert of a space, the distance is
		// increased by 1 as well

		for (int i = numSpaces - 1; i < cipheredText.length(); i += numSpaces) {
			cipheredText.insert(i, " ");
		}

	}

/**
 * determines key length using kasiski analysis
 * @param pos position for where kasiski analysis is obtaining results
 * @return key length
 */
	public int kasiski(int pos) {
		return occurenceList.get(pos).kasiski();
	}

	/**
	 * calculates levenshtein disatance
	 * 
	 * @param lhs
	 * @param rhs
	 * @return
	 */
	public int levenshteinDistance(CharSequence lhs, CharSequence rhs) {
		int len0 = lhs.length() + 1;
		int len1 = rhs.length() + 1;

		// the array of distances
		int[] cost = new int[len0];
		int[] newcost = new int[len0];

		// initial cost of skipping prefix in String s0
		for (int i = 0; i < len0; i++)
			cost[i] = i;

		// dynamically computing the array of distances

		// transformation cost for each letter in s1
		for (int j = 1; j < len1; j++) {
			// initial cost of skipping prefix in String s1
			newcost[0] = j;

			// transformation cost for each letter in s0
			for (int i = 1; i < len0; i++) {
				// matching current letters in both strings
				int match = (lhs.charAt(i - 1) == rhs.charAt(j - 1)) ? 0 : 1;

				// computing cost for each transformation
				int cost_replace = cost[i - 1] + match;
				int cost_insert = cost[i] + 1;
				int cost_delete = newcost[i - 1] + 1;

				// keep minimum cost
				newcost[i] = Math.min(Math.min(cost_insert, cost_delete), cost_replace);
			}

			// swap cost/newcost arrays
			int[] swap = cost;
			cost = newcost;
			newcost = swap;
		}

		// the distance is the cost for transforming all letters in both strings
		return cost[len0 - 1];
	}
	//// TODO need to make a program that finds a word that is the largest
	//// unique word.
	/// should have a maximum length of probably 7 need to see if a word of
	//// length+1 is close enough
	/// need to find what values are normal for if there is addition of word,
	//// how this differs to same size
	/// 2 words of the same size would have 2 substitutions. would want to avoid
	//// that.

	/// TODO implement a trie

	/// TODO change from String to StringBuilder
	public void loadDictionary() {
		try {
			wordList = new Dictionary();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private StringBuilder translated = new StringBuilder();
	private int testLength = 40;

	public String miniDecrypt() {
		translated.setLength(0);
		translated.append(abridgedText.toString());
		for (int i = 0; i < translated.length(); i++) {
			if (translated.charAt(i) == ' ')
				continue;

			translated.setCharAt(i, (char) (translated.charAt(i) - moveCharX.get(i % keyLength)));
			if (translated.charAt(i) < 65)
				translated.setCharAt(i, (char) (translated.charAt(i) + 26));
			// TODO fix this?
		}
		// System.out.println(charArray);
		// cipheredText = String.valueOf(charArray);

		// System.out.println(findIndexOfCoincidence());
		return translated.toString();
	}

	public String fullDecrypt() {
		translated.setLength(0);
		translated.append(cipheredText);
		for (int i = 0; i < translated.length(); i++) {
			if (translated.charAt(i) == ' ')
				continue;

			translated.setCharAt(i, (char) (translated.charAt(i) - moveCharX.get(i % keyLength)));
			if (translated.charAt(i) < 65)
				translated.setCharAt(i, (char) (translated.charAt(i) + 26));
			// TODO fix this?
		}
		// System.out.println(charArray);
		// cipheredText = String.valueOf(charArray);

		// System.out.println(findIndexOfCoincidence());
		return translated.toString();
	}

	public boolean noneSimilar(String word) {
		int wordLength = word.length();

		for (int i = wordLength - 1; i < wordLength && i < occurenceList.size(); i++) {
			for (int j = 0; j < occurenceList.get(i).seq.size(); j++) {
				// System.out.print(" Checking against " +
				// occurenceList.get(i).seq.get(j).seq);
				int dist = levenshteinDistance(word, occurenceList.get(i).seq.get(j).seq);
				// System.out.print(" Distance is " + dist);
				if (dist == 1) {
					System.out.println("Similar word was " + occurenceList.get(i).seq.get(j).seq);
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * reads the file and stores lines into StringBuffer cipheredText
	 * 
	 * @param fileName name for file being read
	 * @return true if file was read, false if something happened with file
	 */
	public boolean readAndStore(String fileName) {

		// Can't figure out spacings. That will be a problem in general, for now
		// files are read from Actuals
		// String url = getClass().getResource(fileName).toString();

		File file = new File(fileName);
		String line = null;
		try {
			// FileReader reads text files in the default encoding.
			FileReader fileReader = new FileReader(file);

			// Always wrap FileReader in BufferedReader.
			BufferedReader bufferedReader = new BufferedReader(fileReader);

			while ((line = bufferedReader.readLine()) != null) {
				// reads each line and analyzes use of each letter
				cipheredText.append(line);
			}
			Store();

			// Always close files.
			bufferedReader.close();
		} catch (FileNotFoundException ex) {
			System.out.println("Unable to open file '" + file + "'");
			return false;
		} catch (IOException ex) {
			System.out.println("Error reading file '" + file + "'");
			return false;
		}

		///// displays results\

		return true;

	}

	/**
	 * how far down until a word or possible word is found
	 */
	public int relevantIndex = 10;

	public void preRecursive() {
		for (int i = 0; i < keyLength; i++) {
			moveCharX.add(indivChars.get(i).getDistfromE(0));
		}
		recursiveThing(0);
	}

	/**
	 * wordDepth is composed of the depth of a text that was used
	 * 
	 * @author qstr2
	 *
	 */
	private class WordDepth {

		public int depth;
		public List<Integer> wordLocs = new ArrayList<Integer>();

		WordDepth() {
		}

		WordDepth(int depth, List<Integer> amtWords) {
			this.depth = depth;
			wordLocs = amtWords;
		}

		void removeOldest() {
			wordLocs.remove(0);
		}

	}

	private WordDepth asdf = new WordDepth();

	String originalText;

	int maxDepth = 0;
	int currentDepth = 0;

	/**
	 * makes maxdepth become current depth if larger. should probably be rewritten
	 * as inline
	 */
	private void checkDepth() {
		if (currentDepth > maxDepth) {
			maxDepth = currentDepth;
		}
	}

	private void launchRecursion(int relevantIndex) {

		WordDepth newASDF = dict.getMatchingPrefix(originalText.substring(relevantIndex));
		if (newASDF.wordLocs.isEmpty()) {
			currentDepth += newASDF.depth;
			checkDepth();
			currentDepth -= newASDF.depth;
			return;
		}

		do {
			currentDepth += newASDF.depth;
			checkDepth();
			launchRecursion(relevantIndex + newASDF.wordLocs.get(0));
			currentDepth -= newASDF.depth;

			newASDF.removeOldest();
			if (relevantIndex > 20) {
////Need to get to the point where whole sentence is not passed but only the correct key
				System.out.println(fullDecrypt());
			}
		} while (!newASDF.wordLocs.isEmpty());

		return;
	}

	private void recursiveThing(int index) {
		/// starts with a blank each iteration adds one to array?

		originalText = miniDecrypt();
		launchRecursion(0);

		if (index >= keyLength || index > maxDepth)
			return;

		// System.out.println(recursiveList.toString());

		// send to decrypt

		// }

		for (int i = 0; i < keyLength; i++) {
			currentDepth = 0;
			maxDepth = 0;

			moveCharX.set(index, indivChars.get(index).getDistfromE(i));

			recursiveThing(index + 1);

//			}
		}

	}

	public void removeSpaces() {
		cipheredText = new StringBuffer(cipheredText.toString().replace(" ", ""));
		abridgedText.insert(0, cipheredText.substring(0, testLength));
	}

	public void replaceChars(String repl, String orig) {
		orig = orig.toLowerCase();

		repl = repl.toUpperCase();

		for (int charPos = 0; charPos < orig.length(); charPos++) {

			if (normalBet.indexOf(orig.charAt(charPos)) >= 0)
				normalBet = normalBet.replace(orig.charAt(charPos), repl.charAt(charPos));
			else
				System.out.println("Already used character " + orig.charAt(charPos));
		}
	}

	public String returnOccurences() {
		return occurenceList.toString();
	}

	public void setKeyLength(int keyLength) {
		this.keyLength = keyLength;
	}

	public String showDistances(int length) {
		/// get returns the length after a number, say get(5) returns length 7
		return (occurenceList.get(length - 2).showDistances());
	}

	public String showFrequency() {
		return alphaCount.toString();
	}

	public String Store() {
		alphaCount.clear();

		for (int i = 0; i < cipheredText.length(); i++) {
			char ch = cipheredText.charAt(i);
			/// so pwetty, this code is
			alphaCount.compute(ch, (k, v) -> v == null ? 1 : v + 1);
		}
		return (alphaCount.toString());
	}

	private String getCharForNumber(int i) {
		return i >= 0 && i < 26 ? String.valueOf((char) (i + 65)) : null;
	}

	public static void main(String[] args) {

		VigenereCipher vigen = new VigenereCipher();
		int key = 10;
		//// 74 is martinhairerhasmadeabreakthrough
		// 87 is ohwhyshouldibe
		/// 40 is everyyearthedirector NZQARIKQIWH
		/// 42 is asforthequestionofdifficulty ZUEZNH

		if (vigen.readAndStore("vigenere42.txt") == false)
			return;
		System.out.println();
		vigen.implementTrie("words.txt");
		vigen.implementTrie("names.txt");
		vigen.checkOccurences(7);
		key = vigen.kasiski(2);
		System.out.println(key);
		vigen.setKeyLength(key);
		vigen.removeSpaces();
		vigen.Store();

		// System.out.println(vigen.showFrequency());
		System.out.println(vigen.findIndexOfCoincidence());
		// System.out.println(vigen.returnOccurences());

		vigen.showDistances(3);
		vigen.filter();

		vigen.indivOccurence(key);
		vigen.chiSquare();

		long startTime = System.currentTimeMillis();
		vigen.preRecursive();
		long endTime = System.currentTimeMillis();
		System.out.println("Total execution time: " + (endTime - startTime) + "ms");
		// vigen.recursiveThing(0);

		/*
		 * 14 6 6 4 10 17 9 22 16 18
		 */

		System.out.println(vigen.decrypt());

		//// Martin Hairer has made a breakthrough
		//// correct key is NFFDJQIVPR

	}
}
