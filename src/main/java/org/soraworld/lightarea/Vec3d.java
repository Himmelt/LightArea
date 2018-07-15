package org.soraworld.lightarea;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;

import java.lang.reflect.Field;

public class Vec3d {

    public final double x, y, z;

    private static final Field boundingBox;

    static {
        Field field = null;
        try {
            field = Entity.class.getDeclaredField("field_70121_D");
            field.setAccessible(true);
        } catch (Throwable ignored) {
        }
        boundingBox = field;
    }

    public Vec3d(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3d(Entity entity) {
        this.x = entity.posX;
        double tmpY;
        if (boundingBox != null) {
            try {
                AxisAlignedBB box = (AxisAlignedBB) boundingBox.get(entity);
                tmpY = box.minY;
            } catch (Throwable ignored) {
                tmpY = entity.posY;
            }
        } else tmpY = entity.posY;
        this.y = tmpY;
        this.z = entity.posZ;
    }

}
