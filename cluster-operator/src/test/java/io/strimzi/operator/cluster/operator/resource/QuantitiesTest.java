/*
 * Copyright Strimzi authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package io.strimzi.operator.cluster.operator.resource;

import org.junit.jupiter.api.Test;

import static io.strimzi.operator.cluster.operator.resource.Quantities.formatMemory;
import static io.strimzi.operator.cluster.operator.resource.Quantities.formatMilliCpu;
import static io.strimzi.operator.cluster.operator.resource.Quantities.normalizeCpu;
import static io.strimzi.operator.cluster.operator.resource.Quantities.normalizeMemory;
import static io.strimzi.operator.cluster.operator.resource.Quantities.parseCpuAsMilliCpus;
import static io.strimzi.operator.cluster.operator.resource.Quantities.parseMemory;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

public class QuantitiesTest {

    @Test
    public void testParseMemory() {
        assertThat(parseMemory("1234"), is(1234L));
        assertThat(parseMemory("0"), is(0L));
        assertThat(parseMemory("1K"), is(1000L));
        assertThat(parseMemory("1Ki"), is(1024L));
        assertThat(parseMemory("512Ki"), is(512 * 1024L));
        assertThat(parseMemory("1e6"), is(1_000_000L));

        assertThat(parseMemory("0"), is(0L));
        assertThat(parseMemory("0K"), is(0L));
        assertThat(parseMemory("0e6"), is(0L));
        assertThat(parseMemory("500Mi"), is(524288000L));
        assertThat(parseMemory("1.1Gi"), is(1181116006L));
        assertThat(parseMemory("1.1G"), is(1100000000L));
        assertThat(parseMemory("1.1e3K"), is(1100000L));

        try {
            parseMemory("-1K");
            fail();
        } catch (IllegalArgumentException e) {

        }

        try {
            parseMemory("K");
            fail();
        } catch (IllegalArgumentException e) {

        }

        try {
            parseMemory("1Kb");
            fail();
        } catch (IllegalArgumentException e) {

        }

        try {
            parseMemory("foo");
            fail();
        } catch (IllegalArgumentException e) {

        }

        try {
            parseMemory("1.1x");
            fail();
        } catch (IllegalArgumentException e) {

        }

        try {
            parseMemory("1.1e-1");
            fail();
        } catch (IllegalArgumentException e) {

        }
    }

    @Test
    public void testFormatMemory() {
        assertThat(formatMemory(0), is("0"));
        assertThat(formatMemory(1), is("1"));
        assertThat(formatMemory(1023), is("1023"));
        assertThat(formatMemory(1024), is("1024"));
        assertThat(formatMemory(1000), is("1000"));
        assertThat(formatMemory(2048), is("2048"));
        assertThat(formatMemory(2000), is("2000"));
        assertThat(formatMemory(2048 * 1024), is("2097152"));
        assertThat(formatMemory(2048 * 2000), is("4096000"));
        assertThat(formatMemory(2000 * 2000), is("4000000"));
        assertThat(formatMemory(1_000_000_000_000_000L), is("1000000000000000"));
        assertThat(formatMemory(1_000_000_000_000_000_000L), is("1000000000000000000"));
        assertThat(formatMemory(parseMemory("1Ei")), is("1125899906842624"));
        assertThat(formatMemory(parseMemory("1024Ei")), is("1152921504606846976"));
        assertThat(formatMemory(524288000L), is("524288000"));
    }

    @Test
    public void testNormalizeMemory() {
        assertThat(normalizeMemory("1K"), is("1000"));
        assertThat(normalizeMemory("1Ki"), is("1024"));
        assertThat(normalizeMemory("1M"), is("1000000"));
        assertThat(normalizeMemory("1Mi"), is("1048576"));
        assertThat(normalizeMemory("12345"), is("12345"));
        assertThat(normalizeMemory("500Mi"), is("524288000"));
        assertThat(normalizeMemory("1.1Gi"), is("1181116006"));
        assertThat(normalizeMemory("1.2Gi"), is("1288490188"));
    }

    @Test
    public void testParse() {
        assertThat(parseCpuAsMilliCpus("100000"), is(100000000));
        assertThat(parseCpuAsMilliCpus("1"), is(1000));
        assertThat(parseCpuAsMilliCpus("1m"), is(1));
        assertThat(parseCpuAsMilliCpus("0.5"), is(500));
        assertThat(parseCpuAsMilliCpus("0"), is(0));
        assertThat(parseCpuAsMilliCpus("0m"), is(0));
        assertThat(parseCpuAsMilliCpus("0.0"), is(0));
        assertThat(parseCpuAsMilliCpus("0.000001"), is(0));

        try {
            parseCpuAsMilliCpus("0.0m");
            fail();
        } catch (NumberFormatException e) { }

        try {
            parseCpuAsMilliCpus("0.1m");
            fail();
        } catch (NumberFormatException e) { }
    }

    @Test
    public void testFormat() {
        assertThat(formatMilliCpu(1000), is("1"));
        assertThat(formatMilliCpu(500), is("500m"));
        assertThat(formatMilliCpu(1), is("1m"));
    }

    @Test
    public void testRt() {
        assertThat(formatMilliCpu(parseCpuAsMilliCpus("1")), is("1"));
        assertThat(formatMilliCpu(parseCpuAsMilliCpus("500m")), is("500m"));
        assertThat(formatMilliCpu(parseCpuAsMilliCpus("1m")), is("1m"));
    }

    @Test
    public void testNormalizeCpu() {
        assertThat(normalizeCpu("1"), is("1"));
        assertThat(normalizeCpu("1000m"), is("1"));
        assertThat(normalizeCpu("500m"), is("500m"));
        assertThat(normalizeCpu("0.5"), is("500m"));
        assertThat(normalizeCpu("0.1"), is("100m"));
        assertThat(normalizeCpu("0.01"), is("10m"));
        assertThat(normalizeCpu("0.001"), is("1m"));
    }
}
