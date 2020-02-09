package cn.innc11.giftcode.dt;

import cn.innc11.giftcode.GiftCodePlugin;
import cn.nukkit.Player;
import cn.nukkit.inventory.PlayerInventory;
import cn.nukkit.item.Item;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class Gift
{
    public UUID uuid;
    public String label = "";

    public ArrayList<Item> items = new ArrayList<>();

    public boolean give(Player player)
    {
        PlayerInventory inventory = player.getInventory();
        int freeSlots = inventory.getSize();
        for (Iterator<Integer> iterator = inventory.slots.keySet().iterator(); iterator.hasNext(); )
        {
            int slot = iterator.next().intValue();
            if (slot <= 35)
                freeSlots--;
        }
        if (freeSlots >= this.items.size())
        {
            inventory.addItem(this.items.toArray(new Item[0]));
            return true;
        }
        return false;
    }

    public List<Codes> getCodeSet()
    {
        ArrayList<Codes> rt = new ArrayList<>();
        for (Codes codeSet : GiftCodePlugin.ins.codes.values())
        {
            if (codeSet.giftUuid.equals(this.uuid))
                rt.add(codeSet);
        }
        return rt;
    }
}
