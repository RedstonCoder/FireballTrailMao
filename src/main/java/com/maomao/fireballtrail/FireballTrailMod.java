package com.maomao.fireballtrail;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = "fireballtrail", name = "Fireball Trail", version = "1.0")
public class FireballTrailMod {

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new FireballRenderer());
    }
}