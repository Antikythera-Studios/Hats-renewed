package me.guivnf.mods.hats.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import me.guivnf.mods.hats.HatsMod;
import me.guivnf.mods.hats.client.compat.EpicFightCompat;
import me.guivnf.mods.hats.common.hat.HatPart;
import me.guivnf.mods.hats.common.hat.placement.HatPlacementInfo;
import me.guivnf.mods.hats.mixin.accessor.ModelPartAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class LayerHat<S extends LivingEntityRenderState, M extends EntityModel<? super S>>
        extends RenderLayer<S, M>
{
    private static final List<String> HEAD_PART_NAMES = Arrays.asList(
        "head", "Head", "hat", "Hat", "helmet", "Helmet", "headWear", "headwear"
    );

    private static final float PLAYER_HEAD_UNITS = 8.0f;

    private static final float Z_FIGHT_INFLATE = 1.005f;
    private static final float HEAD_HALF = 4.0f / 16.0f;

    public static final ThreadLocal<Boolean> RENDERING_HAT_GUI_PREVIEW = ThreadLocal.withInitial(() -> false);

    public static final ThreadLocal<Boolean> RENDERING_LEVEL = ThreadLocal.withInitial(() -> false);

    public static boolean isOwnFirstPersonView(Entity entity)
    {
        if (RENDERING_HAT_GUI_PREVIEW.get() || !RENDERING_LEVEL.get()) return false;

        Minecraft mc = Minecraft.getInstance();
        return entity == mc.getCameraEntity() && mc.options.getCameraType().isFirstPerson();
    }

    private static int rendersThisFrame = 0;

    public static void resetFrameCounter()
    {
        rendersThisFrame = 0;
    }

    private static final Map<ModelPart, float[]> HEAD_OFFSET_CACHE = new WeakHashMap<>();

    public LayerHat(RenderLayerParent<S, M> renderer)
    {
        super(renderer);
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector collector, int light, S state,
            float yRot, float xRot)
    {
        HatRenderStateAccess access = (HatRenderStateAccess) state;
        LivingEntity entity = access.hats$getEntity();
        if (entity != null && EpicFightCompat.PRESENT && EpicFightCompat.isEpicFightEntity(entity)) return;

        access.hats$setLayerRendered(true);

        HatPart hat = access.hats$getHat();
        if (hat == null || access.hats$isHatHidden()) return;

        if (!access.hats$isGuiPreview()
                && rendersThisFrame >= HatsMod.getConfig().maxHatRendersPerFrame) return;
        rendersThisFrame++;

        HatPlacementInfo placement = access.hats$getPlacement();
        if (placement == null) return;

        poseStack.pushPose();

        float[] headData = tryPositionAtHead(poseStack);

        float geometryScale = headData != null ? headData[0] : 1.0f;
        float finalScale = placement.scale * geometryScale;

        if (headData == null) {
            poseStack.translate(0.0, state.boundingBoxHeight + 0.1, 0.0);
        }

        poseStack.translate(0.0, HEAD_HALF, 0.0);
        poseStack.scale(Z_FIGHT_INFLATE, Z_FIGHT_INFLATE, Z_FIGHT_INFLATE);
        poseStack.translate(0.0, -HEAD_HALF, 0.0);
        HatRenderer.render(poseStack, collector, light, OverlayTexture.pack(0, 10), hat,
            placement.offsetX, placement.offsetY, placement.offsetZ,
            placement.rotX, placement.rotY, placement.rotZ, finalScale);

        poseStack.popPose();
    }

    @Nullable
    private float[] tryPositionAtHead(PoseStack poseStack)
    {
        M model = getParentModel();
        ModelPart root = model.root();

        if (model instanceof HeadedModel headed) {
            ModelPart head = headed.getHead();
            List<ModelPart> chain = buildChain(root, head);
            if (chain != null) {
                for (ModelPart bone : chain) bone.translateAndRotate(poseStack);
            } else {
                root.translateAndRotate(poseStack);
                head.translateAndRotate(poseStack);
            }
            return new float[]{ applyGeometryOffset(poseStack, head) };
        }

        List<ModelPart> chain = buildChainByName(root, HEAD_PART_NAMES);
        if (chain != null && !chain.isEmpty()) {
            for (ModelPart bone : chain) bone.translateAndRotate(poseStack);
            return new float[]{ applyGeometryOffset(poseStack, chain.get(chain.size() - 1)) };
        }

        ModelPart largest = largestChildWithCubes(root);
        if (largest != null) {
            root.translateAndRotate(poseStack);
            largest.translateAndRotate(poseStack);
            return new float[]{ applyGeometryOffset(poseStack, largest) };
        }

        return null;
    }

    private float applyGeometryOffset(PoseStack poseStack, ModelPart head)
    {
        float[] off = HEAD_OFFSET_CACHE.computeIfAbsent(head, k -> computeHeadOffsets(head));
        poseStack.translate(
            Float.isNaN(off[0]) ? 0f        : off[0],
            Float.isNaN(off[1]) ? -8f / 16f : off[1],
            Float.isNaN(off[2]) ? 0f        : off[2]);
        return Float.isNaN(off[3]) ? 1f : off[3];
    }

    private float[] computeHeadOffsets(ModelPart head)
    {
        float[] nan = { Float.NaN, Float.NaN, Float.NaN, Float.NaN };

        ModelPartAccessor headAcc = (ModelPartAccessor)(Object) head;
        List<ModelPart.Cube> cubes = headAcc.hats$getCubes();
        if (!cubes.isEmpty()) {
            return offsetsFromCubes(cubes, 0f, 0f, 0f);
        }

        Map<String, ModelPart> children = headAcc.hats$getChildren();
        if (children.isEmpty()) return nan;

        ModelPart bestChild = null;
        float bestVol = 0f;
        for (ModelPart child : children.values()) {
            float vol = totalVolume(child);
            if (vol > bestVol) { bestVol = vol; bestChild = child; }
        }

        if (bestChild != null) {
            ModelPartAccessor childAcc = (ModelPartAccessor)(Object) bestChild;
            return offsetsFromCubes(childAcc.hats$getCubes(),
                bestChild.x / 16f, bestChild.y / 16f, bestChild.z / 16f);
        }

        return nan;
    }

    private static final float MIN_HEAD_CUBE_VOL = 32f;

    private static float[] offsetsFromCubes(List<ModelPart.Cube> cubes, float px, float py, float pz)
    {
        ModelPart.Cube primary = null;
        for (ModelPart.Cube c : cubes) {
            if (volume(c) < MIN_HEAD_CUBE_VOL) continue;
            if (primary == null || c.minY < primary.minY
                    || (c.minY == primary.minY && volume(c) > volume(primary))) {
                primary = c;
            }
        }
        if (primary != null) {
            float headWidthUnits = primary.maxX - primary.minX;
            float scale = Mth.clamp(headWidthUnits / PLAYER_HEAD_UNITS, 0.2f, 3.0f);
            return new float[]{
                (primary.minX + primary.maxX) / 2f / 16f + px,
                 primary.minY                       / 16f + py,
                (primary.minZ + primary.maxZ) / 2f / 16f + pz,
                scale
            };
        }

        if (cubes.isEmpty()) return new float[]{ Float.NaN, Float.NaN, Float.NaN, Float.NaN };

        float topY = Float.MAX_VALUE;
        for (ModelPart.Cube c : cubes) topY = Math.min(topY, c.minY);

        float sumX = 0, sumZ = 0, minX = Float.MAX_VALUE, maxX = -Float.MAX_VALUE;
        int count = 0;
        for (ModelPart.Cube c : cubes) {
            if (c.minY > topY + 2f) continue;
            sumX += (c.minX + c.maxX) / 2f;
            sumZ += (c.minZ + c.maxZ) / 2f;
            minX = Math.min(minX, c.minX);
            maxX = Math.max(maxX, c.maxX);
            count++;
        }
        float cx = count > 0 ? sumX / count : 0f;
        float cz = count > 0 ? sumZ / count : 0f;
        float span = (maxX > minX) ? (maxX - minX) : PLAYER_HEAD_UNITS;
        float scale = Mth.clamp(span / PLAYER_HEAD_UNITS, 0.2f, 3.0f);
        return new float[]{
            cx / 16f + px,
            topY / 16f + py,
            cz / 16f + pz,
            scale
        };
    }


    @Nullable
    private List<ModelPart> buildChain(ModelPart root, ModelPart target)
    {
        List<ModelPart> chain = new ArrayList<>();
        if (buildChainInto(root, target, chain)) return chain;
        return null;
    }

    private boolean buildChainInto(ModelPart current, ModelPart target, List<ModelPart> chain)
    {
        chain.add(current);
        if (current == target) return true;
        for (ModelPart child : ((ModelPartAccessor)(Object) current).hats$getChildren().values()) {
            if (buildChainInto(child, target, chain)) return true;
        }
        chain.remove(chain.size() - 1);
        return false;
    }

    @Nullable
    private List<ModelPart> buildChainByName(ModelPart root, List<String> names)
    {
        return buildChainByNameInto(root, names, new ArrayList<>());
    }

    @Nullable
    private List<ModelPart> buildChainByNameInto(ModelPart current, List<String> names, List<ModelPart> chain)
    {
        chain.add(current);
        for (Map.Entry<String, ModelPart> entry :
                ((ModelPartAccessor)(Object) current).hats$getChildren().entrySet()) {
            if (names.contains(entry.getKey())) {
                chain.add(entry.getValue());
                return chain;
            }
            List<ModelPart> found = buildChainByNameInto(entry.getValue(), names, chain);
            if (found != null) return found;
        }
        chain.remove(chain.size() - 1);
        return null;
    }

    @Nullable
    private ModelPart largestChildWithCubes(ModelPart part)
    {
        ModelPart best = null;
        float bestVol = 0f;
        for (ModelPart child : ((ModelPartAccessor)(Object) part).hats$getChildren().values()) {
            float vol = totalVolume(child);
            if (vol > bestVol) { bestVol = vol; best = child; }
        }
        return best;
    }

    private static float totalVolume(ModelPart part)
    {
        float vol = 0f;
        for (ModelPart.Cube c : ((ModelPartAccessor)(Object) part).hats$getCubes()) vol += volume(c);
        return vol;
    }

    private static float volume(ModelPart.Cube c)
    {
        return (c.maxX - c.minX) * (c.maxY - c.minY) * (c.maxZ - c.minZ);
    }

}
