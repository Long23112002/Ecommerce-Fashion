package org.example.ecommercefashion.annotations;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import java.io.StringReader;
import java.io.IOException;

public class normalized {
    public static String normalizeString(String input) throws IOException {
        StandardAnalyzer analyzer = new StandardAnalyzer();
        TokenStream tokenStream = analyzer.tokenStream("field", new StringReader(input));
        CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);

        tokenStream.reset();
        StringBuilder normalized = new StringBuilder();
        while (tokenStream.incrementToken()) {
            normalized.append(charTermAttribute.toString()).append(" ");
        }
        tokenStream.end();
        tokenStream.close();

        return normalized.toString().trim().toLowerCase();
    }
}
