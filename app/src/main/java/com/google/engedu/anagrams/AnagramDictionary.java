/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.anagrams;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

class AnagramDictionary {

    private static final int MIN_NUM_ANAGRAMS = 4;
    private static final int DEFAULT_WORD_LENGTH = 3;
    private static final int MAX_WORD_LENGTH = 7;
    private Random random = new Random();
    private List<String> wordList = new ArrayList<>();
    private HashSet<String> wordSet = new HashSet<>();
    private int wordLength = DEFAULT_WORD_LENGTH;
    private HashMap<String, List<String>> lettersToWord = new HashMap<>();
    private HashMap<Integer, List<String>> sizeToWords = new HashMap<>();

    AnagramDictionary(Reader reader) throws IOException {
        BufferedReader in = new BufferedReader(reader);
        String line;
        while ((line = in.readLine()) != null) {
            String currentWord = line.trim();
            String sortCurrentWord = sortLetters(currentWord);

            int currentSize = currentWord.length();

            if (sizeToWords.containsKey(currentSize)) {
                sizeToWords.get(currentSize).add(currentWord);
            } else {
                sizeToWords.put(currentSize, new ArrayList<String>());
                sizeToWords.get(currentSize).add(currentWord);
            }

            if (lettersToWord.containsKey(sortCurrentWord)) {
                lettersToWord.get(sortCurrentWord).add(currentWord);
            } else {
                lettersToWord.put(sortCurrentWord, new ArrayList<String>());
                lettersToWord.get(sortCurrentWord).add(currentWord);
            }

            wordSet.add(currentWord);
            wordList.add(currentWord);
        }
    }

    boolean isGoodWord(String word, String base) {
        return wordSet.contains(word) && !word.toLowerCase().contains(base.toLowerCase());
    }

    List<String> getAnagrams(String targetWord) {

        List<String> result = new ArrayList<>();

        String sorterWord = sortLetters(targetWord);

        for (String word : wordList) {
            if (word.length() == sorterWord.length() && sorterWord.equals(sortLetters(word))) {
                result.add(word);
            }
        }

        return result;
    }

    private String sortLetters(String targetWord) {
        char[] chars = targetWord.toCharArray();
        Arrays.sort(chars);
        return new String(chars);
    }

    List<String> getAnagramsWithOneMoreLetter(String query) {
        List<String> result = new ArrayList<>();
        String alphabet = " abcdefghijklmnopqrstuvwxyz";
        char[] lettersAlphabet = alphabet.toCharArray();
        for (char letter : lettersAlphabet) {
            String newWorldSorted = sortLetters(query + letter).trim();
            if (lettersToWord.containsKey(newWorldSorted)) {
                result.addAll(lettersToWord.get(newWorldSorted));
            }
        }

        return result;
    }

    String pickGoodStarterWord() {
        int numberAnagrams = 0;
        String word = "stop";
        List<String> wordWithSize = sizeToWords.get(wordLength);
        while (numberAnagrams < MIN_NUM_ANAGRAMS) {
            int sizeArrayLength = wordWithSize.size();
            int wordRandomPosition = random.nextInt(sizeArrayLength);

            word = wordWithSize.get(wordRandomPosition);

            String sorterWord = sortLetters(word);

            numberAnagrams = lettersToWord.get(sorterWord).size();

            if (numberAnagrams >= MIN_NUM_ANAGRAMS) {
                if (wordLength < MAX_WORD_LENGTH) {
                    wordLength++;
                }
                return word;
            } else {
                wordWithSize.remove(wordRandomPosition);
            }
        }
        return word;
    }
}