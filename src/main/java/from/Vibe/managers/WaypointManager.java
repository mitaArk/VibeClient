package from.Vibe.managers;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import from.Vibe.Vibe;
import from.Vibe.api.events.impl.EventRender2D;
import from.Vibe.utils.Wrapper;
import from.Vibe.utils.math.MathUtils;
import from.Vibe.utils.render.Render2D;
import from.Vibe.utils.render.fonts.Fonts;
import from.Vibe.utils.waypoint.Waypoint;
import lombok.Getter;
import from.Vibe.modules.api.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public class WaypointManager implements Wrapper {
	
    public WaypointManager() {
        Vibe.getInstance().getEventHandler().subscribe(this);
    }
	
	@Getter private final List<Waypoint> waypoints = new ArrayList<>();
	@Getter private final List<String> names = new ArrayList<>();
	
	public void add(Waypoint waypoint) {
		waypoints.add(waypoint);
		names.add(waypoint.getName());
    }

    public void remove(Waypoint waypoint) {
    	waypoints.remove(waypoint);
		names.remove(waypoint.getName());
    }

    public void clear() {
        if (!waypoints.isEmpty()) waypoints.clear();
        if (!names.isEmpty()) names.clear();
    }
    
    public boolean isEmpty() {
        return waypoints.isEmpty() || names.isEmpty();
    }

    public Waypoint getWaypoint(String name) {
        for (Waypoint waypoint : waypoints) {
            if (!waypoint.getName().equalsIgnoreCase(name)) continue;
            return waypoint;
        }
        
        return null;
    }
    
    @EventHandler
    public void onRender2D(EventRender2D e) {
    	if (Module.fullNullCheck()) return;
    	
    	float x = mc.getWindow().getScaledWidth() / 2f;
    	float y = mc.getWindow().getScaledHeight() / 4f;
    	float size = 16f;
    	float yOffset = 0f;
    	for (Waypoint waypoint : waypoints) {
        	e.getContext().getMatrices().push();
            double x2 = waypoint.getX() - mc.player.getX();
            double z2 = waypoint.getZ() - mc.player.getZ();
            float distance = MathUtils.round(MathHelper.sqrt((float) (x2 * x2 + z2 * z2)));
            float yaw = (float) -(Math.atan2(x2, z2) * (180 / Math.PI)) - mc.gameRenderer.getCamera().getYaw();
            Render2D.drawFont(
            		e.getContext().getMatrices(),
            		Fonts.BOLD.getFont(8f),
            		waypoint.getName(),
            		x - Fonts.BOLD.getWidth(waypoint.getName(), 8f) / 2f,
            		y + yOffset + 6f,
            		Color.WHITE
            );
            
            Render2D.drawFont(
            		e.getContext().getMatrices(),
            		Fonts.BOLD.getFont(7f),
            		distance + "m",
            		x - Fonts.BOLD.getWidth(distance + "m", 7f) / 2f,
            		y + yOffset + 14f,
            		Color.WHITE
            );
            
            e.getContext().getMatrices().translate(x, y + yOffset, 0.0F);
            e.getContext().getMatrices().multiply(RotationAxis.POSITIVE_Z.rotationDegrees(yaw));
            e.getContext().getMatrices().translate(-x, -(y - yOffset), 0.0F);
            Render2D.drawTexture(e.getContext().getMatrices(), x - size / 2f, y - size / 2f - yOffset, size, size, 5, Vibe.id("hud/arrow.png"), Color.WHITE);
        	e.getContext().getMatrices().pop();
        	yOffset += 30f;
    	}
    }
}