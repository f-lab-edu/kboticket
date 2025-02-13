package com.kboticket.common.utils;


import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

@Slf4j
public class SnowFlakeIdGeneratorTest {

    @Test
    public void testSnowFlakeGenerator() {

        SnowFlakeIdGenerator generator = new SnowFlakeIdGenerator(1L);
        long id1 = generator.generateId(System.currentTimeMillis());
        long id2 = generator.generateId(System.currentTimeMillis());
        long id3 = generator.generateId(System.currentTimeMillis());

        Assertions.assertTrue(id1 < id2);
        Assertions.assertTrue(id2 < id3);
        Assertions.assertNotEquals(id1, id2);
        Assertions.assertNotEquals(id2, id3);
    }
}