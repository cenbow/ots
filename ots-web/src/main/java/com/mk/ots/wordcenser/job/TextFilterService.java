package com.mk.ots.wordcenser.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TextFilterService {
	// 日志
	private static final Logger LOG = LoggerFactory.getLogger(TextFilterService.class);
	// 敏感词库
	private static HashMap sensitiveWordMap = null;
	// 默认编码格式
	private static final String ENCODING = "gbk";
	// 敏感词库的路径
	@Autowired
	NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	/**
	 * 初始化敏感词库
	 */
	private void init() {
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
	private Set<String> readSensitiveWords() {
		Set<String> keyWords = new HashSet<>();
		String sql = "select id, word from t_sensitive_words order by id asc";
		List<Map<String, Object>> words = namedParameterJdbcTemplate.queryForList(sql, new HashMap());
		for (Map<String, Object> word : words) {
			String s = (String)word.get("word");
			keyWords.add(s.toLowerCase());
		}
		return keyWords;
	}

	/**
	 * 检查敏感词
	 *
	 * @return
	 */
	public List<String> checkSensitiveWord(String text) {
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
	
	public void reload(){
		LOG.error("sensitiveWordMap reload!");
		init();
		LOG.error("sensitiveWordMap ok!");
	}
	
	public static void main(String[] args) {
		TextFilterService filter = new TextFilterService();
		System.out.println(filter.checkSensitiveWord("油炸艾滋鱼艹哈哈逼"));
	}
}