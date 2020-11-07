package com.hong.nlp.wordsegmentation.dictionary;

import java.util.Collection;
import java.util.Map;

public class HashMap implements Dictionary{

    private Map<String, Long> map;

    public HashMap(){
        map = new java.util.HashMap<>();
    }

    @Override
    public int getMaxLength() {
        return map.keySet().size();
    }

    @Override
    public boolean contains(String word) {
        return map.containsKey(word);
    }

    @Override
    public void add(String word) {
        if (map.containsKey(word)){
            map.put(word, map.get(word) + 1);
        } else{
            map.put(word, 1L);
        }
    }

    @Override
    public void remove(String word) {
        map.remove(word);
    }

    @Override
    public void addAll(Collection<String> words) {
        for (String word: words){
            add(word);
        }
    }

    @Override
    public void removeAll(Collection<String> words) {
        for (String word: words){
            remove(word);
        }
    }

    @Override
    public long get(String gramWords) {
        if (contains(gramWords)){
            return map.get(gramWords);
        }
        return 0;
    }
}
