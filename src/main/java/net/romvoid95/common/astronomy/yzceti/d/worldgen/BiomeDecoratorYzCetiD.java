/**
 * Copyright (C) 2020 Interstellar:  Exoplanets
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.romvoid95.common.astronomy.yzceti.d.worldgen;

import net.minecraft.world.World;

import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeDecoratorSpace;

public class BiomeDecoratorYzCetiD extends BiomeDecoratorSpace {

	private World currentWorld;

	public BiomeDecoratorYzCetiD() {}

	@Override
	protected void setCurrentWorld (World world) {
		this.currentWorld = world;
	}

	@Override
	protected World getCurrentWorld () {
		return this.currentWorld;
	}

	@Override
	protected void decorate () {

	}
}