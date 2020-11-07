package com.hong.nlp.wordsegmentation;

import com.hong.nlp.wordsegmentation.segmentation.AbstractSegmentation;
import com.hong.nlp.wordsegmentation.segmentation.SegmentationAlgorithm;

import java.util.List;

public class Segmentation {

    public Segmentation(){
        Factory.load();
    }

    public static List<String> seg(String text, SegmentationAlgorithm algorithm){
        return Factory.getSegmentation(algorithm).seg(text);
    }

    public static List<String> seg(String text){
        return Factory.getSegmentation().seg(text);
    }
}
