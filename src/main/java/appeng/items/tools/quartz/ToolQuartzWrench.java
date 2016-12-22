/*
 * This file is part of Applied Energistics 2.
 * Copyright (c) 2013 - 2014, AlgorithmX2, All rights reserved.
 *
 * Applied Energistics 2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Applied Energistics 2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Applied Energistics 2.  If not, see <http://www.gnu.org/licenses/lgpl>.
 */

package appeng.items.tools.quartz;


import java.util.EnumSet;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import buildcraft.api.tools.IToolWrench;
import com.google.common.base.Optional;

import appeng.api.implementations.items.IAEWrench;
import appeng.api.util.DimensionalCoord;
import appeng.core.features.AEFeature;
import appeng.items.AEBaseItem;
import appeng.transformer.annotations.Integration.Interface;
import appeng.util.Platform;


@Interface( iface = "buildcraft.api.tools.IToolWrench", iname = "BC" )
public class ToolQuartzWrench extends AEBaseItem implements IAEWrench, IToolWrench
{

	public ToolQuartzWrench( AEFeature type )
	{
		super( Optional.of( type.name() ) );

		this.setFeature( EnumSet.of( type, AEFeature.QuartzWrench ) );
		this.setMaxStackSize( 1 );
		this.setHarvestLevel( "wrench", 0 );
	}

	@Override
	public boolean onItemUseFirst( ItemStack is, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ )
	{
		Block b = world.getBlock( x, y, z );
		if( b != null && !player.isSneaking() && Platform.hasPermissions( new DimensionalCoord( world, x, y, z ), player ) )
		{
			if( Platform.isClient() )
			{
				return !world.isRemote;
			}
			
			if (callBlockBreakEvent(x, y, z, world, player)) return false;

			ForgeDirection mySide = ForgeDirection.getOrientation( side );
			if( b.rotateBlock( world, x, y, z, mySide ) )
			{
				b.onNeighborBlockChange( world, x, y, z, Platform.AIR );
				player.swingItem();
				return !world.isRemote;
			}
		}
		return false;
	}

	@Override
	// public boolean shouldPassSneakingClickToBlock(World w, int x, int y, int z)
	public boolean doesSneakBypassUse( World world, int x, int y, int z, EntityPlayer player )
	{
		return true;
	}

	@Override
	public boolean canWrench( ItemStack is, EntityPlayer player, int x, int y, int z )
	{
		return !callBlockBreakEvent(x, y, z, player.worldObj, player);
	}

	@Override
	public boolean canWrench( EntityPlayer player, int x, int y, int z )
	{
		return !callBlockBreakEvent(x, y, z, player.worldObj, player);
	}

	@Override
	public void wrenchUsed( EntityPlayer player, int x, int y, int z )
	{
		player.swingItem();
	}
	
	public static boolean callBlockBreakEvent(int x, int y, int z, World w, EntityPlayer p)
	{
		net.minecraftforge.event.world.BlockEvent.BreakEvent event = new net.minecraftforge.event.world.BlockEvent.BreakEvent(x, y, z, w, w.getBlock(x, y, z), w.getBlockMetadata(x, y, z), p);
		return net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event);
	}
}
