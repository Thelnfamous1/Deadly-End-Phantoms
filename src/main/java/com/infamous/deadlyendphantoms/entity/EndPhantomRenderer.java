package com.infamous.deadlyendphantoms.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.PhantomRenderer;
import net.minecraft.client.renderer.entity.layers.PhantomEyesLayer;
import net.minecraft.client.renderer.entity.model.PhantomModel;
import net.minecraft.entity.monster.PhantomEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static com.infamous.deadlyendphantoms.DeadlyEndPhantoms.MODID;

@OnlyIn(Dist.CLIENT)
public class EndPhantomRenderer extends MobRenderer<EndPhantomEntity, EndPhantomModel<EndPhantomEntity>> {
    private static final ResourceLocation END_PHANTOM_LOCATION = new ResourceLocation(MODID, "textures/entity/end_phantom.png");

    public EndPhantomRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new EndPhantomModel<>(), 0.75F);
        this.addLayer(new EndPhantomEyesLayer<>(this));
    }

    @Override
    public ResourceLocation getEntityTexture(EndPhantomEntity entity) {
        return END_PHANTOM_LOCATION;
    }

    protected void preRenderCallback(EndPhantomEntity entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
        int i = entitylivingbaseIn.getPhantomSize();
        float f = 1.0F + 0.15F * (float)i;
        matrixStackIn.scale(f, f, f);
        matrixStackIn.translate(0.0D, 1.3125D, 0.1875D);
    }

    protected void applyRotations(EndPhantomEntity entityLiving, MatrixStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks) {
        super.applyRotations(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks);
        matrixStackIn.rotate(Vector3f.XP.rotationDegrees(entityLiving.rotationPitch));
    }
}