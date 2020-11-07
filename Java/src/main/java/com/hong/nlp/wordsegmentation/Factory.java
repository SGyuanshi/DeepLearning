package com.hong.nlp.wordsegmentation;

import com.hong.nlp.wordsegmentation.dictionary.Dictionary;
import com.hong.nlp.wordsegmentation.segmentation.AbstractSegmentation;
import com.hong.nlp.wordsegmentation.segmentation.SegmentationAlgorithm;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Factory {

    private static Dictionary dict;
    private static Dictionary gramDict;

    private static Map<String, AbstractSegmentation> pool = new HashMap<>();

    public static void load(){
        if (Configuration.corpusPath == null){
            throw new RuntimeException("语料的路径为空，请通过Configuration.setCorpusPath()进行配置");
        }

        FileReader reader;
        BufferedReader bf;

        try {
            System.out.println("正在加载词典");
            reader = new FileReader(new File(Configuration.corpusPath));
            bf = new BufferedReader(reader);
            String line;
            while ((line = bf.readLine()) != null){
                String[] words = line.split(" ");
                dict.addAll(Arrays.asList(words));
                for (int i=0; i<words.length-1; i++){
                    gramDict.add(words[i] + ":" + words[i+1]);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("语料路径不正确或者格式不对：" + e.getMessage());
        }
        System.out.println("词典加载完成");
    }


    public static void reload(){
        try {
            if (Class.forName(Configuration.dict).isInstance(Dictionary.class)){
                dict = (Dictionary)Class.forName(Configuration.dict).newInstance();
            } else{
                throw new RuntimeException("词典类应为：com.hong.nlp.wordsegmentation.dictionary.Dictionary的子类");
            }
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            throw new RuntimeException("词典类错误");
        }

        try {
            if (Class.forName(Configuration.gramDict).isInstance(Dictionary.class)){
                gramDict = (Dictionary)Class.forName(Configuration.gramDict).newInstance();
            } else{
                throw new RuntimeException("gram词典类应为：com.hong.nlp.wordsegmentation.dictionary.Dictionary的子类");
            }
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            throw new RuntimeException("gram词典类错误");
        }
    }

    public static Dictionary getDictinary(){
        return dict;
    }

    public static Dictionary getGramDictinary(){
        return gramDict;
    }

    public static AbstractSegmentation getSegmentation(SegmentationAlgorithm algorithm){
        String clazz = Configuration.algorithmPackage + algorithm.name();
        AbstractSegmentation segmentation = pool.get(clazz);
        if (segmentation == null){
            try {
                segmentation = (AbstractSegmentation) Class.forName(clazz).newInstance();

            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return segmentation;
    }


    public static AbstractSegmentation getSegmentation(){
        String clazz = Configuration.algorithmPackage + SegmentationAlgorithm.NGram.name();
        AbstractSegmentation segmentation = pool.get(clazz);
        if (segmentation == null){
            try {
                segmentation = (AbstractSegmentation) Class.forName(clazz).newInstance();

            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return segmentation;
    }
}
