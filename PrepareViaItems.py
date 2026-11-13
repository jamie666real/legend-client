#!/usr/bin/env python3
"""Apply ViaItems hooks to the upstream 1.12.2 source before compiling.

This idempotent source patch is used by CompileEPK.sh/CompileJS.sh so the
resource-list and rendering hooks stay synchronized with the ViaItems module.
"""
from pathlib import Path

root = Path(__file__).resolve().parent
bakery_path = root / "src/game/java/net/minecraft/client/renderer/block/model/ModelBakery.java"
render_path = root / "src/game/java/net/minecraft/client/renderer/RenderItem.java"

bakery = bakery_path.read_text(encoding="utf-8")
if "import com.isacofff.clientbase.ViaItemsManager;" not in bakery:
    anchor = "package net.minecraft.client.renderer.block.model;\n"
    if anchor not in bakery:
        raise SystemExit("ModelBakery.java package declaration not found")
    bakery = bakery.replace(anchor, anchor + "\nimport com.isacofff.clientbase.ViaItemsManager;\n", 1)

bake_anchor = "\t\tthis.bakeItemModels();\n\t\treturn this.bakedRegistry;"
bake_hook = "\t\tthis.bakeItemModels();\n\t\tViaItemsManager.clearBakedModels();\n\t\tfor (String viaId : ViaItemsManager.ITEM_IDS) {\n\t\t\tModelResourceLocation viaLocation = new ModelResourceLocation(\"minecraft:\" + viaId, \"inventory\");\n\t\t\tIBakedModel viaModel = this.bakedRegistry.getObject(viaLocation);\n\t\t\tif (viaModel != null) {\n\t\t\t\tViaItemsManager.BAKED_MODELS.put(viaId, viaModel);\n\t\t\t}\n\t\t}\n\t\treturn this.bakedRegistry;"
if "ViaItemsManager.clearBakedModels();" not in bakery:
    if bake_anchor not in bakery:
        raise SystemExit("ModelBakery setupModelRegistry anchor not found")
    bakery = bakery.replace(bake_anchor, bake_hook, 1)

variant_anchor = '\t\tthis.variantNames.put(Items.TOTEM_OF_UNDYING, Lists.newArrayList("totem"));'
variant_hook = variant_anchor + "\n\t\tViaItemsManager.loadItemList();\n\t\tList<String> paperVariants = Lists.newArrayList(\"paper\");\n\t\tfor (String viaId : ViaItemsManager.ITEM_IDS) {\n\t\t\tpaperVariants.add(\"minecraft:\" + viaId);\n\t\t}\n\t\tthis.variantNames.put(Items.PAPER, paperVariants);"
if "ViaItemsManager.loadItemList();" not in bakery:
    if variant_anchor not in bakery:
        raise SystemExit("ModelBakery registerVariantNames anchor not found")
    bakery = bakery.replace(variant_anchor, variant_hook, 1)
bakery_path.write_text(bakery, encoding="utf-8")

render = render_path.read_text(encoding="utf-8")
if "import com.isacofff.clientbase.ViaItemsManager;" not in render:
    anchor = "package net.minecraft.client.renderer;\n"
    if anchor not in render:
        raise SystemExit("RenderItem.java package declaration not found")
    render = render.replace(anchor, anchor + "\nimport com.isacofff.clientbase.ViaItemsManager;\n", 1)
method_anchor = "\t\tIBakedModel ibakedmodel = this.itemModelMesher.getItemModel(stack);\n\t\tItem item = stack.getItem();"
method_hook = "\t\tIBakedModel viaModel = ViaItemsManager.getViaModel(stack);\n\t\tif (viaModel != null) {\n\t\t\treturn viaModel;\n\t\t}\n\t\tIBakedModel ibakedmodel = this.itemModelMesher.getItemModel(stack);\n\t\tItem item = stack.getItem();"
if "ViaItemsManager.getViaModel(stack)" not in render:
    if method_anchor not in render:
        raise SystemExit("RenderItem.getItemModelWithOverrides anchor not found")
    render = render.replace(method_anchor, method_hook, 1)
render_path.write_text(render, encoding="utf-8")
print("ViaItems source hooks are present.")
