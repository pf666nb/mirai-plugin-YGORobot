package com.happysnaker.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Happysnaker
 * @description
 * @date 2022/2/22
 * @email happysnaker@foxmail.com
 */
public class StringUtil {


   public static List<String> splitSpaces(String s) {
       List<String> ans = new ArrayList<>();
       for (String s1 : s.split("\\s+")) {
           if (!s1.isEmpty()) {
               ans.add(s1);
           }
       }
       return ans;
   }

   public static String reverse(String str) {
       StringBuilder sb = new StringBuilder();
       for (int i = str.length() - 1; i >= 0; i--) {
           sb.append(str.charAt(i));
       }
       return sb.toString();
   }

    public static int getEditDistance(String word1, String word2) {
        int n1= word1.length(), n2 = word2.length();
        int[][] dp = new int[n1 + 1][n2 + 1];
        for (int j = 1; j <= n2; j++) {
            dp[0][j] = j;
        }
        for (int i = 1; i <= n1; i++) {
            dp[i][0] = i;
        }
        for (int i = 1; i <= n1; i++) {
            for (int j = 1; j <= n2; j++) {
                dp[i][j] = dp[i][j - 1] + 1;
                dp[i][j] = Math.min(dp[i][j], dp[i - 1][j] + 1);
                if (word1.charAt(i - 1) != word2.charAt(j - 1)) {
                    dp[i][j] = Math.min(dp[i][j], dp[i - 1][j - 1] + 1);
                } else {
                    dp[i][j] = Math.min(dp[i][j], dp[i - 1][j - 1]);
                }
            }
        }
        return dp[n1][n2];
    }

    public static String getErrorInfoFromException(Throwable e) {
        try {
            StringWriter sw = null;
            PrintWriter pw = null;
            try {
                sw = new StringWriter();
                pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                return sw.toString();
            } finally {
                sw.close();
                pw.close();
            }
        } catch (Exception e1) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    public static boolean isNum(String s) {
        s = s.charAt(0) == '-' ? s.substring(1) : s;
        for (char ch : s.toCharArray()) {
            if (!Character.isDigit(ch)) {
                return false;
            }
        }
        return true;
    }
}
