package com.mk.ots.wordcenser.service;

/**
 * Created with IntelliJ IDEA.
 * User: jnduan
 * Date: 15/9/11
 * Time: 下午5:59
 */
public interface IWordCensorService {
    public boolean containsInvalidWord(String origin);

    public void addInvalidWord(String word);

    public void removeWord(String word);
}
