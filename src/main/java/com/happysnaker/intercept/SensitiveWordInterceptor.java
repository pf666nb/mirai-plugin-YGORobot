package com.happysnaker.intercept;

import com.happysnaker.config.RobotConfig;
import com.happysnaker.context.Context;
import com.happysnaker.handler.handler;
import com.happysnaker.handler.message.GroupMessageEventHandler;
import com.happysnaker.utils.OfUtil;
import com.happysnaker.utils.RobotUtil;
import com.sun.org.apache.bcel.internal.generic.RETURN;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageSource;
import net.mamoe.mirai.message.data.QuoteReply;

import java.util.*;

/**
 * 关键词检测
 *
 * @author Happysnaker
 * @description
 * @date 2022/7/2
 * @email happysnaker@foxmail.com
 */
@intercept
public class SensitiveWordInterceptor implements Interceptor {
    private Trie trie;

    private boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
            return true;
        }
        return false;
    }

    public SensitiveWordInterceptor() {
        trie = new Trie();
        for (String s : RobotConfig.sensitiveWord) {
            trie.insert(s);
        }
    }


    public boolean containsSensitiveWord(char[] text, int from) {
        int n = text.length;
        boolean skip = RobotConfig.skipIsMeaninglessWord;
        int skipStep = RobotConfig.skipStep;
        Trie node = trie;
        for (int i = from; i < n; i++) {
            char ch = text[i];
            // 如果没检测到，但是字符可以忽略，则忽略
            if (node.nextNode(ch) == null) {
                if (skip && !isChinese(ch) && !Character.isLetterOrDigit(ch)) {
                    continue;
                } else if (skipStep-- > 0) {
                    continue;
                } else {
                    return false;
                }
            }
            node = node.nextNode(ch);
            if (node.isStringEnd()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean interceptBefore(MessageEvent event) {
        if (!(event instanceof GroupMessageEvent)) {
            return false;
        }
        GroupMessageEvent groupEvent = (GroupMessageEvent) event;
        String gid = String.valueOf(groupEvent.getGroup().getId());
        if (!RobotConfig.enableSensitiveWordDetection.contains(gid)) {
            return false;
        }
        char[] str = RobotUtil.getOnlyPlainContent(event).trim().toCharArray();
        int n = str.length, count = 0;
        for (int i = 0; i < n; i++) {
            if (containsSensitiveWord(str, i)) {
                count++;
                if (count >= RobotConfig.withdrawalThreshold) {
                    MessageChain chain = RobotUtil.buildMessageChain(
                            new At(event.getSender().getId()),
                            "\n警告，你的发言违反了群内相关规定，请不要发布色情及政治相关言论"
                    );
                    event.getSubject().sendMessage(chain);
                    MessageSource.recall(event.getSource());
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public List<MessageChain> interceptAfter(MessageEvent e, List<MessageChain> m) {
        return m;
    }


    /**
     * 字典树模板
     */
    public class Trie {
        private Map<Character, Trie> children;
        private Trie prevNode;
        private int prefixCount;
        private int fullCount;
        private String val;

        public Trie() {
            children = new HashMap<>();
        }

        private Trie(Trie prevNode) {
            this.prevNode = prevNode;
            children = new HashMap<>();
        }

        /**
         * 获取树中包含以该节点结尾的前缀数目，这个函数应该与 nextNode函数 搭配使用，因为树的总头节点本身不具备任何信息，因此使用树的头节点操作该函数是毫无意义的
         *
         * @return 前缀字符串的数目
         */
        public int getPrefixCount() {
            return prefixCount;
        }

        /**
         * 获取包含以该节点结尾字符串的数目，这个函数应该与 nextNode函数 搭配使用，树因为的总头节点本身不具备任何信息，因此使用树的头节点操作该函数是毫无意义的
         *
         * @return 字符串的数目
         */
        public int getFullCount() {
            return fullCount;
        }

        /**
         * @return 返回以该节点结尾的字符串，如果树中不包含该字符串则返回null
         */
        public String getStringVal() {
            return val;
        }

        /**
         * 当前节点是否作为一个完整字符串的结尾，这个函数应该与 nextNode函数 搭配使用，树因为的总头节点本身不具备任何信息，因此使用树的头节点操作该函数是毫无意义的
         *
         * @return 是则返回 true,否则返回false
         */
        public boolean isStringEnd() {
            return fullCount > 0;
        }

        /**
         * 获取对应下一个字符的节点
         *
         * @param ch 待输入的字符
         * @return 返回下一层树节点，如果没有则返回 null。
         */
        public Trie nextNode(char ch) {
            return this.children.getOrDefault(ch, null);
        }

        /**
         * 获取字典树中匹配字符串 str 的树节点
         *
         * @param str 待搜索的字符串
         * @return 返回字典树中匹配字符串 str 的树节点
         */
        public Trie getNode(String str) {
            Trie node = this;
            for (int i = 0; i < str.length(); i++) {
                char ch = str.charAt(i);
                if (node.children.get(ch) == null) {
                    return null;
                }
                node = node.children.get(ch);
            }
            return node;
        }

        /**
         * 向树中插入一个字符串
         *
         * @param word 待插入的字符串
         */
        public void insert(String word) {
            Trie cur = this;
            for (int i = 0; i < word.length(); i++) {
                char ch = word.charAt(i);
                if (cur.children.get(ch) == null) {
                    cur.children.put(ch, new Trie(cur));
                }
                cur = cur.children.get(ch);
                cur.prefixCount++;
            }
            cur.fullCount++;
            cur.val = word;
        }


        /**
         * 向树中插入一个字符
         *
         * @param ch 待插入的字符
         */
        public void insert(char ch) {
            insert(String.valueOf(ch));
        }

        /**
         * 从树中删除字符串，如果树中不存在字符串，则什么也不做
         *
         * @param word 待删除的字符串
         */
        public void delete(String word) {
            /*
             * 为什么不从头部删除的原因是担心树中可能并不存在该字符串，从而误删前缀相同的其他字符串。
             * 代价是多一次遍历
             */
            Trie node = getNode(word);
            if (node == null || node.fullCount == 0) {
                return;
            }
            int i = word.length() - 1;
            int cnt = node.fullCount;
            node.fullCount = 0;
            node.val = null;
            while (node != this) {
                node.prefixCount -= cnt;
                if (node.prefixCount == 0) {
                    node.prevNode.children.remove(word.charAt(i));
                }
                i--;
                node = node.prevNode;
            }
            return;
        }

        public boolean isEmpty() {
            return this.children.isEmpty();
        }

        /**
         * 搜索树中包含该完整字符串的数目
         *
         * @param word 待搜索的字符串
         * @return 返回树中包含该完整字符串的数目
         */
        public int search(String word) {
            Trie node = getNode(word);
            return node == null ? 0 : node.fullCount;
        }

        public boolean exist(String word) {
            return search(word) > 0;
        }

        /**
         * 搜索树中包含该前缀的字符串的数目
         *
         * @param prefix 待搜索的字符串
         * @return 返回树中前缀包含该字符串的数目
         */
        public int startsWith(String prefix) {
            Trie node = getNode(prefix);
            return node == null ? 0 : node.prefixCount;
        }

        /**
         * 搜索 word 中的每一个前缀，如果树中存在这个前缀，则添加至结果集
         *
         * @param word 待搜索的字符串
         * @return 树中存在 word 的前缀字符串集合，集合不重复
         */
        public Set<String> getPrefixStringList(String word) {
            Trie node = this;
            Set<String> ans = new HashSet<>(word.length());
            for (int i = 0; i < word.length(); i++) {
                node = node.nextNode(word.charAt(i));
                if (node == null) {
                    return ans;
                }
                if (node.isStringEnd()) {
                    ans.add(node.getStringVal());
                }
            }
            return ans;
        }


        /**
         * 搜索 word 中的每一个前缀，如果树中存在这个前缀，则添加至哈希表中，哈希表的 val 是前缀的个数
         *
         * @param word 待搜索的字符串
         * @return 树中存在 word 的前缀字符串哈希集合，集合 key 是前缀字符串，val 是次数
         * @deprecated 注意，哈希表以字符串为 key 可能需要 O(len) 的添加时间，因此该方法可能不是线性的，可以采用字符串哈希优化
         */
        public Map<String, Integer> getPrefixStringMap(String word) {
            Trie node = this;
            Map<String, Integer> ans = new HashMap<>(word.length());
            for (int i = 0; i < word.length(); i++) {
                node = node.nextNode(word.charAt(i));
                if (node == null) {
                    return ans;
                }
                if (node.isStringEnd()) {
                    String key = node.getStringVal();
                    ans.put(key, node.getFullCount());
                }
            }
            return ans;
        }
    }
}
