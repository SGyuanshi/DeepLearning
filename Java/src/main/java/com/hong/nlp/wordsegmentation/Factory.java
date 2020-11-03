package com.hong.nlp.wordsegmentation;

import com.hong.nlp.wordsegmentation.dictionary.Dictionary;
import com.hong.nlp.wordsegmentation.segmentation.AbstractSegmentation;

import java.util.Map;

public class Factory {

    private static String dict = "com.hong.nlp.wordsegmentation.dictionary.HashSet";

    private static String gramDict = "com.hong.nlp.wordsegmentation.dictionary.HashMap";

    private static String algorithm = "com.hong.nlp.wordsegmentation.segmentation.NGram";

    private static Map<String, >;

    public static Dictionary getDictinary(){
        try {
            if (Class.forName(dict).isInstance(Dictionary.class)){
                return (Dictionary)Class.forName(dict).newInstance();
            } else{
                throw new RuntimeException("词典类应为：com.hong.nlp.wordsegmentation.dictionary.Dictionary的子类");
            }
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            throw new RuntimeException("词典类错误");
        }
    }

    public static Dictionary getGramDictinary(){
        try {
            if (Class.forName(gramDict).isInstance(Dictionary.class)){
                return (Dictionary)Class.forName(gramDict).newInstance();
            } else{
                throw new RuntimeException("gram词典类应为：com.hong.nlp.wordsegmentation.dictionary.Dictionary的子类");
            }
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            throw new RuntimeException("gram词典类错误");
        }
    }

    public static String seg(AbstractSegmentation segmentation, String text){
        Factory.segmentation = segmentation;
    }

    public static AbstractSegmentation getSegmentation(){
        return null;
    }
}
