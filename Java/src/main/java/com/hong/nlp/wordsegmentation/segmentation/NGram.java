package com.hong.nlp.wordsegmentation.segmentation;

import java.util.List;

public class NGram extends AbstractSegmentation{

    MaximumMatching segmentation = new MaximumMatching();
    ReverseMaximumMatching reverseSegmentation = new ReverseMaximumMatching();

    @Override
    public List<String> seg(String text) {
        List<String> res = segmentation.seg(text);
        long source = getSource(res);

        List<String> res2 = reverseSegmentation.seg(text);
        long source2 = getSource(res2);

        if (source > source2){
            return res;
        }
        return res2;
    }

    /**
     * 通过N-gram算法对分词结果进行消除歧义
     * @param result
     * @return
     */
    private long getSource(List<String> result){
        long source = 0;
        for (int i=0; i<result.size()-1; i++){
            source += gramDictionary.get(result.get(i) + ":" + result.get(i+1));
        }
        return source;
    }
}
