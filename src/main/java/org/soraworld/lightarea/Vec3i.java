package org.soraworld.lightarea;

import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

import java.nio.charset.StandardCharsets;

public class Vec3i {

    public final int x, y, z;

    public Vec3i(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3i(Entity entity) {
        this.x = MathHelper.floor_double(entity.posX);
        this.y = MathHelper.floor_double(entity.posY);
        this.z = MathHelper.floor_double(entity.posZ);
    }

    public String toString() {
        return "(" + x + " , " + y + " , " + z + ")";
    }

    public byte[] cui(int pos, int size) {
        return ("p|" + (pos - 1) + '|' + x + '|' + y + '|' + z + '|' + size).getBytes(StandardCharsets.UTF_8);
    }

}
