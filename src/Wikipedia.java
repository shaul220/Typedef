import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Wikipedia {
	
	final String WIKIPEDIA_URL = "https://en.wikipedia.org/wiki/";
	final String CONTENT_CLASS = "#mw-content-text";
	final int FREQUENT_AMOUNT_MIN = 5;
	final int FREQUENT_AMOUNT_MAX = 50;
	final int FREQUENT_PRECENTAGE = 5;
	final int PRECENTAGE_LIMIT = 80;

	private Map<String, Integer> map;
	private Map<String, int[]> docMap;
	private String[] stopWords;
	private String[][] freqWords;
	
	private ProgressBar progress;
	
	public Wikipedia()
	{
		stopWords = getStopWords();
		progress = new ProgressBar();
	}
	
	public Map<String, Integer> getMap()
	{
		return map;
	}
	
	public Map<String, int[]> getDocMap()
	{
		return docMap;
	}
	
	public ArrayList<String> getFrequentWords(String[] words, int amount)
	{
		String[] docs = getContent(words);
		
		mapWords(docs);
		removeStopWords();
		unitePlurals();
		removeKeyWords(words);
		freqWords = new String[words.length][];
		for(int i = 0; i<freqWords.length; i++)
		{
			freqWords[i] = getFrequentWords(i);
			/*
			System.out.print(words[i] + "\n");
			for(int j = 0; j < freqWords[i].length; j++)
			{
				System.out.print(freqWords[i][j] + "\t" + docMap.get(freqWords[i][j])[i] + "\n");
			}
			System.out.print("\n\n");
			*/
		}
		ArrayList<String> frequent = processFrequentWords(freqWords); 
		return frequent;
	}
	
	public ArrayList<String> processFrequentWords(String[][] freqWords)
	{	
		ArrayList<String> frequent = new ArrayList<String>();
		for(int i = 0; i<freqWords.length; i++)
		{
			for(int j = 0; j<freqWords[i].length; j++)
			{
				processFrequentWord(freqWords[i][j], freqWords, frequent);
			}
		}
		return frequent;
	}
	
	public void processFrequentWord(String word, String[][] freqWords, ArrayList<String> frequent)
	{
		double counter = 0;
		if(frequent.contains(word))
			return;
		for(int i = 0; i < freqWords.length; i++)
		{
			counter += contains(freqWords[i], word)?1:0;
		}
		double precent = (counter/freqWords.length)*100;
		if(precent > PRECENTAGE_LIMIT)
		{
			frequent.add(word);
		}
	}
	
	public boolean contains(String[] freqWords, String word)
	{
		for(int i = 0; i < freqWords.length; i++)
		{
			if(word.compareTo(freqWords[i]) == 0)
				return true;
		}
		return false;
	}
	/*
	public void getFrequentWords(String[] freqWords)
	{
		int idx = 0;
		for(Entry<String, Integer> entry:map.entrySet())
		{
			String key = entry.getKey();
			if(idx >= freqWords.length)
			{
				int minimum = getMinimum(freqWords);
				if(map.get(freqWords[minimum]) < map.get(key))
				{
					freqWords[minimum] = key;
				}
			}
			else
			{
				freqWords[idx] = key;
				idx++;
			}
		}
		
		sortFreqWords(freqWords);
	}
	*/
	public String[] getFrequentWords(int index)
	{
		int frequentAmount = getFrequentAmount(index);
		String[] freqWords = new String[frequentAmount];
		int idx = 0;
		for(Entry<String, int[]> entry:docMap.entrySet())
		{
			String key = entry.getKey();
			if(docMap.get(key) == null)
				continue;
			if(idx >= freqWords.length)
			{
				int minimum = getMinimum(freqWords, index);
				if(docMap.get(freqWords[minimum])[index] < docMap.get(key)[index])
				{
					freqWords[minimum] = key;
				}
			}
			else
			{
				freqWords[idx] = key;
				idx++;
			}
		}
		
		return freqWords;
	}
	
	public int getFrequentAmount(int index)
	{
		double counter = 0;
		for(Entry<String, int[]> entry : docMap.entrySet())
		{
			if(entry.getValue() != null && entry.getValue()[index] != 0)
			{
				counter++;
			}
		}
		double amount = counter * ((double)FREQUENT_PRECENTAGE/100);
		if(amount < FREQUENT_AMOUNT_MIN)
			amount = FREQUENT_AMOUNT_MIN;
		else if(amount > FREQUENT_AMOUNT_MAX)
			amount = FREQUENT_AMOUNT_MAX;
		//System.out.print(amount + "\t" + counter + "\n");
		return (int)amount;
	}
	
	public void sortFreqWords(String[] freqWords)
	{
		for(int i = 0; i<freqWords.length; i++)
		{
			for(int j = 0; j<freqWords.length-1; j++)
			{
				if(map.get(freqWords[j]) < map.get(freqWords[j+1]))
				{
					swap(freqWords, j, j+1);
				}
			}
		}
	}
	
	public void swap(String[] freqWords, int i, int j)
	{
		String tmp = freqWords[i];
		freqWords[i] = freqWords[j];
		freqWords[j] = tmp;
	}
	
	/*
	public int getMinimum(String[] freqWords)
	{
		int min = 0;
		for(int i = 1; i<freqWords.length; i++)
		{
			if(map.get(freqWords[min]) > map.get(freqWords[i]))
			{
				min = i;
			}
		}
		return min;
	}
	*/
	
	public String[][] getDocFreqWords()
	{
		return freqWords;
	}
	
	public int getMinimum(String[] freqWords, int index)
	{
		int min = 0;
		for(int i = 1; i<freqWords.length; i++)
		{
			if(docMap.get(freqWords[min]) == null)
			{
				//docMap.remove(freqWords[min]);
				return min;
			}
			if(docMap.get(freqWords[i]) == null)
			{
				//docMap.remove(freqWords[i]);
				return i;
			}
			if(docMap.get(freqWords[min])[index] > docMap.get(freqWords[i])[index])
			{
				min = i;
			}
		}
		return min;
	}
	
	public Map<String, Integer> mapWords(String[] docs)
	{
		map = new HashMap<String,Integer>();
		docMap = new HashMap<String, int[]>();
		for(int i = 0; i<docs.length; i++)
		{
			mapWords(docs[i], i, docs.length);
		}
		return map;
	}
	
	private void mapWords(String doc, int index, int length) {
		String[] words = doc.split(" ");
		for(int i = 0; i<words.length; i++)
		{
			if(!words[i].isEmpty() && words[i].length()>2 && words[i].length()<10)
			{
				if(docMap.get(words[i]) == null)
				{
					int[] values = new int[length];
					values[index] = 1;
					docMap.put(words[i], values);
				}
				else
				{
					docMap.get(words[i])[index]++;
				}
				if(map.get(words[i]) == null)
					map.put(words[i], 1);
				else
					map.put(words[i], map.get(words[i]) + 1);
			}	
		}
	}
	/*
	private void mapWords(String doc) {
		String[] words = doc.split(" ");
		for(int i = 0; i<words.length; i++)
		{
			if(!words[i].isEmpty() && words[i].length()>2 && words[i].length()<10)
			{
				if(map.get(words[i]) == null)
				{
					map.put(words[i], 1);
				}
				else
				{
					map.put(words[i], map.get(words[i]) + 1);
				}
			}	
		}
	}
	*/

	public String[] clean(String[] content)
	{
		String[] cleanContent = new String[content.length];
		
		for(int i = 0; i<cleanContent.length; i++)
		{
			progress.setCurrentProcess("Cleaning Document " + (i+1));
			cleanContent[i] = clean(content[i]);
			
		}
		return cleanContent;
	}
	
	public String clean(String content)
	{
		String cleanContent = "";
		
		boolean read = true;
		for(int i = 0; i<content.length(); i++)
		{
			if((read && content.charAt(i) == '<') || (!read && content.charAt(i) == '>'))
			{
				read = !read;
			}
			else if(read)
			{
				if(content.charAt(i) == '\n')
					cleanContent += ' ';
				else
					cleanContent += content.charAt(i);
			}
			double currProgress = ((double)(i+1)/content.length())*100;
			progress.showProgress((int)currProgress);
		}
		cleanContent = cleanContent.replaceAll("[^a-zA-Z\\s]", "");
		return cleanContent;
	}
	
	public String[] getContent(String[] words)
	{
		return clean(getDocuments(words));
	}
	
	public String[] getDocuments(String[] words)
	{
		String[] docs = new String[words.length];
		progress.setCurrentProcess("Fetching Documents");
		for(int i = 0; i<docs.length; i++)
		{
			docs[i] = getDocument(words[i]).select(CONTENT_CLASS).first().getElementsByTag("p").toString().replace('\n', ' ').toLowerCase();
			double currProgress = ((double)(i+1)/docs.length)*100;
			progress.showProgress((int)currProgress);
		}
		return docs;
	}
	
	public Document getDocument(String word)
	{
		Document doc = null;
		try {
			doc = Jsoup.connect(WIKIPEDIA_URL + word).get();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return doc;
	}
	
	private String[] getStopWords() {
		String text = "";
		try {
			BufferedReader reader = new BufferedReader(new FileReader("/Users/Shaul/Desktop/ויצמן/Nitzan/src/StopWords.txt"));
			String line = "";
			while((line = reader.readLine()) != null)
			{
				text+=line + " ";
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String[] words = text.split(" ");
		return words;
	}
	
	public void removeStopWords()
	{
		for(int i = 0; i<stopWords.length; i++)
		{
			map.remove(stopWords[i]);
			docMap.remove(stopWords[i]);
		}
	}
	
	public void removeKeyWords(String[] words)
	{
		for(int i = 0; i<words.length; i++)
		{
			String word = words[i].toLowerCase();
			map.remove(word);
			docMap.remove(word);
		}
	}
	
	public void unitePlurals()
	{
		for(Entry<String, Integer> entry:map.entrySet())
		{
			String key = entry.getKey();
			String pluralKey = plural(key);
			if(map.get(pluralKey) != null && docMap.get(pluralKey) != null)
			{
				map.put(key, map.get(pluralKey) + map.get(key));
				map.put(pluralKey, 0);
				for(int i = 0; i<docMap.get(key).length; i++)
				{
					docMap.get(key)[i] += docMap.get(pluralKey)[i];
				}
				docMap.put(pluralKey, null);
			}
		}
	}
	
	public String plural(String key)
	{
		int length = key.length();
		if(length<2)
			return "";
		if(key.charAt(length-1) == 's')
		{
			return key + "es";
		}
		else if(key.charAt(length-1) == 'y')
		{
			char pre = key.charAt(length-2);
			if(pre == 'a' || pre == 'u' || pre == 'i' || pre == 'o' || pre == 'e')
			{
				return key + "s";
			}
			else
			{
				return key.substring(0, length-1) + "ies";
			}
		}
		else
		{
			return key + "s";
		}
	}	
}
