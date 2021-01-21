package com.github.commoble.cram.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.ILightReader;
import net.minecraft.world.World;

/**
 * Wrapper class around the vanilla block renderer so we don't have to copy five
 * functions verbatim
 **/
public class BlockPreviewRenderer extends BlockModelRenderer
{
	private static BlockPreviewRenderer INSTANCE;
	
	public static BlockPreviewRenderer getInstance(BlockModelRenderer baseRenderer)
	{
		if (INSTANCE == null || INSTANCE.blockColors != baseRenderer.blockColors)
		{
			INSTANCE = new BlockPreviewRenderer(baseRenderer);
		}
		
		return INSTANCE;
	}
	public BlockPreviewRenderer(BlockModelRenderer baseRenderer)
	{
		super(baseRenderer.blockColors);
	}

	// invoked from the DrawHighlightEvent.HighlightBlock event
	public static void renderBlockPreview(BlockPos pos, BlockState state, World world, Vec3d currentRenderPos, MatrixStack matrix, IRenderTypeBuffer renderTypeBuffer)
	{
		matrix.push();
	
		// the current position of the matrix stack is the position of the player's
		// viewport (the head, essentially)
		// we want to move it to the correct position to render the block at
		double offsetX = pos.getX() - currentRenderPos.getX();
		double offsetY = pos.getY() - currentRenderPos.getY();
		double offsetZ = pos.getZ() - currentRenderPos.getZ();
		matrix.translate(offsetX, offsetY, offsetZ);
	
		BlockRendererDispatcher blockDispatcher = Minecraft.getInstance().getBlockRendererDispatcher();
		BlockModelRenderer renderer = getInstance(blockDispatcher.getBlockModelRenderer());
		renderer.renderModel(
			world,
			blockDispatcher.getModelForState(state),
			state,
			pos,
			matrix,
			renderTypeBuffer.getBuffer(RenderTypeLookup.getRenderType(state)),
			false,
			world.rand,
			state.getPositionRandom(pos),
			OverlayTexture.NO_OVERLAY,
			net.minecraftforge.client.model.data.EmptyModelData.INSTANCE);
	
		matrix.pop();
	}

	/** As in the superclass's method, except we tint the RGB values over time **/
	@Override
	public void renderQuadSmooth(ILightReader world, BlockState state, BlockPos pos, IVertexBuilder buffer, MatrixStack.Entry matrixEntry, BakedQuad quadIn,
		float tintA, float tintB, float tintC, float tintD, int brightness0, int brightness1, int brightness2, int brightness3, int combinedOverlayIn)
	{
		long milliTime = Util.milliTime();
		double valueTime = milliTime * 0.005D;
		double colorTime = Util.milliTime() * 0.005D;
		float value = 1;
		float r= value * (float) ((Math.sin(colorTime) / 4D) + 0.5D);
		float g= value * (float) ((Math.sin(colorTime + 2*Math.PI / 3) / 4D) + 0.5D);
		float b= value * (float) ((Math.sin(colorTime + 4*Math.PI / 3) / 4D) + 0.5D);
		if (quadIn.hasTintIndex())
		{
			int i = this.blockColors.getColor(state, world, pos, quadIn.getTintIndex());
			r = (i >> 16 & 255) / 255.0F;
			g = (i >> 8 & 255) / 256.0F;
			b = (i & 255) / 255.0F;
		}
		// FORGE: Apply diffuse lighting at render-time instead of baking it in
		if (quadIn.shouldApplyDiffuseLighting())
		{
			// TODO this should be handled by the forge lighting pipeline
			float forgeLighting = net.minecraftforge.client.model.pipeline.LightUtil.diffuseLight(quadIn.getFace());
			r *= forgeLighting;
			g *= forgeLighting;
			b *= forgeLighting;
		}

		// use our method below instead of adding the quad in the usual manner
		buffer.addQuad(matrixEntry, quadIn, new float[] { tintA, tintB, tintC, tintD }, r, g, b, new int[] { brightness0, brightness1, brightness2, brightness3 },
			combinedOverlayIn, true);
	
	}
}