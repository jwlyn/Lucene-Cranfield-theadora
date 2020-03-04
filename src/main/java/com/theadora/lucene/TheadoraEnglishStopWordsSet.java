package com.theadora.lucene;

import org.apache.lucene.analysis.CharArraySet;

import java.util.Arrays;
import java.util.List;

/**
 * @author theadora
 */
public class TheadoraEnglishStopWordsSet {
    public static final CharArraySet ENGLISH_STOP_WORDS_SET;

    static {
        final List<String> stopWords = Arrays.asList(
                "a", "an", "and", "are", "as", "at", "be", "but", "by",
                "for", "if", "in", "into", "is", "it",
                "no", "not", "of", "on", "or", "such",
                "that", "the", "their", "then", "there", "these",
                "they", "this", "to", "was", "will", "with","about",
                "above", "after", "because", "can", "do", "from","get",
                "how", "most", "more", "so", "would", "we","when",
                "while", "which", "you"
        );
        final CharArraySet stopSet = new CharArraySet(stopWords, false);
        ENGLISH_STOP_WORDS_SET = CharArraySet.unmodifiableSet(stopSet);
    }
}
