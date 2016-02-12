import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class DocumentTest {
	
	final static String CONTENT_CLASS = "#mw-content-text";

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			Document doc = Jsoup.connect("https://en.wikipedia.org/wiki/Dog").get();
			Element content = doc.select(CONTENT_CLASS).first();
			Elements elements = content.getElementsByTag("p");
			
			System.out.print(elements.toString());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
