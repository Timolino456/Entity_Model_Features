package traben.entity_model_features.models.jem_objects;

import net.minecraft.util.Identifier;
import traben.entity_model_features.utils.EMFUtils;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Objects;

public class EMFPartData {


    public String texture = "";
    public int[] textureSize = null;
    public String invertAxis = "";
    public float[] translate = {0, 0, 0};
    public float[] rotate = {0, 0, 0};
    public String mirrorTexture = "";
    public EMFBoxData[] boxes = {};
    public EMFSpriteData[] sprites = {};
    public EMFPartData submodel = null;
    public LinkedList<EMFPartData> submodels = new LinkedList<>();
    public String baseId = "";  //- Model parent ID, all parent properties are inherited
    public String model = "";  //- Part model jemJsonObjects, from which to load the part model definition
    public String id = "";            //- Model ID, can be used to reference the model as parent
    public String part = null;//"!!!!!";     //- Entity part to which the part model is attached
    public boolean attach = false; //- True: attach to the entity part, False: replace it
    public float scale = 1.0f;

    public LinkedList<LinkedHashMap<String, String>> animations = null;
    public Identifier customTexture = null;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EMFPartData partData = (EMFPartData) o;
        return attach == partData.attach && Float.compare(partData.scale, scale) == 0 && Objects.equals(texture, partData.texture) && Arrays.equals(textureSize, partData.textureSize) && Objects.equals(invertAxis, partData.invertAxis) && Arrays.equals(translate, partData.translate) && Arrays.equals(rotate, partData.rotate) && Objects.equals(mirrorTexture, partData.mirrorTexture) && Arrays.equals(boxes, partData.boxes) && Arrays.equals(sprites, partData.sprites) && Objects.equals(submodel, partData.submodel) && Objects.equals(submodels, partData.submodels) && Objects.equals(baseId, partData.baseId) && Objects.equals(model, partData.model) && Objects.equals(id, partData.id) && Objects.equals(part, partData.part) && Objects.equals(animations, partData.animations) && Objects.equals(customTexture, partData.customTexture);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(texture, invertAxis, mirrorTexture, submodel, submodels, baseId, model, id, part, attach, scale, customTexture, animations);
        result = 31 * result + Arrays.hashCode(textureSize);
        result = 31 * result + Arrays.hashCode(translate);
        result = 31 * result + Arrays.hashCode(rotate);
        result = 31 * result + Arrays.hashCode(boxes);
        result = 31 * result + Arrays.hashCode(sprites);
        //result = 31 * result + Arrays.hashCode(animations);
        return result;
    }

    private void copyFrom(EMFPartData jpmModel) {
        //no part and attach
        if (submodels.isEmpty())
            this.submodels = jpmModel.submodels;
        if (submodel == null)
            this.submodel = jpmModel.submodel;
        if (textureSize == null)
            this.textureSize = jpmModel.textureSize;
        if (texture.isBlank())
            this.texture = jpmModel.texture;
        if (invertAxis.isBlank())
            this.invertAxis = jpmModel.invertAxis;
        if (translate[0] == 0 && translate[1] == 0 && translate[2] == 0)
            this.translate = jpmModel.translate;
        if (rotate[0] == 0 && rotate[1] == 0 && rotate[2] == 0)
            this.rotate = jpmModel.rotate;
        if (mirrorTexture.isBlank())
            this.mirrorTexture = jpmModel.mirrorTexture;
        if (boxes.length == 0)
            this.boxes = jpmModel.boxes;
        if (sprites.length == 0)
            this.sprites = jpmModel.sprites;
        if (scale == 1f)
            this.scale = jpmModel.scale;
        if (animations == null || animations.size() == 0)
            this.animations = jpmModel.animations;
        if (baseId.isBlank())
            this.baseId = jpmModel.baseId;//todo i'm not sure what this does yet, it probably should be defined outside the jpm and thus not copied here
    }

    public void prepare(int[] textureSize, EMFJemData jem, Identifier jemTexture) {
        if (this.id.isBlank())
            this.id = "EMF_" + hashCode();
        else
            this.id = "EMF_" + this.id;


        //check if we need to load a .jpm into this object
        if (!this.model.isEmpty()) {
            EMFPartData jpmModel = EMFUtils.EMFReadModelPart(this.model, jem.filePath);
            if (jpmModel != null) {
                copyFrom(jpmModel);

            }
        }


        if (this.textureSize == null) this.textureSize = textureSize;
        this.customTexture = jem.validateJemTexture(texture);

        if (customTexture == null) customTexture = jemTexture;

        boolean invX = invertAxis.contains("x");
        boolean invY = invertAxis.contains("y");
        boolean invZ = invertAxis.contains("z");


        //these ones need to change
        float translateX = translate[0];
        float translateY = translate[1];
        float translateZ = translate[2];

        float rotateX = (float) Math.toRadians(rotate[0]);
        float rotateY = (float) Math.toRadians(rotate[1]);
        float rotateZ = (float) Math.toRadians(rotate[2]);


        if (invX) {
            rotateX = -rotateX;
            translateX = -translateX;
        }
        if (invY) {
            rotateY = -rotateY;
            translateY = -translateY;
        }
        if (invZ) {
            rotateZ = -rotateZ;
            translateZ = -translateZ;
        }

        translate[0] = translateX;
        translate[1] = translateY;
        translate[2] = translateZ;

        rotate[0] = rotateX;
        rotate[1] = rotateY;
        rotate[2] = rotateZ;


        for (EMFBoxData box :
                boxes) {
            box.prepare(invX, invY, invZ);
        }

        for (EMFSpriteData sprite :
                sprites) {
            sprite.prepare();
        }
        if (submodel != null) {
            submodel.prepare(this.textureSize, jem, null);
            if (!submodels.contains(submodel)) {
                submodels.add(submodel);
                submodel = null;
            }
        }
        for (EMFPartData model :
                submodels) {
            model.prepare(this.textureSize, jem, null);
        }
    }

    @Override
    public String toString() {
        return "modelData{ id='" + id + "', part='" + part + "', submodels=" + submodels.size() + "', anims=" + (animations == null ? "0" : animations.size()) + '}';
    }


    public static class EMFPartPrinter {
        public String texture = "";
        public int[] textureSize = null;
        public String invertAxis = "";
        public float[] translate = {0, 0, 0};
        public float[] rotate = {0, 0, 0};
        public String mirrorTexture = "";
        public EMFBoxData.EMFBoxPrinter[] boxes = {};
        public EMFSpriteData[] sprites = {};
        public EMFPartPrinter submodel = null;
        public LinkedList<EMFPartPrinter> submodels = new LinkedList<>();
        public String baseId = "";  //- Model parent ID, all parent properties are inherited
        public String model = "";  //- Part model jemJsonObjects, from which to load the part model definition
        public String id = "";            //- Model ID, can be used to reference the model as parent
        public String part = null;//"!!!!!";     //- Entity part to which the part model is attached
        public boolean attach = false; //- True: attach to the entity part, False: replace it
        public float scale = 1.0f;
        @SuppressWarnings("unchecked")
        public LinkedHashMap<String, String>[] animations = new LinkedHashMap[]{};
    }


}
