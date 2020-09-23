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

package net.romvoid95.core;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.romvoid95.api.registry.ExoRegistry;
import net.romvoid95.common.ExoplanetSounds;
import net.romvoid95.core.initialization.ExoBlocks;
import net.romvoid95.core.initialization.ExoEntities;
import net.romvoid95.core.initialization.ExoItems;

public class RegistrationHandler {

	public static void init (ExoRegistry r) {
		r.addRegistrationHandler(ExoEntities::registerAll, EntityEntry.class);
		r.addRegistrationHandler(ExoBlocks::registerAll, Block.class);
		r.addRegistrationHandler(ExoItems::registerAll, Item.class);
		r.addRegistrationHandler(ExoplanetSounds::registerAll, SoundEvent.class);
	}
}