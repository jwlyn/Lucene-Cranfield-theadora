package com.theadora.lucene;

import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;


/**
 * @author theadora
 */
public class TheadoraAnalyzer extends StopwordAnalyzerBase {

	private int tokenLength = 255;

	public TheadoraAnalyzer(CharArraySet stopwords) {
		super(stopwords);
	}

	public void setMaxTokenLength(int length) {
		tokenLength = length;
	}

	public int getMaxTokenLength() {
		return tokenLength;
	}

	public CharArraySet getStopwords() {
		return stopwords;
	}

	@Override
	protected TokenStreamComponents createComponents(final String fieldName) {

		final Tokenizer src;
		StandardTokenizer t = new StandardTokenizer();
		t.setMaxTokenLength(tokenLength);
		src = t;

		TokenStream tok = new LowerCaseFilter(src);
		tok= new PorterStemFilter(tok);
		tok = new StopFilter(tok, TheadoraEnglishStopWordsSet.ENGLISH_STOP_WORDS_SET);

		return new TokenStreamComponents(t,tok);
	}

}
