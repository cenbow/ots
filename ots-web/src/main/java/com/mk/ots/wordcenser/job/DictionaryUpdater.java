package com.mk.ots.wordcenser.job;

import org.ansj.domain.Term;
import org.ansj.library.UserDefineLibrary;
import org.ansj.splitWord.analysis.NlpAnalysis;
import org.ansj.util.FilterModifWord;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created with IntelliJ IDEA.
 * User: jnduan
 * Date: 15/9/11
 * Time: 下午6:02
 */
@Scope(value = "singleton")
@Component
public class DictionaryUpdater {

    public static final String RUDELY_WORD_NATURE = "ru";
    private Logger logger = LoggerFactory.getLogger(DictionaryUpdater.class);

    private ReentrantLock lock = new ReentrantLock();

    @PostConstruct
    public void init() {
        try {
            List<String> words = FileUtils.readLines(new File(DictionaryUpdater.class.getResource("/library/ban.txt").getFile()),"utf-8");
            for(String word:words){
                addWord(word.toLowerCase());
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public static void main(String[] args) {
        DictionaryUpdater du = new DictionaryUpdater();
        du.init();
        List<Term> terms = NlpAnalysis.parse("习近平");
        terms = FilterModifWord.modifResult(terms);
        boolean flag = false;
        for(Term t:terms){
            System.out.println(t.getName());
            System.out.println(t.getNatureStr());
            if(DictionaryUpdater.RUDELY_WORD_NATURE.equals(t.getNatureStr())){
                flag = true;
            }
        }
        System.out.println(flag?"脏":"不脏");
    }

    public void addWord(String word) {
        UserDefineLibrary.insertWord(word, RUDELY_WORD_NATURE, 5000);
    }

    public void removeWord(String word) {
        UserDefineLibrary.removeWord(word);
    }
}
