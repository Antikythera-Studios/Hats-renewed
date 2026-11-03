package me.guivnf.mods.hats.common.hat;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

import java.util.ArrayList;
import java.util.List;

public class HatPart
{
    public String name;
    public String registryKey = null;
    public int count = 1;
    public boolean isFavourite = false;
    public boolean isNew = false;
    public boolean isShowing = false;
    public boolean enchanted = false;

    public float[] colour = {0f, 0f, 0f, 0f};

    public float[] hsb = {0f, 0f, 0f};

    public final List<HatPart> accessories = new ArrayList<>();

    public HatPart(String name)
    {
        this.name = name;
    }

    public String getRegistryKey()
    {
        return registryKey != null ? registryKey : name;
    }

    public CompoundTag save()
    {
        CompoundTag tag = new CompoundTag();
        tag.putString("name", name);
        if (registryKey != null) tag.putString("registryKey", registryKey);
        tag.putInt("count", count);
        tag.putBoolean("favourite", isFavourite);
        tag.putBoolean("new", isNew);
        tag.putBoolean("showing", isShowing);
        tag.putBoolean("enchanted", enchanted);
        tag.putFloat("colR", colour[0]);
        tag.putFloat("colG", colour[1]);
        tag.putFloat("colB", colour[2]);
        tag.putFloat("colA", colour[3]);
        tag.putFloat("hsbH", hsb[0]);
        tag.putFloat("hsbS", hsb[1]);
        tag.putFloat("hsbB", hsb[2]);

        if (!accessories.isEmpty()) {
            ListTag list = new ListTag();
            for (HatPart acc : accessories) {
                list.add(acc.save());
            }
            tag.put("accessories", list);
        }

        return tag;
    }

    public static HatPart load(CompoundTag tag)
    {
        HatPart part = new HatPart(tag.getStringOr("name", ""));
        if (tag.contains("registryKey")) part.registryKey = tag.getStringOr("registryKey", null);
        part.count = tag.getIntOr("count", 0);
        part.isFavourite = tag.getBooleanOr("favourite", false);
        part.isNew = tag.getBooleanOr("new", false);
        part.isShowing = tag.getBooleanOr("showing", false);
        part.enchanted = tag.getBooleanOr("enchanted", false);
        part.colour[0] = tag.getFloatOr("colR", 0f);
        part.colour[1] = tag.getFloatOr("colG", 0f);
        part.colour[2] = tag.getFloatOr("colB", 0f);
        part.colour[3] = tag.getFloatOr("colA", 0f);
        part.hsb[0] = tag.getFloatOr("hsbH", 0f);
        part.hsb[1] = tag.getFloatOr("hsbS", 0f);
        part.hsb[2] = tag.getFloatOr("hsbB", 0f);

        ListTag list = tag.getListOrEmpty("accessories");
        for (int i = 0; i < list.size(); i++) {
            part.accessories.add(load(list.getCompoundOrEmpty(i)));
        }

        return part;
    }

    public HatPart copy()
    {
        HatPart copy = new HatPart(name);
        copy.registryKey = registryKey;
        copy.count = count;
        copy.isFavourite = isFavourite;
        copy.isNew = isNew;
        copy.isShowing = isShowing;
        copy.enchanted = enchanted;
        System.arraycopy(colour, 0, copy.colour, 0, colour.length);
        System.arraycopy(hsb, 0, copy.hsb, 0, hsb.length);
        for (HatPart acc : accessories) {
            copy.accessories.add(acc.copy());
        }
        return copy;
    }

    public boolean hasAccessory(String accessoryName)
    {
        for (HatPart acc : accessories) {
            if (acc.name.equals(accessoryName)) return true;
        }
        return false;
    }
}
