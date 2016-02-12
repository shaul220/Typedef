import java.util.Arrays;

public class Main {

	public static void main(String[] args) {
		//String[] words = {"israel", "russia", "france", "belgium", "germany", "jordan"};
		//String[] words = {"red", "green", "yellow", "gray", "white", "black", "blue"};
		//String[] words = {"paris", "london", "amsterdam", "tokyo", "jerusalem", "berlin"};
		//String[] words = {"linear_algebra", "statistics", "arithmetic", "algebra", "combinatorics", "geometry"};
		//String[] words = {"paintbrush", "painting", "sculpture", "photography", "drawing", "museum"};
		//String[] words = {"school", "student", "college", "university", "teacher"};
		//String[] words = {"perl", "javascript", "xml"};
		//String[] words = {"trousers", "shirt", "shoe", "hat", "sock", "shorts"};
		//String[] words = {"car", "motorcycle", "bus"};
		//String[] words = {"water", "cola", "beer", "wine", "vodka", "juice"};
		//String[] words = {"apple", "banana", "tomato", "berry", "apricot", "clementine"};
		String[] words = {"gun", "tank", "sword", "missile", "pistol", "rifle"};
		Subject subject = new Subject(words);
		subject.log();
	}
}
