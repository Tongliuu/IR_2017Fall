package PreProcessData;

/**
 * This is for INFSCI 2140 in 2018
 * 
 * TextTokenizer can split a sequence of text into individual word tokens.
 */
public class WordTokenizer {
	//you can add essential private methods or variables
	private char[] texts;
	private int position=0;

	// YOU MUST IMPLEMENT THIS METHOD
	public WordTokenizer( char[] texts ) {
		// this constructor will tokenize the input texts (usually it is a char array for a whole document)
		this.texts=new char[texts.length];
		for(int i=0;i<texts.length;i++)
			this.texts[i]=texts[i];
	}
	
	// YOU MUST IMPLEMENT THIS METHOD
	public char[] nextWord() {
		// read and return the next word of the document
		// or return null if it is the end of the document
		int ptr=position;
		char[] word;

		//delete (space) and (") until it reaches a letter or digit
		for(;ptr<texts.length-1;ptr++){
			if(!Character.isLetterOrDigit(texts[ptr])) {
				position++;
				continue;
			}
			break;
		}

		//Build and trim a word, delete irrelevant sign
		for(position=ptr;ptr<texts.length-1;ptr++) {
			if(texts[ptr]==' '//reaches a space
					||(!Character.isLetterOrDigit(texts[ptr])&&texts[ptr+1]==' ')//reaches a word end up with a sign (e.g. "," "." "\"") followed by a space
					||(ptr==texts.length-1&&ptr!=position)){//reaches the end of the whole text
				//Any condition mentioned above represent the end of a word
				//ptr(itself not included) is the end of the word, position is the start of the word
				word = new char[ptr - position];//copy and return the pruned word
				for(int i=0;i<ptr-position;i++)
					word[i]=texts[position+i];
				position=ptr;
				return word;
			}
		}

		return null;
	}
	
}
