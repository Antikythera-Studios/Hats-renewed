package me.guivnf.mods.hats.client.render;

import me.guivnf.mods.hats.common.hat.HatPart;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

import org.jetbrains.annotations.Nullable;

public class HatEntityRenderState extends EntityRenderState
{
    @Nullable
    public HatPart hat;

    public float rotX;
    public float rotY;
}
