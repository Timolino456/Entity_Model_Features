package traben.entity_model_features.mixin.rendering;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.DrownedOverlayFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.DrownedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.DrownedEntity;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.config.EMFConfig;
import traben.entity_model_features.models.IEMFModel;

@Mixin(DrownedOverlayFeatureRenderer.class)
public class MixinDrownedOverlayFeatureRenderer<T extends DrownedEntity> {


    @Mutable
    @Shadow @Final private DrownedEntityModel<T> model;
    @Unique
    private DrownedEntityModel<T> emf$heldModelToForce = null;

    @Inject(method = "<init>",
            at = @At(value = "TAIL"))
    private void emf$saveEMFModel(FeatureRendererContext<?,?> context, EntityModelLoader loader, CallbackInfo ci) {
        if(EMFConfig.getConfig().tryForceEmfModels && ((IEMFModel)model).emf$isEMFModel()){
            emf$heldModelToForce = model;
        }
    }

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/mob/DrownedEntity;FFFFFF)V",
            at = @At(value = "HEAD"))
    private void emf$resetModel(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T drownedEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        if(emf$heldModelToForce != null && EMFConfig.getConfig().tryForceEmfModels){
            model = emf$heldModelToForce;
            emf$heldModelToForce = null;
        }
   }
}
