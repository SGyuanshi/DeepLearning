package com.hong.nlp.wordsegmentation.dictionary;

import java.util.Collection;

/**
 * remove...从词典中删除单词时可能会引起maxLength的变化
 */
public interface Dictionary {

    /**
     * 获取词典所有词的最大长度
     * @return
     */
    public int getMaxLength();

    /**
     * 判断一个词是否在词典中
     * @param word
     * @return
     */
    public boolean contains(String word);

    /**
     * 向词典中增加新的词
     * @param word
     */
    public void add(String word);

    /**
     * 删除词典中特定的词
     * @param word
     */
    public void remove(String word);

    /**
     * 批量往词典中加入单词
     * @param words
     */
    public void addAll(Collection<String> words);

    /**
     * 批量删除词典中的单词
     * @param words
     */
    public void removeAll(Collection<String> words);
}
