package net.minecraft.item;

import net.minecraft.init.Items;
import net.minecraft.util.registry.RegistryNamespaced;

public abstract class Item {

    /*
     * 1.7.10 - itemRegistry
     * 1.12.2 - REGISTRY
     * */
    public static RegistryNamespaced<String, Item> field_150901_e;

    /*
     * 1.12.2 - getByNameOrId
     * */
    public static Item func_111206_d(String id) {
        return Items.field_151053_p;
    }

}