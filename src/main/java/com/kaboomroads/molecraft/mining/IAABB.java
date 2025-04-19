package com.kaboomroads.molecraft.mining;

import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import org.joml.Vector3f;

import java.util.Objects;

public class IAABB {
    public final int minX;
    public final int minY;
    public final int minZ;
    public final int maxX;
    public final int maxY;
    public final int maxZ;

    public IAABB(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        this.minX = Math.min(minX, maxX);
        this.minY = Math.min(minY, maxY);
        this.minZ = Math.min(minZ, maxZ);
        this.maxX = Math.max(minX, maxX);
        this.maxY = Math.max(minY, maxY);
        this.maxZ = Math.max(minZ, maxZ);
    }

    public IAABB(Vec3i from, Vec3i to) {
        this(from.getX(), from.getY(), from.getZ(), to.getX(), to.getY(), to.getZ());
    }

    public boolean intersects(IAABB var0) {
        return intersects(var0.minX, var0.minY, var0.minZ, var0.maxX, var0.maxY, var0.maxZ);
    }

    public boolean intersects(int var0, int var2, int var4, int var6, int var8, int var10) {
        return this.minX < var6 && this.maxX > var0 && this.minY < var8 && this.maxY > var2 && this.minZ < var10 && this.maxZ > var4;
    }

    public boolean contains(int x, int y, int z) {
        return x >= minX && x <= maxX && y >= minY && y <= maxY && z >= minZ && z <= maxZ;
    }

    public float getSize() {
        int var0 = getXsize();
        int var2 = getYsize();
        int var4 = getZsize();
        return (var0 + var2 + var4) / 3.0F;
    }

    public int getXsize() {
        return maxX - minX;
    }

    public int getYsize() {
        return maxY - minY;
    }

    public int getZsize() {
        return maxZ - minZ;
    }

    public Vector3f getCenter() {
        return new Vector3f(Mth.lerp(0.5F, minX, maxX), Mth.lerp(0.5F, minY, maxY), Mth.lerp(0.5F, minZ, maxZ));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IAABB iaabb = (IAABB) o;
        return minX == iaabb.minX && minY == iaabb.minY && minZ == iaabb.minZ && maxX == iaabb.maxX && maxY == iaabb.maxY && maxZ == iaabb.maxZ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(minX, minY, minZ, maxX, maxY, maxZ);
    }

    @Override
    public String toString() {
        return "[minX=" + minX +
                ", minY=" + minY +
                ", minZ=" + minZ +
                ", maxX=" + maxX +
                ", maxY=" + maxY +
                ", maxZ=" + maxZ +
                ']';
    }
}
