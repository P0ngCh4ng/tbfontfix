# Traveler's Backpack Font Fix

A tiny client-side Fabric mod for Minecraft 1.18.2.
It fixes a crash in [Traveler's Backpack](https://www.curseforge.com/minecraft/mc-mods/travelers-backpack-fabric) when another mod draws text over item icons.

[日本語の説明は下にあります](#日本語)

## The crash

```
java.lang.NullPointerException: Cannot invoke "net.minecraft.class_327.method_1727(String)" because "font" is null
	at net.minecraft.class_918.handler$...$wands$renderGuiItemDecorations
	at net.minecraft.class_918.method_4025
	at com.tiviacz.travelersbackpack.client.screen.OverlayHandledScreen.drawItemStack
	at com.tiviacz.travelersbackpack.client.screen.OverlayHandledScreen.renderOverlay
```

It happens when you wear a backpack with the HUD overlay enabled and put a wand from [Building Wands](https://www.curseforge.com/minecraft/mc-mods/building-wands) in a tool slot.

## Cause

`OverlayHandledScreen` extends `Screen`, but it is never initialised.
Its `textRenderer` field stays `null`, and that `null` is passed to `ItemRenderer#renderGuiItemOverlay`.

Vanilla only reads the font when the stack count is above one, so vanilla items do not crash.
Building Wands reads the font for every wand, so it crashes.

## Fix

Two `@ModifyArg` mixins replace a `null` font with `MinecraftClient.getInstance().textRenderer`.
A font that is not `null` is passed through unchanged.

| Target | Method |
| --- | --- |
| `OverlayHandledScreen` | `drawItemStack` (HUD overlay — the crash) |
| `BackpackTooltipComponent` | `drawItem` (tooltip preview — guarded as well) |

## Compatibility

- Minecraft 1.18.2, Fabric Loader 0.14+
- Tested target: Traveler's Backpack `fabric-1.18.2-7.1.39`
- Client only. Not needed on servers.

If a newer Traveler's Backpack renames these methods, the game will stop at startup with a mixin error.
In that case, remove this mod.

## Build

```
./gradlew build
```

The jar is written to `build/libs/`.

## License

MIT. This mod contains no code from Traveler's Backpack.

---

## 日本語

Minecraft 1.18.2（Fabric）用の小さなクライアント Mod です。
Traveler's Backpack のクラッシュを1つ直します。

### 起きる症状

バックパックを背負い、HUD オーバーレイを有効にした状態で、Building Wands の杖をツールスロットに入れるとクラッシュします。

### 原因

`OverlayHandledScreen` は `Screen` を継承していますが、初期化されません。
そのため `textRenderer` が `null` のまま `ItemRenderer#renderGuiItemOverlay` に渡されます。
バニラはスタック数が2以上のときしかフォントを使わないので落ちません。
Building Wands は杖を描くたびにフォントを使うので落ちます。

### 修正内容

フォントが `null` のときだけ、ゲーム標準のフォントに差し替えます。
フォントが渡されている場合は何も変えません。

### 導入

`build/libs/` の jar（または Releases の jar）を `mods` フォルダに入れてください。
サーバーには不要です。

Traveler's Backpack の更新で起動時に Mixin のエラーが出た場合は、この Mod を外してください。
