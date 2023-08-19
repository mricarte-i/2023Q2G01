package org.example.suppliers;

import java.math.BigInteger;
import java.util.function.Supplier;

public class IncreasingBigIntegersSupplier implements Supplier<BigInteger> {

    private BigInteger bigInteger = BigInteger.ZERO;

    public IncreasingBigIntegersSupplier() { }

    public IncreasingBigIntegersSupplier(BigInteger startValue) {
        bigInteger = startValue;
    }

    @Override
    public BigInteger get() {
        BigInteger current = bigInteger;
        bigInteger = bigInteger.add(BigInteger.ONE);
        return current;
    }
}
