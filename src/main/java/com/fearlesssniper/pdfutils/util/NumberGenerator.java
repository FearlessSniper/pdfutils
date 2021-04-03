/*
 * The MIT License
 *
 * Copyright 2021 fearlesssniper.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.fearlesssniper.pdfutils.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 *
 * @author fearlesssniper
 */
public class NumberGenerator {
    
    /**
     * Generate a series of even numbers from 1 to end inclusive
     * @param end Ending value
     * @return The series of even numbers
     */
    public static List<Integer> getEvenNumbers(int end) {
        var evenNumbers = new ArrayList<Integer>(end / 2);
        for (int i = 0; i < end / 2; i++) {
            evenNumbers.add(2 * (i + 1));
        }
        return evenNumbers; 
    }
    
    /**
     * Generate a series of odd numbers from 1 to end inclusive
     * @param end Ending value
     * @return The series of odd numbers
     */
    public static List<Integer> getOddNumbers(int end) {
        var oddNumbers = new ArrayList<Integer>((end - 1) / 2 + 1);
        for (int i = 0; i < (end + 1) / 2; i++) {
            oddNumbers.add(2 * (i + 1) - 1);
        }
        return oddNumbers; 
    }

    /**
     * Get a list of numbers from 1 to end inclusive
     * @param end Ending value
     * @return The list of numbers
     */
    public static List<Integer> getAllNumbers(int end) {
        var numbers = new ArrayList<Integer>(end);
        for (int i = 1; i <= end; i++) {
            numbers.add(i);
        }
        return numbers;
    }
//    /**
//     * Generates a series of odd numbers from start to end inclusive.
//     * @param start Starting value
//     * @param end Ending value
//     * @return The series of odd numbers
//     */
//    public static int[] getOddNumbers(int start, int end) {
//        int[] oddNumbers = new int[(end - start) / 2 + 1];
//        int j = 0; // The counter for array position
//        for (int i = start - start % 2 + 1; i <= end; i+=2) {
//            oddNumbers[j] = i;
//            j++;
//        }
//        return oddNumbers;
//    }
    
    public static void main(String[] args) {
        System.out.println(getEvenNumbers(8));
        System.out.println(getOddNumbers(9));
    }
}
