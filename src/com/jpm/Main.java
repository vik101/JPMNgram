package com.jpm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {
    /**
     * Iterate through each line of input.
     */
    public static void main(String[] args) throws IOException {
        InputStreamReader reader = new InputStreamReader(System.in, StandardCharsets.UTF_8);
        BufferedReader in = new BufferedReader(reader);
        String line;
        while ((line = in.readLine()) != null) {
            System.out.println(ngram(line));
        }
    }

    private static String ngram(String input) {
        String[] params = input.split(",");
        int length = Integer.parseInt(params[0]);
        String userText = params[1];

        List<String> lookAheadWords = getLookAheadWords(length, userText);

        Map<String, Long> wordsMap = new HashMap<>();
        lookAheadWords.stream()
            .collect(Collectors.groupingBy(word -> word, () -> wordsMap, Collectors.counting()));

        Long sumOfWordFreqs = wordsMap.values()
            .stream()
            .mapToLong(Long::valueOf)
            .sum();

        Map<String, Long> sortedWordsMap = wordsMap.entrySet()
            .stream()
            .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                    LinkedHashMap::new));

        StringBuilder output = new StringBuilder();

        for (Map.Entry<String, Long> entry : sortedWordsMap.entrySet()) {
            double ratioOfWord = entry.getValue() / (double) sumOfWordFreqs;
            output.append(entry.getKey());
            output.append(",");
            output.append(String.format("%.3f", ratioOfWord));
            output.append(";");
        }

        return output.toString();
    }

    private static List<String> getLookAheadWords(int length, String userText) {
        String[] wordsInPoem = getWordTokensFromPoem();
        List<String> result = new ArrayList<>();

        int j = 0;
        while ((j + (length - 1)) < wordsInPoem.length) {
            if(wordsInPoem[j].equals(userText)) {
                result.add(wordsInPoem[j + (length - 1)]);
            }

            j++;
        }

        return result;
    }

    private static String[] getWordTokensFromPoem() {
        return poem
            .replaceAll("[\\t\\n\\r]+"," ")
            .replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");
    }

    private static String poem = "Mary had a little lamb its fleece was white as snow;\n" +
        "And everywhere that Mary went, the lamb was sure to go.\n" +
        "It followed her to school one day, which was against the rule;\n" +
        "It made the children laugh and play, to see a lamb at school.\n" +
        "And so the teacher turned it out, but still it lingered near,\n" +
        "And waited patiently about till Mary did appear.\n" +
        "\"Why does the lamb love Mary so?\" the eager children cry;\"Why, Mary loves the lamb, you know\" the teacher did reply.\"";
}
