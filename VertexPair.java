
package graph;

import java.util.Objects;

public final class VertexPair {
    private final Object u;
    private final Object v;

    /** Constructs an unordered pair of vertices u and v. */
    public VertexPair(Object u, Object v) {
        this.u = u;
        this.v = v;
    }

    /** Returns true if obj contains the same vertices in either order. */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof VertexPair)) {
            return false;
        }
        VertexPair pair = (VertexPair) obj;

        return (Objects.equals(u, pair.u) &&
                Objects.equals(v, pair.v)) ||
               (Objects.equals(u, pair.v) &&
                Objects.equals(v, pair.u));
    }

    /** Returns an order-independent hash code for this vertex pair. */
    @Override
    public int hashCode() {
        int a = mix(Objects.hashCode(u));
        int b = mix(Objects.hashCode(v));

        return Integer.rotateLeft(Math.min(a, b), 16) ^ Math.max(a, b);
    }

    private static final int HASH_MULTIPLIER = 0x9e3779b9;

    /** Returns a mixed version of hash to improve bucket distribution. */
    private static int mix(int hash) {
        hash *= HASH_MULTIPLIER;
        return hash ^ (hash >>> 16);
    }
}
