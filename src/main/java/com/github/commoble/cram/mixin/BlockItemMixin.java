package com.github.commoble.cram.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.github.commoble.cram.MixinCallbacks;

import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin extends Item
{	
	public BlockItemMixin(Properties properties)
	{
		super(properties);
	}

	@Inject(method="onItemUse", at=@At("HEAD"), cancellable = true)
	public void whenOnItemUse(ItemUseContext context, CallbackInfoReturnable<ActionResultType> info)
	{
		MixinCallbacks.onBlockItemUse(context, info);
	}
}
