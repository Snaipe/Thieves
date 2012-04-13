package net.minekingdom.snaipe.Thieves;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.InventoryHolder;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.IInventory;
import net.minecraft.server.ItemStack;
import net.minecraft.server.PlayerInventory;

public class ThievesInventory implements IInventory
{
    EntityPlayer player;
    private ItemStack[] items = new ItemStack[36];
    
    public List<HumanEntity> transaction = new ArrayList<HumanEntity>();
    private int maxStack = MAX_STACK;
    
    public ThievesInventory(PlayerInventory inventory, EntityPlayer entityplayer)
    {
        player = entityplayer;
        this.items = inventory.items;
    }

    @Override
    public ItemStack[] getContents()
    {
        ItemStack[] C = new ItemStack[getSize()];
        System.arraycopy(items, 0, C, 0, items.length);
        return C;
    }

    @Override
    public int getSize()
    {
        return Thieves.getInstance().getSettingManager().canStealHotBar() ? 36 : 27 ;
    }

    @Override
    public ItemStack getItem(int i)
    {
        ItemStack[] is = this.items;

        if (i >= is.length)
        {
            i -= is.length;
        }
        else
        {
            i = getReversedItemSlotNum(i);
        }

        if (i >= is.length)
        {
            i -= is.length;
        }

        return is[i];
    }

    @Override
    public ItemStack splitStack(int i, int j)
    {
        ItemStack[] is = this.items;

        if (i >= is.length)
        {
            i -= is.length;
        }
        else
        {
            i = getReversedItemSlotNum(i);
        }

        if (i >= is.length)
        {
            i -= is.length;
        }

        if (is[i] != null)
        {
            ItemStack itemstack;

            if (is[i].count <= j)
            {
                itemstack = is[i];
                is[i] = null;
                return itemstack;
            }
            else
            {
                itemstack = is[i].a(j);
                if (is[i].count == 0)
                {
                    is[i] = null;
                }

                return itemstack;
            }
        }
        else
        {
            return null;
        }
    }

    @Override
    public void setItem(int i, ItemStack itemstack)
    {
        ItemStack[] is = this.items;

        if (i >= is.length)
        {
            i -= is.length;
        }
        else
        {
            i = getReversedItemSlotNum(i);
        }

        if (i >= is.length)
        {
            i -= is.length;
        }

        is[i] = itemstack;
    }

    private int getReversedItemSlotNum(int i)
    {
        if (i >= 27) return i - 27;
        else return i + 9;
    }

    @Override
    public String getName()
    {
        if (player.name.length() > 16) return player.name.substring(0, 16);
        return player.name;
    }

    @Override
    public int getMaxStackSize()
    {
        return maxStack;
    }

    @Override
    public boolean a(EntityHuman entityhuman)
    {
        return true;
    }

    @Override
    public void f()
    {

    }
    
    @Override
    public void g() 
    {
        
    }

    @Override
    public void update()
    {

    }
    
    @Override
    public void onOpen(CraftHumanEntity who) 
    {
        transaction.add(who);
    }
    
    @Override
    public void onClose(CraftHumanEntity who) 
    {
        transaction.remove(who);
    }
    
    @Override
    public List<HumanEntity> getViewers() 
    {
        return transaction;
    }
    
    @Override
    public InventoryHolder getOwner() 
    {
        return null; 
    }

    @Override
    public ItemStack splitWithoutUpdate(int i)
    {
        ItemStack[] is = this.items;

        if (i >= is.length)
        {
            i -= is.length;
        }
        else
        {
            i = getReversedItemSlotNum(i);
        }

        if (i >= is.length)
        {
            i -= is.length;
        }
        
        if (this.items[i] != null) {
            ItemStack itemstack = this.items[i];

            this.items[i] = null;
            return itemstack;
        } else {
            return null;
        }
    }

    @Override
    public void setMaxStackSize(int size)
    {
        maxStack = size;
    }
}