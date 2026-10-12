import java.util.Iterator;
import java.util.Random;

/**
 * ReservoirSampler.java — Module 6: Randomised Algorithms.
 *
 * Implements Algorithm R (Alan G. Waterman / Donald Knuth) for uniform
 * random sampling of 1 item (k = 1) from an arbitrary stream or iterator:
 *
 * Mathematical Properties:
 *   - Space Complexity: O(1) auxiliary selection space (does not copy or buffer the stream).
 *   - Time Complexity: O(n) single sequential scan over the elements.
 *   - Uniformity Proof:
 *       Let the stream contain n items.
 *       The i-th item is chosen with probability 1/i when encountered.
 *       For each subsequent item j (where j = i + 1 to n), the chosen item is NOT replaced
 *       with probability 1 - 1/j = (j - 1) / j.
 *       By telescoping product:
 *           P(item i is final result) = (1 / i) * (i / (i + 1)) * ((i + 1) / (i + 2)) * ... * ((n - 1) / n)
 *                                    = 1 / n.
 *       Every element in the stream has an exact, uniform 1/n probability of selection.
 */
public class ReservoirSampler {

    /**
     * Sample 1 item uniformly at random from an Iterator.
     *
     * @param iterator the stream of items
     * @param random   random number generator (injected for deterministic testing)
     * @param <T>      element type
     * @return uniformly selected element, or null if the iterator is empty/null
     */
    public static <T> T sample(Iterator<T> iterator, Random random) {
        if (iterator == null || !iterator.hasNext()) {
            return null;
        }
        if (random == null) {
            random = new Random();
        }

        // Element 1 is selected unconditionally with probability 1/1 = 1.0
        T reservoir = iterator.next();
        int count = 1;

        // Process elements 2..n
        while (iterator.hasNext()) {
            T item = iterator.next();
            count++; // 1-based position i

            // Replace current selection with probability 1 / count
            // nextInt(count) generates an integer uniformly in [0, count - 1].
            // The condition (val == 0) holds with probability exactly 1 / count.
            if (random.nextInt(count) == 0) {
                reservoir = item;
            }
        }

        return reservoir;
    }

    /**
     * Sample 1 item uniformly at random from an Iterable using a supplied Random instance.
     */
    public static <T> T sample(Iterable<T> iterable, Random random) {
        if (iterable == null) return null;
        return sample(iterable.iterator(), random);
    }

    /**
     * Sample 1 item uniformly at random from an Iterable using a default Random instance.
     */
    public static <T> T sample(Iterable<T> iterable) {
        return sample(iterable, new Random());
    }

    /** Metadata description for viva voce and UI documentation. */
    public static final String ALGORITHM = "Reservoir Sampling (Algorithm R, k=1)";
    public static final String COMPLEXITY = "Time O(n), Auxiliary Selection Space O(1)";
    public static final String PROBABILITY_FORMULA = "P(doc_i selected) = (1/i) * ∏_{j=i+1}^n (1 - 1/j) = 1/n";
}
