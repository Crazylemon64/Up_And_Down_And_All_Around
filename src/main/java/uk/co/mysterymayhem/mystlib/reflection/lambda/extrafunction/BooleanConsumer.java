package uk.co.mysterymayhem.mystlib.reflection.lambda.extrafunction;

import java.util.Objects;

/**
 * Created by Mysteryem on 2016-12-30.
 */
@FunctionalInterface
public interface BooleanConsumer {
    default BooleanConsumer andThen(BooleanConsumer after) {
        Objects.requireNonNull(after);
        return (t) -> {
            this.accept(t);
            after.accept(t);
        };
    }

    void accept(boolean value);
}
