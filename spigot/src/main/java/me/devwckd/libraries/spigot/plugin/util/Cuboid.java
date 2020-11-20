package me.devwckd.libraries.spigot.plugin.util;

import lombok.*;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.Iterator;

/**
 * @author SaiintBrisson
 */

@Getter
public class Cuboid implements Iterable<Block> {
    protected final World world;

    protected final int lowerX, lowerY, lowerZ,
      upperX, upperY, upperZ;

    protected final int sizeX, sizeY, sizeZ;

    private ChunkPosition[] chunkPositions;

    public Cuboid(@NonNull World world,
                  int lowerX, int lowerY, int lowerZ,
                  int upperX, int upperY, int upperZ) {
        this.world = world;

        this.lowerX = Math.min(lowerX, upperX);
        this.lowerY = Math.min(lowerY, upperY);
        this.lowerZ = Math.min(lowerZ, upperZ);

        this.upperX = Math.max(lowerX, upperX);
        this.upperY = Math.max(lowerY, upperY);
        this.upperZ = Math.max(lowerZ, upperZ);

        this.sizeX = (this.upperX - this.lowerX) + 1;
        this.sizeY = (this.upperY - this.lowerY) + 1;
        this.sizeZ = (this.upperZ - this.lowerZ) + 1;
    }

    /**
     * Returns this cuboid volume by
     * multiplying size X, Y, and Z
     * @return this cuboid size
     */
    public int getVolume() {
        return getSizeX() * getSizeY() * getSizeZ();
    }

    /**
     * Returns the biggest horizontal size
     * comparing size X and and Z
     * @return the biggest horizontal size
     */
    public int getBiggestHorizontalSize() {
        return Math.max(sizeX, sizeZ);
    }

    /**
     * Verifies whether the specified coordinates are
     * within this cuboid region
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     * @return if X, Y, and Z are within this cuboid
     */
    public boolean withinRegion(int x, int y, int z) {
        return x >= lowerX && x <= upperX &&
          y >= lowerY && y <= upperY &&
          z >= lowerZ && z <= upperZ;
    }

    /**
     * Verifies whether the specified location is
     * within this cuboid region
     * @param location the location to be compared
     * @return if location is within this cuboid
     */
    public boolean withinRegion(@NonNull Location location) {
        final World world = location.getWorld();
        if (world != null && !world.equals(this.world)) {
            return false;
        }

        return withinRegion(
          location.getBlockX(),
          location.getBlockY(),
          location.getBlockZ()
        );
    }

    /**
     * Verifies whether the specified block is
     * within this cuboid region
     * @param block the block to be compared
     * @return if block is within this cuboid
     */
    public boolean withinRegion(@NonNull Block block) {
        final World world = block.getWorld();
        if (world != null && !world.equals(this.world)) {
            return false;
        }

        return withinRegion(
          block.getX(),
          block.getY(),
          block.getZ()
        );
    }

    /**
     * Calculates all chunk coordinates present
     * within this cuboid
     * @return all chunk position within this cuboid
     */
    public ChunkPosition[] getChunks() {
        if (chunkPositions != null) {
            return chunkPositions;
        }

        this.chunkPositions = new ChunkPosition[0];

        for (int x = getLowerX(); x <= getUpperX(); x += 16) {
            for (int z = getLowerZ(); z <= getUpperZ(); z += 16) {
                this.chunkPositions = (ChunkPosition[]) ArrayUtils.add(
                  this.chunkPositions,
                  new ChunkPosition(x >> 4, z >> 4)
                );
            }
        }

        return this.chunkPositions;
    }

    /**
     * Creates a new iterator that goes through
     * all blocks within this cuboid
     */
    @Override
    public Iterator<Block> iterator() {
        return new BukkitBlockIterator();
    }

    /**
     * Creates a new iterator that goes through
     * all blocks within this cuboid layer
     * @param y the relative Y layer (starting from 0 up to size y, if your cuboid starts from Y 10, layer 1 will be Y 11)
     */
    public Iterator<Block> layerIterator(int y) {
        return new BukkitBlockLayerIterator(y);
    }

    @Getter
    @AllArgsConstructor
    public static class ChunkPosition {
        private final int x;
        private final int z;
    }

    @Getter
    @NoArgsConstructor
    public class BukkitBlockIterator implements Iterator<Block> {
        private int x, y, z;

        @Override
        public boolean hasNext() {
            return this.x < Cuboid.this.upperX && this.y < Cuboid.this.upperY && this.z < Cuboid.this.upperZ;
        }

        @Override
        public Block next() {
            final Block block = Cuboid.this.world.getBlockAt(this.x, this.y, this.z);

            if (++this.x >= Cuboid.this.upperX) {
                this.x = Cuboid.this.lowerX;
                if (++this.y >= Cuboid.this.upperY) {
                    this.y = Cuboid.this.upperY;
                    ++this.z;
                }
            }

            return block;
        }
    }

    @Getter
    @RequiredArgsConstructor
    public class BukkitBlockLayerIterator implements Iterator<Block> {
        private int x, z;
        private final int y;

        @Override
        public boolean hasNext() {
            return this.x < Cuboid.this.upperX && this.z < Cuboid.this.upperZ;
        }

        @Override
        public Block next() {
            final Block block = Cuboid.this.world.getBlockAt(this.x, this.y + Cuboid.this.lowerY, this.z);

            if (++this.x >= Cuboid.this.upperX) {
                this.x = Cuboid.this.lowerX;
                ++this.z;
            }

            return block;
        }
    }
}
