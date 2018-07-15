package org.soraworld.lightarea;

import net.minecraft.entity.Entity;

public class Vec3d {

    public final double x, y, z;

    public Vec3d(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3d(Entity entity) {
        this.x = entity.posX;
        this.y = entity.boundingBox.minY;
        this.z = entity.posZ;
    }
}
