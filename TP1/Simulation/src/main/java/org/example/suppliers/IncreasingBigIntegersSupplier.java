package org.example.suppliers;

import java.math.BigInteger;
import java.util.function.Supplier;

public class IncreasingBigIntegersSupplier implements Supplier<BigInteger> {

    private BigInteger bigInteger = BigInteger.ZERO;

    @Override
    public BigInteger get() {
        bigInteger = bigInteger.add(BigInteger.ONE);
        return bigInteger;
    }
}
