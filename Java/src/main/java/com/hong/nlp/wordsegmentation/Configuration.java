package com.hong.nlp.wordsegmentation;

public final class Configuration {

    static String dict = "com.hong.nlp.wordsegmentation.dictionary.HashSet";

    static String gramDict = "com.hong.nlp.wordsegmentation.dictionary.HashMap";

    static String algorithmPackage = "com.hong.nlp.wordsegmentation.segmentation.";

    static String corpusPath;

    public static String getDict() {
        return dict;
    }

    public static void setDict(String dict) {
        Configuration.dict = dict;
    }

    public static String getGramDict() {
        return gramDict;
    }

    public static void setGramDict(String gramDict) {
        Configuration.gramDict = gramDict;
    }

    public static String getAlgorithmPackage() {
        return algorithmPackage;
    }

    public static void setAlgorithmPackage(String algorithmPackage) {
        Configuration.algorithmPackage = algorithmPackage;
    }

    public static String getCorpusPath() {
        return corpusPath;
    }

    public static void setCorpusPath(String corpusPath) {
        Configuration.corpusPath = corpusPath;
    }
}
