package com.superworldsun.superslegend.waypoints;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.util.math.BlockPos;

public interface IWaypoints
{
	@Nonnull
	List<Waypoint> getWaypoints();
	
	void addWaypoint(Waypoint waypoint);
	
	void removeWaypoint(Waypoint waypoint);

	void removeWaypoint(BlockPos pos);

	int getMaxWaypoints();

	@Nullable
	Waypoint getWaypoint(BlockPos blockPos);
}
