# 馬刺しMOD 詳細設計書

最終更新: 2026-06-18

---

## 1. 概要

馬を倒すと「馬刺し」がドロップし、それを加工・調理して馬肉料理を作れるようにする食料追加MOD。

| 項目 | 内容 |
|------|------|
| MOD名 | 馬刺しMOD (Basashi Mod) |
| MOD ID | `basashi` |
| 対象MC | **1.20.1** |
| ローダー | **Forge + NeoForge 両対応**（※1.20.1ではForgeビルド1つで両方に対応。下記参照） |
| 言語 | Java 17 |
| ビルド | Gradle (Architectury Loom) |

> **1.20.1 における NeoForge 対応について（重要）**
> MC 1.20.1 では NeoForge は Forge のフォークで完全互換（`net.minecraftforge.*` パッケージ・`mods.toml` 形式をそのまま維持）。Architectury Loom も 1.20.1 用の独立した NeoForge プラットフォームを提供していない（`net.neoforged:neoforge` artifact は 1.20.2 以降のみ）。
> したがって本MODは **Forge版を1つビルドし、その jar を Forge / NeoForge 1.20.1 の両方で使用**する方針とする。独立した NeoForge モジュールは持たない。専用の2ビルドが必要になるのは MC 1.20.2 以降。

---

## 2. 技術スタック / バージョン

| ライブラリ | バージョン（実績値） |
|------------|--------------------|
| Minecraft | 1.20.1 |
| Architectury API | 9.2.14 |
| Architectury Loom | 1.7.435（plugin: `1.7-SNAPSHOT`） |
| Forge | 1.20.1-47.3.0 |
| Gradle | **8.10.2**（Architectury Loom 1.7 は 8.8 以上が必須） |
| JDK | **17**（mise: `temurin-17`） |

> 当初 Gradle 8.7 / NeoForge `20.1.x` を想定していたが、ビルド検証で以下が判明し修正済み:
> - Architectury Loom 1.7 は Gradle **8.8 以上**が必須 → 8.10.2 に変更
> - NeoForge 1.20.1 は `net.neoforged:neoforge` に存在せず（1.20.2以降のみ）→ NeoForgeモジュールを廃止し Forge互換jarで対応

---

## 3. プロジェクト構成

```
basashi-mod/
├── DESIGN.md                  ← 本書
├── settings.gradle
├── build.gradle               ← ルート（共通設定）
├── gradle.properties          ← バージョン定義を集約
├── gradlew / gradlew.bat
│
├── common/                    ← ★ ローダー非依存の本体（中身の大半）
│   └── src/main/
│       ├── java/com/basashi/
│       │   ├── BasashiMod.java        共通初期化（アイテム登録・イベント登録）
│       │   ├── ModItems.java          アイテム定義（DeferredRegister）
│       │   └── HorseDropHandler.java   馬の死亡イベント処理
│       └── resources/
│           ├── assets/basashi/
│           │   ├── lang/{ja_jp,en_us}.json
│           │   ├── models/item/*.json
│           │   └── textures/item/*.png
│           └── data/basashi/recipes/*.json   レシピ（焼き・クラフト）
│
└── forge/                     ← Forge固有の薄いエントリポイント（生成jarはNeoForge 1.20.1でも動作）
    └── src/main/
        ├── java/com/basashi/forge/BasashiModForge.java
        └── resources/META-INF/mods.toml
```

設計方針: **JSON（アイテムモデル・レシピ・言語・テクスチャ）と判定ロジックはすべて `common` に集約**し、`forge` は「共通初期化を呼ぶだけ」の数十行に留める。

---

## 4. 追加アイテム（4種・すべて食料）

Minecraftの食料は `nutrition`（回復する空腹度）と `saturationModifier`（隠し満腹度の係数）で表現される。
実際の隠し満腹度 = `nutrition × saturationModifier × 2`。

| ID | 表示名(日) | 種別 | nutrition | satMod | 実満腹度 | 参考（バニラ） |
|----|-----------|------|-----------|--------|----------|----------------|
| `basashi` | 馬刺し | 生 | 3 | 0.3 | 1.8 | 生の牛肉と同等 |
| `uma_no_tataki` | 馬のタタキ | 焼き | 8 | 0.8 | 12.8 | ステーキと同等 |
| `uma_yukke` | 馬肉のユッケ | 料理 | 6 | 0.6 | 7.2 | 生卵入りの中級料理 |
| `uma_tartare` | 馬肉のタルタルステーキ | 料理 | 11 | 0.9 | 19.8 | 最上級料理（黄金リンゴ未満） |

- 全アイテム最大スタック 64。
- 生系（馬刺し・ユッケ）に腐敗・毒の付与は **なし**（シンプル優先）。必要なら後で追加可。

---

## 5. ドロップ仕様（馬の死亡時）

ルートテーブル改変ではなく、**死亡イベント (`EntityEvent.LIVING_DEATH`) で判定**する。
理由: 「燃えている馬かどうか」の分岐がイベントの方が素直に書け、Forge/NeoForge共通で動くため。

対象エンティティ: **普通の馬 (`EntityType.HORSE`) のみ**（ロバ・ラバ・スケルトン馬等は対象外）。

```
馬が死亡:
  ├─ 火属性で死んだ／死亡時に燃えている  → 「馬のタタキ」をドロップ
  └─ それ以外                          → 「馬刺し」をドロップ
```

- 「燃えている」判定: `entity.isOnFire()` または ダメージソースが火属性 (`DamageTypeTags.IS_FIRE`：溶岩・炎・火打石着火・延焼など)。
- ドロップ数: 基本 **1〜2個**（ランダム）。
- ドロップ増加(Looting): 倒したプレイヤーの剣のドロップ増加エンチャ1レベルにつき **+0〜1個**（バニラの肉ドロップ準拠）。
- バニラの革(leather)ドロップは**そのまま維持**（上書きしない）。

---

## 6. 調理レシピ（焼き）— 馬刺し → 馬のタタキ

3種の加熱装置すべてに対応。

| 装置 | レシピ種別 | 調理時間 | 入手経験値 |
|------|-----------|----------|-----------|
| かまど | `minecraft:smelting` | 200 tick (10秒) | 0.35 |
| 燻製機 | `minecraft:smoking` | 100 tick (5秒) | 0.35 |
| 焚火 | `minecraft:campfire_cooking` | 600 tick (30秒) | 0.35 |

入力: `basashi:basashi` → 出力: `basashi:uma_no_tataki`

---

## 7. クラフトレシピ（作業台・形不問 shapeless）

### 馬肉のユッケ (`uma_yukke`)
| 材料 | 個数 |
|------|------|
| 馬刺し | 1 |
| 卵 (`minecraft:egg`) | 1 |

→ 馬肉のユッケ ×1

### 馬肉のタルタルステーキ (`uma_tartare`)
| 材料 | 個数 |
|------|------|
| 馬刺し | 1 |
| ニンジン (`minecraft:carrot`) | 1 |
| ビートルート (`minecraft:beetroot`) | 1 |
| 卵 (`minecraft:egg`) | 1 |

→ 馬肉のタルタルステーキ ×1

> どちらも shapeless（並べ方自由）。個数は各1個で確定。変更したい場合はここを直す。

---

## 8. 多言語対応 (lang)

| キー | ja_jp | en_us |
|------|-------|-------|
| `item.basashi.basashi` | 馬刺し | Horse Sashimi |
| `item.basashi.uma_no_tataki` | 馬のタタキ | Horse Tataki |
| `item.basashi.uma_yukke` | 馬肉のユッケ | Horse Yukke |
| `item.basashi.uma_tartare` | 馬肉のタルタルステーキ | Horse Tartare Steak |

クリエイティブタブ: バニラの「食料と飲み物」タブに4種を追加する。

---

## 9. テクスチャ / モデル

- モデルは全アイテム `item/generated`（平面テクスチャ）。
- テクスチャは 16×16 PNG。初期は**仮テクスチャ（識別用の単色＋簡易模様）**を自動生成して入れる。
  - 馬刺し: 赤系 / 馬のタタキ: こげ茶系 / ユッケ: 赤＋黄(卵) / タルタル: 赤＋緑＋紫
- 後で本番テクスチャに差し替え可能（同名で上書きするだけ）。

---

## 10. 実装タスク一覧

1. [ ] Gradle マルチプロジェクト雛形（settings/build/gradle.properties/wrapper）
2. [ ] `common`: アイテム登録 (`ModItems`)・食料定義
3. [ ] `common`: 馬死亡イベント (`HorseDropHandler`)
4. [ ] `common`: レシピJSON（焼き3種＋クラフト2種）
5. [ ] `common`: モデルJSON・lang(ja/en)・仮テクスチャ
6. [ ] `forge`: エントリポイント＋mods.toml
7. [ ] `neoforge`: エントリポイント＋mods.toml
8. [ ] JDK 17 導入 → `gradlew build` でビルド確認
9. [ ] ゲーム内動作確認（ドロップ・焼き・クラフト）

---

## 11. 前提条件

- **JDK 17** のインストールが必須（現状PC未導入）。Adoptium Temurin 17 等。
- 初回ビルド時、Gradleが依存ライブラリをダウンロードするため**ネットワーク接続**が必要。

---

## 12. 未確定・要確認事項（デフォルトで進行可）

以下はデフォルト値で進められるが、希望があれば変更する:

- 食料ステータス（§4の数値）
- ドロップ数 1〜2個（§5）
- レシピの材料個数 各1個（§7）
- 仮テクスチャの色（§9）
