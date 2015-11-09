package com.mk.ots.wordcenser.job;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

public class TextFilterUtil {
	// 日志
	private static final Logger LOG = LoggerFactory.getLogger(TextFilterUtil.class);
	// 敏感词库
	private static HashMap sensitiveWordMap = null;
	// 默认编码格式
	private static final String ENCODING = "gbk";
	// 敏感词库的路径

	/**
	 * 初始化敏感词库
	 */
	private static void init() {
		// 读取文件
		Set<String> keyWords = readSensitiveWords();
		// 创建敏感词库
		sensitiveWordMap = new HashMap<>(keyWords.size());
		for (String keyWord : keyWords) {
			createKeyWord(keyWord);
		}
	}

	/**
	 * 构建敏感词库
	 *
	 * @param keyWord
	 */
	private static void createKeyWord(String keyWord) {
		if (sensitiveWordMap == null) {
			LOG.error("sensitiveWordMap 未初始化!");
			return;
		}
		Map nowMap = sensitiveWordMap;
		for (Character c : keyWord.toCharArray()) {
			Object obj = nowMap.get(c);
			if (obj == null) {
				Map<String, Object> childMap = new HashMap<>();
				childMap.put("isEnd", "false");
				nowMap.put(c, childMap);
				nowMap = childMap;
			} else {
				nowMap = (Map) obj;
			}
		}
		nowMap.put("isEnd", "true");
	}

	/**
	 * 读取敏感词文件
	 *
	 * @return
	 */
	private static Set<String> readSensitiveWords() {
		Set<String> keyWords = new HashSet<>();
		try {
			List<String> words = FileUtils.readLines(new File(DictionaryUpdater.class.getResource("/library/ban.txt").getFile()),"utf-8");
			for (String w : words) {
				keyWords.add(w.trim().toLowerCase());
			}
		} catch (UnsupportedEncodingException e) {
			LOG.error("敏感词库文件转码失败!");
		} catch (FileNotFoundException e) {
			LOG.error("敏感词库文件不存在!");
		} catch (IOException e) {
			LOG.error("敏感词库文件读取失败!");
		}
		return keyWords;
	}

	/**
	 * 检查敏感词
	 *
	 * @return
	 */
	public static List<String> checkSensitiveWord(String text) {
		text = text.toLowerCase();
		if (sensitiveWordMap == null) {
			init();
		}
		List<String> sensitiveWords = new ArrayList<>();
		Map nowMap = sensitiveWordMap;
		for (int i = 0; i < text.length(); i++) {
			Character word = text.charAt(i);
			Object obj = nowMap.get(word);
			if (obj == null) {
				continue;
			}
			int j = i + 1;
			Map childMap = (Map) obj;
			if ("true".equals(childMap.get("isEnd"))) {
				sensitiveWords.add(text.substring(i, j));
			}
			while (j < text.length()) {
				obj = childMap.get(text.charAt(j));
				if (obj != null) {
					childMap = (Map) obj;
					if ("true".equals(childMap.get("isEnd"))) {
						sensitiveWords.add(text.substring(i, j+1));
					}
				} else {
					break;
				}
				j++;
			}
		}
		return sensitiveWords;
	}
	
	public static void main(String[] args) {
		System.out.println(TextFilterUtil.checkSensitiveWord("艾滋鱼"));;
	}
}