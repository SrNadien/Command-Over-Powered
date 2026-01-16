package nadiendev.cmdop.util;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;

public class DummyCraftingMenu extends CraftingMenu {

    public DummyCraftingMenu(int id, Inventory inventory, ContainerLevelAccess containerLevelAccess) {
        super(id, inventory, containerLevelAccess);
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}