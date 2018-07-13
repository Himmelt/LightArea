package org.soraworld.lightarea;

import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class Vec3i {

    public final int x, y, z;

    public Vec3i(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3i(Entity entity) {
        this.x = MathHelper.floor_double(entity.posX);
        this.y = MathHelper.floor_double(entity.posY + 0.5D);
        this.z = MathHelper.floor_double(entity.posZ);
    }

    public String toString() {
        return "x:" + x + ",y:" + y + ",z:" + z;
    }
}
