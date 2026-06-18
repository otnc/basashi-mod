# 開発ガイド (CONTRIBUTING)

馬刺しMOD の開発・ビルド・リリースに関する情報をまとめます。MOD自体の仕様は [DESIGN.md](DESIGN.md)、リリース運用の詳細は [RELEASING.md](RELEASING.md) を参照してください。

## 技術スタック

- Minecraft 1.20.1 / Forge（生成jarは NeoForge 1.20.1 でも動作）
- Architectury（`common` + `forge` 構成）
- Java 17 / Gradle (Architectury Loom)

## 必要環境

- **JDK 17**（必須）
- 初回ビルド時はネット接続が必要（依存ライブラリを取得）

JDK のバージョン管理に [mise](https://mise.jdx.dev/) を使う場合、プロジェクト直下の `.mise.toml` で Java 17 を固定しています:

```powershell
mise install
```

## ビルド

プロジェクト直下で:

```powershell
.\gradlew build
```

成果物（配布用jar）:

- **`forge\build\libs\basashi-forge-1.20.1-1.0.0.jar`** … Forge / NeoForge 1.20.1 共通で使える配布用 jar

`*-sources.jar` や `*-dev-shadow.jar` は配布用ではないので無視してよい。

> JDK が PATH に無い場合は、ビルド前に `JAVA_HOME` を JDK 17 に向ける:
> ```powershell
> $env:JAVA_HOME = (mise where java@temurin-17)
> $env:Path = "$env:JAVA_HOME\bin;$env:Path"
> ```

## 開発実行（ゲーム内テスト）

```powershell
.\gradlew :forge:runClient
```

NeoForge での動作確認は、ビルドした jar を NeoForge 1.20.1 環境の `mods` に入れて行う。

## ディレクトリ構成

```
common/   … ローダー非依存の本体（アイテム・イベント・レシピ・モデル・言語・テクスチャ）
forge/    … Forge固有エントリ（生成jarはNeoForge 1.20.1でも動作）
docs/img/ … README用の画像
```

## コード整形

Prettier（`prettier-plugin-java`）で Java を整形:

```powershell
npm install      # 初回のみ
npm run format       # 整形（上書き）
npm run format:check # チェックのみ
```

## CI / リリース（GitHub Actions）

ワークフローは目的別に2本。詳細な運用は [RELEASING.md](RELEASING.md) を参照。

### build.yml — ビルド検証（手動）
- **動くタイミング**: 手動実行のみ（push では走らない）
- **やること**: `./gradlew build` で検証し、jar を Actions の Artifacts に14日間保管
- **使い方**: Actions タブ → Build → **Run workflow**

### release.yml — リリース発行（手動・バージョン入力）
- **動くタイミング**: 手動実行のみ
- **やること**: 入力したMODバージョンでビルド → `basashi-<MC>-<MOD>.jar` を生成 → タグ `v<MOD>+<MC>` を作成 → GitHub Release を発行して添付
- **使い方**: Actions タブ → Release → **Run workflow** → 対象ブランチを選び、**MODバージョン**を入力（例 `1.0.0`）。MCバージョンはそのブランチの `gradle.properties` から自動取得

## テクスチャ・調整メモ

- テクスチャ（`common/.../textures/item/*.png`）は **16×16**。同名で上書きすれば差し替え可能（32×32等の高解像度も可）。
- 食料の数値やレシピは [DESIGN.md](DESIGN.md) と各 JSON / `ModItems.java` を編集して調整できる。
