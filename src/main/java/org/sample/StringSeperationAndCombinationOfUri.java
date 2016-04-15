/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.sample;

import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.infra.Blackhole;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class StringSeperationAndCombinationOfUri {
    private static final String DUMMY_URI = "/application/scope/resource/M2M1YWIwYjctMTJjZi00MDA4LWI3OTYtYmU3OWViOGY3NWM0";
    private static final Splitter SPLITTER = Splitter.on('/');
    private static final Joiner JOINER = Joiner.on(':');
    private static final CharMatcher CHAR_MATCHER = CharMatcher.anyOf("/");

    @Benchmark
    public void guavaSplitJoin(Blackhole blackhole) {
        Iterable<String> stringIterable = SPLITTER.split(DUMMY_URI);
        Iterator<String> it = stringIterable.iterator();
        blackhole.consume(it.next());
        blackhole.consume(JOINER.join(it));
    }

    @Benchmark
    public void javaxSplitJoin(Blackhole blackhole) {
        String[] stringParts = DUMMY_URI.split("/");
        blackhole.consume(stringParts[0]);
        stringParts = Arrays.copyOfRange(stringParts, 1, stringParts.length);
        blackhole.consume(String.join(":", stringParts));
    }

    @Benchmark
    public void indexOfSplit(Blackhole blackhole) {
        List<String> stringParts = split('/', DUMMY_URI);
        blackhole.consume(stringParts.remove(0));
        blackhole.consume(stringParts.stream().collect(Collectors.joining(":")));
    }

    @Benchmark
    public void streamingReplace(Blackhole blackhole) {
        blackhole.consume(DUMMY_URI.chars()
                .map(c -> c == '/' ? ':' : c)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString());
    }

    @Benchmark
    public void guavaReplace(Blackhole blackhole) {
        blackhole.consume(CHAR_MATCHER.replaceFrom(DUMMY_URI, ':'));
    }

    @Benchmark
    public void javaxReplace(Blackhole blackhole) {
        blackhole.consume(DUMMY_URI.replace('/', ':'));
    }

    @Benchmark
    public void indexOfReplace(Blackhole blackhole) {
        blackhole.consume(replace('/', ':', DUMMY_URI));
    }

    private List<String> split(final char delimiter, final String input) {
        List<String> list = new ArrayList<String>();
        int pos = 0, end;
        while ((end = input.indexOf(delimiter, pos)) >= 0) {
            list.add(input.substring(pos, end));
            pos = end + 1;
        }

        return list;
    }

    private String replace(final char oldDelim, final char newDelim, final String input) {
        StringBuilder sb = new StringBuilder(input);
        int pos = 0, end;
        while ((end = input.indexOf(oldDelim, pos)) >= 0) {
            sb.setCharAt(end, newDelim);
            pos = end + 1;
        }

        return sb.toString();
    }

}
