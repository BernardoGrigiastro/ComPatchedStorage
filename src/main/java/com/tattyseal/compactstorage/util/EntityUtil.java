package com.tattyseal.compactstorage.util;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.MathHelper;

public class EntityUtil {

	static Direction[] HORIZONTALS = { Direction.SOUTH, Direction.WEST, Direction.NORTH, Direction.EAST };

	public static Direction get2dOrientation(LivingEntity entityliving) {
		int orientationIndex = MathHelper.floor((entityliving.rotationYaw + 45.0) / 90.0) & 3;
		return HORIZONTALS[orientationIndex];
	}
}
