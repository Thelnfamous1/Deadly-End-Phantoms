package com.infamous.deadlyendphantoms.entity;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.AbstractEyesLayer;
import net.minecraft.client.renderer.entity.model.PhantomModel;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static com.infamous.deadlyendphantoms.DeadlyEndPhantoms.MODID;

@OnlyIn(Dist.CLIENT)
public class EndPhantomEyesLayer<T extends Entity> extends AbstractEyesLayer<T, EndPhantomModel<T>> {
    private static final RenderType field_229138_a_ = RenderType.getEyes(new ResourceLocation(MODID, "textures/entity/phantom_eyes.png"));

    public EndPhantomEyesLayer(IEntityRenderer<T, EndPhantomModel<T>> p_i50928_1_) {
        super(p_i50928_1_);
    }

    public RenderType getRenderType() {
        return field_229138_a_;
    }
}
