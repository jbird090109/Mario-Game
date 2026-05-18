public enum TileType {
    EMPTY(false, false),
    GROUND(true, false),
    BLOCK(true, false),
    BRICK(true, true),
    QUESTION(true, true),
    USED(true, false),
    PIPE_TOP(true, false),
    PIPE_BODY(true, false),
    SEMI_SOLID(true, false),
    GOAL(true, false);

    public final boolean solid;
    public final boolean bumpable;

    TileType(boolean solid, boolean bumpable) {
        this.solid = solid;
        this.bumpable = bumpable;
    }
}
