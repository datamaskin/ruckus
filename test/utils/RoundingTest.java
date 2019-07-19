package utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by mwalsh on 7/21/14.
 */
public class RoundingTest {

    private int round(float i, float v) {
        return (int)(Math.round(i/v) * v);
    }

    @Test
    public void testNickel() {
        assertEquals(1670, round(1672, 5));
        assertEquals(1675, round(1673, 5));
        assertEquals(1675, round(1674, 5));
        assertEquals(1675, round(1675, 5));
        assertEquals(1675, round(1676, 5));
        assertEquals(1675, round(1677, 5));
        assertEquals(1680, round(1678, 5));
        assertEquals(1680, round(1679, 5));
        assertEquals(1680, round(1680, 5));
        assertEquals(1680, round(1681, 5));
        assertEquals(1680, round(1682, 5));
        assertEquals(1685, round(1683, 5));
    }

    @Test
    public void testDime() {
        assertEquals(1670, round(1672, 10));
        assertEquals(1670, round(1673, 10));
        assertEquals(1670, round(1674, 10));
        assertEquals(1680, round(1675, 10));
        assertEquals(1680, round(1676, 10));
        assertEquals(1680, round(1677, 10));
        assertEquals(1680, round(1678, 10));
        assertEquals(1680, round(1679, 10));
        assertEquals(1680, round(1680, 10));
        assertEquals(1680, round(1681, 10));
        assertEquals(1680, round(1682, 10));
        assertEquals(1680, round(1683, 10));
        assertEquals(1680, round(1684, 10));
        assertEquals(1690, round(1685, 10));

    }

    @Test
    public void testDollar(){
        assertEquals(32600, round(32649, 100));
        assertEquals(32700, round(32651, 100));
    }

}
