package com.kaboomroads.molecraft.mining;

import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.Collection;

public class Octree {
    public Node rootNode = null;

    public MiningArea get(int x, int y, int z) {
        return navigate(rootNode, x, y, z);
    }

    public MiningArea navigate(Node node, int x, int y, int z) {
        if (node.boundingBox.contains(x, y, z))
            if (!node.leaf) for (Node child : node.children) navigate(child, x, y, z);
            else for (MiningArea area : node.areas) if (area.bounds.contains(x, y, z)) return area;
        return null;
    }

    public void build(Collection<MiningArea> areas) {
        rootNode = new Node(generateEncapsulatingCube(areas));
        bleurgate(rootNode, areas);
    }

    private void bleurgate(Node root, Collection<MiningArea> miningAreas) {
        if (miningAreas.size() <= 8) root.areas = miningAreas;
        else {
            int lastSize = miningAreas.size();
            Node[] subdivisions = root.subdivide();
            for (Node subNode : subdivisions) {
                ArrayList<MiningArea> inside = new ArrayList<>();
                for (MiningArea area : miningAreas) {
                    AABB subBB = subNode.boundingBox;
                    IAABB bounds = area.bounds;
                    if (intersects(subBB.minX, subBB.minY, subBB.minZ, subBB.maxX, subBB.maxY, subBB.maxZ, bounds.minX, bounds.minY, bounds.minZ, bounds.maxX, bounds.maxY, bounds.maxZ))
                        inside.add(area);
                }
                if (inside.size() == lastSize) {
                    root.areas = miningAreas;
                    return;
                } else bleurgate(subNode, inside);
            }
            root.children[0] = subdivisions[0];
            root.children[1] = subdivisions[1];
            root.children[2] = subdivisions[2];
            root.children[3] = subdivisions[3];
            root.children[4] = subdivisions[4];
            root.children[5] = subdivisions[5];
            root.children[6] = subdivisions[6];
            root.children[7] = subdivisions[7];
            root.leaf = false;
        }
    }

    public boolean intersects(double minX1, double minY1, double minZ1, double maxX1, double maxY1, double maxZ1, double minX2, double minY2, double minZ2, double maxX2, double maxY2, double maxZ2) {
        return minX1 < minX2 && maxX1 > maxX2 && minY1 < minY2 && maxY1 > maxY2 && minZ1 < minZ2 && maxZ1 > maxZ2;
    }

    public static AABB generateEncapsulatingCube(Iterable<MiningArea> boxes) {
        int globalMinX = Integer.MAX_VALUE;
        int globalMinY = Integer.MAX_VALUE;
        int globalMinZ = Integer.MAX_VALUE;
        int globalMaxX = Integer.MIN_VALUE;
        int globalMaxY = Integer.MIN_VALUE;
        int globalMaxZ = Integer.MIN_VALUE;
        for (MiningArea area : boxes) {
            IAABB box = area.bounds;
            globalMinX = Math.min(globalMinX, box.minX);
            globalMinY = Math.min(globalMinY, box.minY);
            globalMinZ = Math.min(globalMinZ, box.minZ);
            globalMaxX = Math.max(globalMaxX, box.maxX + 1);
            globalMaxY = Math.max(globalMaxY, box.maxY + 1);
            globalMaxZ = Math.max(globalMaxZ, box.maxZ + 1);
        }
        double centerX = (globalMinX + globalMaxX) * 0.5;
        double centerY = (globalMinY + globalMaxY) * 0.5;
        double centerZ = (globalMinZ + globalMaxZ) * 0.5;
        double halfSize = Math.max(globalMaxX - globalMinX, Math.max(globalMaxY - globalMinY, globalMaxZ - globalMinZ)) * 0.5;
        return new AABB(Math.floor(centerX - halfSize), Math.floor(centerY - halfSize), Math.floor(centerZ - halfSize), Math.ceil(centerX + halfSize), Math.ceil(centerY + halfSize), Math.ceil(centerZ + halfSize));
    }

    public static class Node {
        public final AABB boundingBox;
        public boolean leaf = true;
        public final Node[] children = new Node[8];
        public Collection<MiningArea> areas = null;

        public Node(AABB boundingBox) {
            this.boundingBox = boundingBox;
        }

        public Node[] subdivide() {
            double minX = boundingBox.minX;
            double minY = boundingBox.minY;
            double minZ = boundingBox.minZ;
            double maxX = boundingBox.maxX;
            double maxY = boundingBox.maxY;
            double maxZ = boundingBox.minZ;
            double midX = (minX + maxX) * 0.5;
            double midY = (minY + maxY) * 0.5;
            double midZ = (minZ + maxZ) * 0.5;
            Node[] tempChildren = new Node[8];
            tempChildren[0] = new Node(new AABB(minX, minY, minZ, midX, midY, midZ));
            tempChildren[1] = new Node(new AABB(midX, minY, minZ, maxX, midY, midZ));
            tempChildren[2] = new Node(new AABB(minX, midY, minZ, midX, maxY, midZ));
            tempChildren[3] = new Node(new AABB(midX, midY, minZ, maxX, maxY, midZ));
            tempChildren[4] = new Node(new AABB(minX, minY, midZ, midX, midY, maxZ));
            tempChildren[5] = new Node(new AABB(midX, minY, midZ, maxX, midY, maxZ));
            tempChildren[6] = new Node(new AABB(minX, midY, midZ, midX, maxY, maxZ));
            tempChildren[7] = new Node(new AABB(midX, midY, midZ, maxX, maxY, maxZ));
            return tempChildren;
        }
    }
}
