package com.fearlesssniper.pdfutils.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SequenceGenerator {
    private int numberOfPages;

    public SequenceGenerator(int numberOfPages) {
        this.numberOfPages = numberOfPages;
    }

    public List<Integer> getPageSequence(String sequenceString) throws BadSequenceStringException {
        // Trim down the input string
        String[] indexes = sequenceString.split("[, ]+");
        var sequence = new ArrayList<Integer>();
        boolean reverseFlag = false;
        for (String s : indexes) {
            var tempSequence = new ArrayList<Integer>();
            try {
                tempSequence.add(Integer.parseInt(s));
                // Reverse specified but is not followed by odd or even
                if (reverseFlag) {
                    throw new BadSequenceStringException(
                        "\'reverse\' command must be succeeded by either \'odd\'"
                        + " or \'even\'"
                        );
                }
            } catch (NumberFormatException e) {
                if (s.equalsIgnoreCase("reverse")) {
                    reverseFlag = true;
                } else {
                    if (s.equalsIgnoreCase("odd")) {
                        tempSequence.addAll(NumberGenerator.getOddNumbers(this.numberOfPages));
                    } else if (s.equalsIgnoreCase("even")) {
                        tempSequence.addAll(NumberGenerator.getEvenNumbers(this.numberOfPages));
                    }
                    else {
                        throw new BadSequenceStringException(
                            "Unrecognized keyword \"" + s + "\""
                            );
                    }
                    if (reverseFlag) {
                        Collections.reverse(tempSequence);
                    }
                    reverseFlag = false;
                }
            }
            sequence.addAll(tempSequence);
        }
        return sequence;
    }

    public class BadSequenceStringException extends Exception {
        public BadSequenceStringException(String errorString) {
            super(errorString);
        }
    }

    public static void main(String[] args) throws BadSequenceStringException {
        var generator1 = new SequenceGenerator(6);
        var generator2 = new SequenceGenerator(6);
        var generator3 = new SequenceGenerator(20);
        System.out.println(generator1.getPageSequence("odd reverse even"));
        System.out.println(generator2.getPageSequence("1 3 5 7 9"));
        System.out.println(generator3.getPageSequence("1 3 even 7 13 odd"));
    }
}
