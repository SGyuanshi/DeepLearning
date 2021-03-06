package com.hong.nlp.wordsegmentation.segmentation;

import com.hong.nlp.wordsegmentation.Factory;
import com.hong.nlp.wordsegmentation.dictionary.Dictionary;

import java.util.List;

public abstract class AbstractSegmentation {

    static Dictionary dictionary = Factory.getDictinary();

    static Dictionary gramDictionary = Factory.getGramDictinary();

    public abstract List<String> seg(String text);

//    public abstract List<String> seg(String text, SegmentationAlgorithm algorithm);

}
