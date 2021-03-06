package com.jungwuk.study.sentenceseparator;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

// TODO : 예외처리
public class Main {
    static final String[] FILTER = new String[]{"Mrs.", "Mr.", "U.S."};

    public static void main(String[] args) {
        /**
         * \으로 해설 구분 (wow\와우), " \n"으로 새 본문
         */

        StringBuilder sb = new StringBuilder();
        File sourceFile = new File("source.txt");

        try {
            String fileText = FileUtils.readFileToString(sourceFile); //TODO : 인코딩 처리
            for (String article : fileText.split("=>")) {
                String[] separatedArticle = article.split("\\\\");
                ArrayList<String> engSentences = (ArrayList<String>) separate(separatedArticle[0].replace("\r", "").replace("\n", ""));
                ArrayList<String> korSentences = (ArrayList<String>) separate(separatedArticle[1].replace("\r", "").replace("\n", ""));

                System.out.println(separatedArticle[1]);
                if (separatedArticle.length != 2) {
                    throw new IllegalArgumentException();
                }

                int length = engSentences.size();
                for (int i = 0; i < length; i ++) {
                    System.out.println(engSentences.get(i));
                    System.out.println(korSentences.get(i));
                    sb.append(engSentences.get(i)).append("\n").append(korSentences.get(i)).append("\n");
                }

                sb.append("\n\n---------------\n\n");
            }

            FileUtils.write(new File("result.txt"), sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static List<String> separate(String s){
        ArrayList<String> strings = new ArrayList<String>();
        StringBuilder sb = new StringBuilder();
        HashSet<Character> tokenSet = new HashSet<Character>();
        tokenSet.add('?');
        tokenSet.add('.');
        tokenSet.add('!');

        char[] chars = s.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            boolean shouldSkip = false;

            for (String word : FILTER) {
                if (word.charAt(0) == c) {
                    for (int j = 1; j < word.length(); j++) {
                        if (j < word.length() - 1) {
                            if (word.charAt(j) != chars[i + j]) {
                                break;
                            }
                        } else {
                            if (word.charAt(j) == chars[i + j]) {
                                shouldSkip = true;
                            }
                        }
                    }
                }

                if (shouldSkip) {
                    sb.append(word);
                    i += word.length() - 1;
                    break;
                }
            }

            if (shouldSkip) {
                continue;
            }

            sb.append(c);
            if(tokenSet.contains(c)){
                if(i + 1 < chars.length) {
                    char nextChar = chars[i + 1];
                    if (nextChar == '“' || nextChar == '"' || nextChar == '”') {
                        sb.append(nextChar);
                        i++;
                    }
                }
                strings.add(sb.toString().trim());
                sb.setLength(0);
            }
        }

        String last = sb.toString();
        if (!last.trim().isEmpty()) {
            strings.add(last);
        }
        return strings;
    }
}
