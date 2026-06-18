# 馬刺しMOD (Basashi Mod)

Minecraft **1.20.1** 向けの食料追加MOD。**Forge / NeoForge 両対応**。

> 1.20.1 では NeoForge が Forge と完全互換（同じ `net.minecraftforge.*` パッケージ・同じ `mods.toml` 形式）のため、**ビルドした Forge 版 jar をそのまま NeoForge 1.20.1 でも導入できます**。専用の NeoForge ビルドは不要です（独立した NeoForge ビルドが必要になるのは MC 1.20.2 以降）。

## 内容

- 馬を倒すと **馬刺し** がドロップ
- 馬刺しを **かまど / 燻製機 / 焚火** で焼くと **馬のタタキ**
- **燃えている馬** を倒すと **馬のタタキ** が直接ドロップ
- **馬刺し + 卵** → **馬肉のユッケ**
- **馬刺し + ニンジン + ビートルート + 卵** → **馬肉のタルタルステーキ**

詳細仕様は [DESIGN.md](DESIGN.md) を参照。

## 必要環境

- **JDK 17**（必須）
- 初回ビルド時はネット接続が必要（依存ライブラリを取得）

## ビルド方法

プロジェクト直下で:

```powershell
.\gradlew build
```

成果物（配布用jar）:

- **`forge\build\libs\basashi-forge-1.0.0.jar`** … Forge / NeoForge 1.20.1 共通で使える配布用 jar

`*-sources.jar` や `*-dev-shadow.jar` は配布用ではないので無視してよい。

> JDK が PATH に無い場合は、ビルド前に `JAVA_HOME` を JDK 17 に向ける:
> ```powershell
> $env:JAVA_HOME = (mise where java@temurin-17)
> $env:Path = "$env:JAVA_HOME\bin;$env:Path"
> ```

## 開発実行（ゲーム内テスト）

```powershell
# Forgeでクライアント起動（NeoForgeでの動作確認は、ビルドしたjarをNeoForge環境に入れて行う）
.\gradlew :forge:runClient
```

## ディレクトリ構成

```
common/   … ローダー非依存の本体（アイテム・イベント・レシピ・モデル・言語・テクスチャ）
forge/    … Forge固有エントリ（生成jarはNeoForge 1.20.1でも動作）
```

## CI / リリース（GitHub Actions）

ワークフローは目的別に2本に分かれている。詳細な運用は [RELEASING.md](RELEASING.md) を参照。

### build.yml — ビルド検証（普段使い）
- **動くタイミング**: `main` / `mc/**` への push、すべての PR
- **やること**: `./gradlew build` でビルドが通るか検証し、jar を Actions の Artifacts に14日間保管
- **使い方**: 特に操作不要。push / PR すると自動で走る。お試しビルドが欲しいときは Actions タブから手動実行（Run workflow）も可能

### release.yml — リリース発行
- **動くタイミング**: `v*` タグの push
- **やること**: ビルド → `basashi-<MC>-<MOD>.jar` を生成 → GitHub Release を作成して添付
- **使い方**:
  ```powershell
  git tag v1.0.0+1.20.1
  git push origin v1.0.0+1.20.1
  ```
  タグ形式は `v<MODバージョン>+<MCバージョン>`。

## 備考

- テクスチャ（`common/.../textures/item/*.png`）は識別用の仮テクスチャ。同名で上書きすれば差し替え可能。
- 食料の数値やレシピは [DESIGN.md](DESIGN.md) と各 JSON / `ModItems.java` を編集して調整できる。
