package com.github.commoble.cram.client;

import com.github.commoble.cram.CrammedTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelDataManager;
import net.minecraftforge.client.model.data.IModelData;

public class CrammedTileEntityRenderer extends TileEntityRenderer<CrammedTileEntity>
{

	public CrammedTileEntityRenderer(TileEntityRendererDispatcher rendererDispatcherIn)
	{
		super(rendererDispatcherIn);
	}

	@Override
	public void render(CrammedTileEntity te, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn)
	{
		BlockRendererDispatcher dispatcher = Minecraft.getInstance().getBlockRendererDispatcher();
		BlockModelRenderer baseRenderer = dispatcher.getBlockModelRenderer();
		CrammedBlockStateRenderer renderer = CrammedBlockStateRenderer.getInstance(baseRenderer);
		BlockModelShapes shapes = dispatcher.getBlockModelShapes();
		World world = te.getWorld();
		BlockPos pos = te.getPos();
		
		for (BlockState state : te.states)
		{
	        IBakedModel model = shapes.getModel(state);
	        IModelData modelData = model.getModelData(world, pos, state, ModelDataManager.getModelData(world, pos));
//	        dispatcher.renderModel(state, pos, world, matrixStack, buffer.getBuffer(RenderTypeLookup.getRenderType(state)), true, world.rand, modelData);
			renderer.renderModel(
				world,
				model,
				state,
				pos,
				matrixStack,
				buffer.getBuffer(RenderTypeLookup.getRenderType(state)),
				false,
				world.rand,
				state.getPositionRandom(pos),
				OverlayTexture.NO_OVERLAY,
				modelData);
		}
	}

}
