package traben.entity_model_features.mixin.rendering;


import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.WolfCollarFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.WolfEntityModel;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.models.EMFModelPartRoot;
import traben.entity_model_features.utils.EMFManager;

@Mixin(WolfCollarFeatureRenderer.class)
public class MixinWolfCollarFeatureRenderer {

    @Unique
    private static final EntityModelLayer emf$collar_layer = new EntityModelLayer(new Identifier("minecraft", "wolf"), "collar");


    @Inject(method = "<init>", at = @At("TAIL"))
    private void setEmf$Model(FeatureRendererContext<?, ?> featureRendererContext, CallbackInfo ci) {
        ModelPart w = EMFManager.getInstance().injectIntoModelRootGetter(emf$collar_layer, WolfEntityModel.getTexturedModelData().createModel());
        if (w instanceof EMFModelPartRoot) {
            EMFManager.wolfCollarModel = new WolfEntityModel<>(w);
        }
    }

    @ModifyArg(
            method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/passive/WolfEntity;FFFFFF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/feature/WolfCollarFeatureRenderer;renderModel(Lnet/minecraft/client/render/entity/model/EntityModel;Lnet/minecraft/util/Identifier;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFF)V"),
            index = 0
    )
    private EntityModel<?> emf$injectModel(EntityModel<?> par1) {
        if (EMFManager.wolfCollarModel != null) {
            EMFManager.wolfCollarModel.handSwingProgress = par1.handSwingProgress;
            EMFManager.wolfCollarModel.riding = par1.riding;
            EMFManager.wolfCollarModel.child = par1.child;
            return EMFManager.wolfCollarModel;
        }
        return par1;
    }
}
