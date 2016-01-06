package nl.marayla.Xara.ElementCollisions;

import org.jetbrains.annotations.Contract;

public final class CollisionResult {
    @Contract("_, _ -> !null")
    public static CollisionResult determineCollisionResult(
            final ElementCollisionData element1,
            final ElementCollisionData element2
    ) {
        return new CollisionResult(
                element1.getCollision().determineElement1Result(element1, element2),
                element1.getCollision().determineElement2Result(element1, element2)
        );
    }

    public CollisionResult(
            final ElementCollision.ElementCollisionResult element1Result,
            final ElementCollision.ElementCollisionResult element2Result
    ) {
        this.element1Result = element1Result;
        this.element2Result = element2Result;
    }

    public final ElementCollision.ElementCollisionResult getElement1Result() {
        return element1Result;
    }

    public final ElementCollision.ElementCollisionResult getElement2Result() {
        return element2Result;
    }

    private ElementCollision.ElementCollisionResult element1Result;
    private ElementCollision.ElementCollisionResult element2Result;
}
