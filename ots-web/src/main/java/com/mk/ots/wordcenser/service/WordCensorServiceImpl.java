package com.mk.ots.wordcenser.service;

import com.mk.ots.wordcenser.job.DictionaryUpdater;

import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.NlpAnalysis;
import org.ansj.util.FilterModifWord;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jnduan
 * Date: 15/9/11
 * Time: 下午6:00
 */
@Service
public class WordCensorServiceImpl implements IWordCensorService {

    private static final Logger logger = LoggerFactory.getLogger(WordCensorServiceImpl.class);

    @Autowired
    private DictionaryUpdater updater;

    @PostConstruct
    public void init() {
        NlpAnalysis.parse("热身");
    }

    @Override
    public boolean containsInvalidWord(String origin) {
        if(StringUtils.isNotBlank(origin)) {
            List<Term> terms = NlpAnalysis.parse(origin);
            terms = FilterModifWord.modifResult(terms);
            for(Term t:terms){
                if(DictionaryUpdater.RUDELY_WORD_NATURE.equals(t.getNatureStr())){
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public void addInvalidWord(String word) {
        updater.addWord(word);
    }

    @Override
    public void removeWord(String word) {
        updater.removeWord(word);
    }
}
