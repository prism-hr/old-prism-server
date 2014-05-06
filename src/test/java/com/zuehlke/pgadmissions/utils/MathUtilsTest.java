package com.zuehlke.pgadmissions.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.math.BigDecimal;

import org.junit.Test;

public class MathUtilsTest {

    @Test
    public void testConvertRatingToPercent() {
        assertNull(MathUtils.convertRatingToPercent(null));
        assertEquals(new Integer(0), MathUtils.convertRatingToPercent(new BigDecimal(0)));
        assertEquals(new Integer(20), MathUtils.convertRatingToPercent(new BigDecimal(1)));
        assertEquals(new Integer(50), MathUtils.convertRatingToPercent(new BigDecimal(2.5)));
        assertEquals(new Integer(67), MathUtils.convertRatingToPercent(new BigDecimal(3.33)));
        assertEquals(new Integer(100), MathUtils.convertRatingToPercent(new BigDecimal(5)));
    }
    
    @Test
    public void testFormattingRating() {
        assertNull(MathUtils.formatRating(null));
        assertEquals("0.00", MathUtils.formatRating(new BigDecimal(0)));
        assertEquals("2.00", MathUtils.formatRating(new BigDecimal(2.00)));
        assertEquals("2.50", MathUtils.formatRating(new BigDecimal(2.50)));
        assertEquals("2.33", MathUtils.formatRating(new BigDecimal(2.33)));
    }

}
