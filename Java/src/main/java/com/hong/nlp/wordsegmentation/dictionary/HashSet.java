package com.hong.nlp.wordsegmentation.dictionary;


import java.util.Collection;
import java.util.Set;

public class HashSet implements Dictionary {

    private int maxLength = 0;
    private final Set<String> dict = new java.util.HashSet<>();

    @Override
    public int getMaxLength() {
        return maxLength;
    }

    @Override
    public boolean contains(String word) {
        return dict.contains(word);
    }

    @Override
    public void add(String word) {
        dict.add(word);
        if (word.length() > maxLength){
            maxLength = word.length();
        }
    }

    @Override
    public void remove(String word) {
        dict.remove(word);
    }

    @Override
    public void addAll(Collection<String> words){
        dict.addAll(words);
        for (String word: words){
            if (word.length() > maxLength){
                maxLength = word.length();
            }
        }
    }

    @Override
    public void removeAll(Collection<String> words) {
        dict.removeAll(words);
    }

    @Deprecated
    public long get(String gramWords) {
        return -1;
    }
}
