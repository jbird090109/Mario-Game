public final class TileCollision {
    private TileCollision() {}

    public static boolean resolve(Player p, Level level) {
        boolean onGround = false;

        p.setPosition(p.getX() + (int) p.getVelX(), p.getY());
        if (intersectsSolid(level, p, false)) {
            if (p.getVelX() > 0) {
                int edge = ((p.getHitboxX() + p.getHitboxW()) / SmwConstants.TILE) * SmwConstants.TILE;
                p.setPosition(edge - p.getHitboxW() - p.getHitboxOffsetX(), p.getY());
            } else if (p.getVelX() < 0) {
                int edge = (p.getHitboxX() / SmwConstants.TILE + 1) * SmwConstants.TILE;
                p.setPosition(edge - p.getHitboxOffsetX(), p.getY());
            }
            p.setVelX(0);
        }

        p.setPosition(p.getX(), p.getY() + (int) p.getVelY());
        if (intersectsSolid(level, p, true)) {
            if (p.getVelY() > 0) {
                int ty = (p.getHitboxY() + p.getHitboxH()) / SmwConstants.TILE;
                int newY = ty * SmwConstants.TILE - p.getFootOffset();
                p.setPosition(p.getX(), newY);
                p.setVelY(0);
                onGround = true;
            } else if (p.getVelY() < 0) {
                int ty = p.getHitboxY() / SmwConstants.TILE;
                int tx = (p.getHitboxX() + p.getHitboxW() / 2) / SmwConstants.TILE;
                int newY = (ty + 1) * SmwConstants.TILE - (p.getHitboxY() - p.getY());
                p.setPosition(p.getX(), newY);
                p.setVelY(0);
                level.bumpBlock(tx, ty, p.getState());
            }
        }

        if (!onGround && p.getVelY() >= 0) {
            int foot = p.getHitboxY() + p.getHitboxH();
            int tx1 = p.getHitboxX() / SmwConstants.TILE;
            int tx2 = (p.getHitboxX() + p.getHitboxW() - 1) / SmwConstants.TILE;
            int ty = foot / SmwConstants.TILE;
            for (int tx = tx1; tx <= tx2; tx++) {
                if (level.getTile(tx, ty) == TileType.SEMI_SOLID) {
                    int top = ty * SmwConstants.TILE;
                    if (foot >= top && foot <= top + SmwConstants.TILE) {
                        p.setPosition(p.getX(), top - p.getFootOffset());
                        p.setVelY(0);
                        onGround = true;
                        break;
                    }
                }
            }
        }

        return onGround;
    }

    private static boolean intersectsSolid(Level level, Player p, boolean checkSemi) {
        int x1 = p.getHitboxX() / SmwConstants.TILE;
        int x2 = (p.getHitboxX() + p.getHitboxW() - 1) / SmwConstants.TILE;
        int y1 = p.getHitboxY() / SmwConstants.TILE;
        int y2 = (p.getHitboxY() + p.getHitboxH() - 1) / SmwConstants.TILE;
        for (int ty = y1; ty <= y2; ty++) {
            for (int tx = x1; tx <= x2; tx++) {
                TileType t = level.getTile(tx, ty);
                if (t == TileType.SEMI_SOLID) {
                    if (!checkSemi) {
                        continue;
                    }
                } else if (t.solid) {
                    return true;
                }
            }
        }
        return false;
    }
}
