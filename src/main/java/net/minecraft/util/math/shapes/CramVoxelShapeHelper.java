package net.minecraft.util.math.shapes;

// helper to get at protected VoxelShape methods
public abstract class CramVoxelShapeHelper
{
	public static boolean shapeContains(VoxelShape shape, double x, double y, double z)
	{
		return shape.contains(x, y, z);
	}
}
