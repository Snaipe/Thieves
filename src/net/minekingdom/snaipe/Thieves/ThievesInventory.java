package net.minekingdom.snaipe.Thieves;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.IInventory;
import net.minecraft.server.ItemStack;
import net.minecraft.server.PlayerInventory;

public class ThievesInventory implements IInventory
{
    EntityPlayer player;
    private ItemStack[] items = new ItemStack[36];
    
    public ThievesInventory(PlayerInventory inventory, EntityPlayer entityplayer)
    {
        player = entityplayer;
        this.items = inventory.items;
    }

    public ItemStack[] getContents()
    {
        ItemStack[] C = new ItemStack[getSize()];
        System.arraycopy(items, 0, C, 0, items.length);
        return C;
    }

    public int getSize()
    {
        return Thieves.getInstance().getSettingManager().canStealHotBar() ? 36 : 27 ;
    }

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

    public String getName()
    {
        if (player.name.length() > 16) return player.name.substring(0, 16);
        return player.name;
    }

    public int getMaxStackSize()
    {
        return 64;
    }

    public boolean a(EntityHuman entityhuman)
    {
        return true;
    }

    public void f()
    {

    }
    
    public void g() 
    {
        
    }

    public void update()
    {

    }
}