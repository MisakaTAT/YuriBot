package com.mikuac.bot.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;


/**
 * @author Zero
 */
@Slf4j
@Component
public class TrieUtils {

    public static void readFile(String filePath) {
        if (StringUtils.isEmpty(filePath)) {
            log.info("敏感词文件路径不能为空");
            return;
        }
        try {
            Resource resource = new ClassPathResource(filePath);
            BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));
            String str;
            while ((str = br.readLine()) != null) {
                addWord(str);
            }
        } catch (Exception e) {
            log.info("读取文件[{}]时发生异常", filePath);
            e.printStackTrace();
        }
    }

    private static class Node {
        // 节点是否为叶子节点的标志；true：叶子节点，false：非叶子节点（拥有子节点的节点）
        public boolean isWord;
        // 当前节点拥有的子节点，使用hashmap进行存储，在查找子节点时的时间复杂度为O(1)
        public HashMap<Character, Node> children;

        public Node(boolean isWord) {
            this.isWord = isWord;
            this.children = new HashMap<>();
        }

        public Node() {
            this(false);
        }
    }

    /**
     * trie树的根节点
     */
    private static Node root;

    /**
     * trie树中拥有多少分枝（多少个敏感词）
     */
    private static int size;

    public static void trie() {
        root = new Node();
        size = 0;
    }

    public static void addWord(String word) {
        // 设置当前节点为根节点
        Node cur = root;
        char[] words = word.toCharArray();

        for (char c : words) {
            // 判断当前节点的子节点中是否存在字符c
            if (!cur.children.containsKey(c)) {
                // 如果不存在则将其添加进行子节点中
                cur.children.put(c, new Node());
            }
            // 当前节点进行变换，变换为新插入到节点 c
            cur = cur.children.get(c);
        }
        // 分枝添加完成后，将分枝中的最后一个节点设置为叶子节点
        if (!cur.isWord) {
            cur.isWord = true;
            // 分枝数（敏感词数）加1
            size++;
        }
    }

    public static boolean contains(String word) {
        Node cur = root;
        char[] words = word.toCharArray();

        for (char c : words) {
            if (!cur.children.containsKey(c)) {
                return false;
            }
            cur = cur.children.get(c);
        }
        // 如果存在并且遍历到trie树中某个分支最后一个节点了，那此节点就是叶子节点，直接返回true
        return cur.isWord;
    }

}
