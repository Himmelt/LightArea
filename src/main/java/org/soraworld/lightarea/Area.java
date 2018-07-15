package org.soraworld.lightarea;

public class Area {

    public final int id;
    public final int x1;
    public final int y1;
    public final int z1;
    public final int x2;
    public final int y2;
    public final int z2;
    public float light;

    public Area(int id, int x1, int y1, int z1, int x2, int y2, int z2, float light) {
        this.id = id;
        this.x1 = x1 < x2 ? x1 : x2;
        this.y1 = y1 < y2 ? y1 : y2;
        this.z1 = z1 < z2 ? z1 : z2;
        this.x2 = x1 > x2 ? x1 : x2;
        this.y2 = y1 > y2 ? y1 : y2;
        this.z2 = z1 > z2 ? z1 : z2;
        this.light = light;
    }

    private boolean contains(double x, double y, double z) {
        return x >= x1 && x <= x2 + 1 && y >= y1 && y < y2 + 1 && z >= z1 && z <= z2 + 1;
    }

    boolean contains(Vec3d pos) {
        return contains(pos.x, pos.y, pos.z);
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj instanceof Area) {
            Area area = (Area) obj;
            return area.x1 == this.x1 && area.y1 == this.y1 && area.z1 == this.z1
                    && area.x2 == this.x2 && area.y2 == this.y2 && area.z2 == this.z2;
        }
        return false;
    }

    public String toString() {
        return "" + x1 + ',' + y1 + ',' + z1 + ','
                + x2 + ',' + y2 + ',' + z2 + ',' + light;
    }

    public String pos1() {
        return "(" + x1 + " , " + y1 + " , " + z1 + ')';
    }

    public String pos2() {
        return "(" + x2 + " , " + y2 + " , " + z2 + ')';
    }

    public Vec3i vec1() {
        return new Vec3i(x1, y1, z1);
    }

    public Vec3i vec2() {
        return new Vec3i(x2, y2, z2);
    }

}
