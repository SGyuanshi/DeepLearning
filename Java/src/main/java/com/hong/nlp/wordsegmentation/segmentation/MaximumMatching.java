package com.hong.nlp.wordsegmentation.segmentation;

import com.hong.nlp.wordsegmentation.dictionary.HashSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 正向最大匹配法
 */
public class MaximumMatching extends AbstractSegmentation{

    public List<String> seg(String text) {
        List<String> res = new ArrayList<>();
        int maxLen = dictionary.getMaxLength();
        int textLen = text.length();
        int start = 0;

        String str;
        while (textLen > 0){
            // 从最大长度开始匹配，逐步减1
            int len = Math.min(maxLen, textLen);
            while (len > 1){
                str = text.substring(start, start+len);
                if (dictionary.contains(str)){
                    res.add(str);
                    break;
                } else{
                    len -= 1;
                }
            }
            // 没有组成词典中的词，将单个字切分
            if (len == 1){
                res.add(text.substring(start, start+1));
            }
            start += len;
            textLen -= len;
        }
        return res;
    }


    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        MaximumMatching maximumMatching = new MaximumMatching();
        maximumMatching.dictionary = new HashSet();
        List<String> words = Arrays.asList("我们", "这样走");
        maximumMatching.dictionary.addAll(words);

        long s1 = System.currentTimeMillis();
        for (int i=0; i<10000; i++){
            maximumMatching.seg("我们这样走路的是吗哈哈天啊");
        }
        System.out.println(System.currentTimeMillis() - s1);
        System.out.println(maximumMatching.seg("我们这样走路的是吗哈哈天啊"));

    }
}
