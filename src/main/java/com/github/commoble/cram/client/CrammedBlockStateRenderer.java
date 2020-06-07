package com.github.commoble.cram.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ILightReader;

public class CrammedBlockStateRenderer extends BlockModelRenderer
{
	private static CrammedBlockStateRenderer INSTANCE;

	public static CrammedBlockStateRenderer getInstance(BlockModelRenderer baseRenderer)
	{
		if (INSTANCE == null || INSTANCE.blockColors != baseRenderer.blockColors)
		{
			INSTANCE = new CrammedBlockStateRenderer(baseRenderer);
		}

		return INSTANCE;
	}

	public CrammedBlockStateRenderer(BlockModelRenderer baseRenderer)
	{
		super(baseRenderer.blockColors);
	}

	@Override
	public void renderQuadSmooth(ILightReader blockAccessIn, BlockState stateIn, BlockPos posIn, IVertexBuilder buffer, MatrixStack.Entry matrixEntry, BakedQuad quadIn,
		float colorMul0, float colorMul1, float colorMul2, float colorMul3, int brightness0, int brightness1, int brightness2, int brightness3, int combinedOverlayIn)
	{
		float f;
		float f1;
		float f2;
		if (quadIn.hasTintIndex())
		{
			int i = this.blockColors.getColor(stateIn, blockAccessIn, posIn, quadIn.getTintIndex());
			f = (i >> 16 & 255) / 255.0F;
			f1 = (i >> 8 & 255) / 255.0F;
			f2 = (i & 255) / 255.0F;
		}
		else
		{
			f = 1.0F;
			f1 = 1.0F;
			f2 = 1.0F;
		}
//		 FORGE: Apply diffuse lighting at render-time instead of baking it in
//		if (quadIn.shouldApplyDiffuseLighting())
//		{
//			// TODO this should be handled by the forge lighting pipeline
//			float l = net.minecraftforge.client.model.pipeline.LightUtil.diffuseLight(quadIn.getFace());
//			f *= l;
//			f1 *= l;
//			f2 *= l;
//		}

		buffer.addQuad(matrixEntry, quadIn, new float[] { colorMul0, colorMul1, colorMul2, colorMul3 }, f, f1, f2, new int[] { brightness0, brightness1, brightness2, brightness3 },
			combinedOverlayIn, true);
	}

}
