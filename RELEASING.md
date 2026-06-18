# リリース運用ガイド

馬刺しMOD のバージョン管理・リリース方法と、将来の複数 Minecraft バージョン展開の方針をまとめる。

---

## バージョン形式

| 種類 | 形式 | 例 | 定義場所 |
|------|------|----|----------|
| MODバージョン | SemVer `MAJOR.MINOR.PATCH` | `1.0.0` | `gradle.properties` の `mod_version` |
| 対象MCバージョン | `x.y.z` | `1.20.1` | `gradle.properties` の `minecraft_version` |
| 内部バージョン（mods.toml/jar） | `<MC>-<MOD>` | `1.20.1-1.0.0` | build.gradle で自動合成 |
| 配布jar名 | `basashi-<MC>-<MOD>.jar` | `basashi-1.20.1-1.0.0.jar` | Release 時に生成 |

- **MODバージョンはMCと独立**。機能追加=MINOR、バグ修正=PATCH、互換を壊す変更=MAJOR。
- jar名にMCバージョンを含めることで、どのMC向けかが一目で分かる。

### SemVer の上げ方の目安
- `PATCH`（1.0.0→1.0.1）: バグ修正のみ
- `MINOR`（1.0.0→1.1.0）: アイテム/レシピ追加など後方互換のある機能追加
- `MAJOR`（1.0.0→2.0.0）: 既存の挙動・ID変更などワールドに影響する非互換変更

---

## 複数 Minecraft バージョンの展開方針

**MCバージョンごとにブランチを分ける。**

| ブランチ | 対象MC | 備考 |
|----------|--------|------|
| `main` | 1.20.1 | 現行 |
| `mc/1.21`（将来） | 1.21.x | 1.21対応を始めたら作成 |

- 各ブランチの `gradle.properties` がそのブランチの `minecraft_version` を持つ。
- 機能追加は各ブランチへ反映（必要に応じて cherry-pick）。
- **1.20.2 以降を対応する場合**は、本物の NeoForge 分岐が始まるため、`neoforge` モジュールを追加した本来のマルチローダー構成に戻すことを検討する（1.20.1 は Forge 互換jarで両対応している）。

---

## リリース手順

リリースは **GitHub Actions の手動実行（バージョン入力）** で行う。タグやファイル更新は不要。

### 1. リリースしたいブランチを確認
そのブランチの `gradle.properties` の `minecraft_version` が対象MCになる（例: `main` = 1.20.1）。

### 2. Release ワークフローを実行
GitHub の **Actions → Release → Run workflow**:
- **Use workflow from**: リリース対象のブランチを選択
- **MODバージョン**: リリースする版を入力（例 `1.0.0`）

→ [`.github/workflows/release.yml`](.github/workflows/release.yml) が自動で:
- 入力したMODバージョンでビルド（`-Pmod_version=<入力値>`）
- `basashi-<MC>-<MOD>.jar` を生成
- タグ `v<MOD>+<MC>`（例 `v1.0.0+1.20.1`）を作成
- GitHub Release を作成して jar を添付

> タグに `+<MC>` を含めるため、別のMCバージョンで同じMODバージョン（1.20.1 と 1.21 の両方で v1.0.0）を出してもタグが衝突しない。

> MODバージョンは実行時入力が優先される。`gradle.properties` の `mod_version` は普段の既定値。恒久的に上げたい場合は別途 `gradle.properties` を更新してコミットする。

---

## Modrinth / CurseForge への公開

`release.yml` は [`Kir-Antipov/mc-publish`](https://github.com/Kir-Antipov/mc-publish) で GitHub Release と同時に Modrinth / CurseForge へも公開する。**プロジェクトは各サイトで事前に手動作成**しておくこと（初回のみ）。

GitHub リポジトリの Settings に以下を登録すると有効化される（未設定のプラットフォームは自動スキップ）:

| 種類 | 場所 | 名前 | 内容 |
|------|------|------|------|
| Variable | Settings → Secrets and variables → Actions → **Variables** | `MODRINTH_PROJECT_ID` | Modrinth のプロジェクトID（slug可） |
| Variable | 同上 | `CURSEFORGE_PROJECT_ID` | CurseForge の数値プロジェクトID |
| Secret | 同 → **Secrets** | `MODRINTH_TOKEN` | Modrinth の API トークン（[PAT発行](https://modrinth.com/settings/pats)） |
| Secret | 同上 | `CURSEFORGE_TOKEN` | CurseForge の API トークン |

- ID（Variable）とトークン（Secret）の**両方**が揃ったプラットフォームのみ公開される。
- 公開時のメタ情報（ローダー=forge/neoforge、ゲームバージョン、前提MOD=architectury-api）は `release.yml` 内で指定済み。
- 各サイトで初回ファイルは審査が入る場合がある。

---

## CI（ビルド検証）

[`.github/workflows/build.yml`](.github/workflows/build.yml) は **手動実行のみ**（Actions タブ → Build → Run workflow）。push では自動で走らない。実行すると `./gradlew build` で検証し、成果物（jar）を Actions の Artifacts から14日間ダウンロードできる。リリース時のビルドは [`release.yml`](.github/workflows/release.yml) が別途行う。

---

## チェックリスト（リリース前）

- [ ] `mod_version` / `minecraft_version` が正しいか
- [ ] `./gradlew build` がローカルで通るか
- [ ] ゲーム内で動作確認したか（ドロップ・焼き・クラフト）
- [ ] タグ形式は `v<MOD>+<MC>` か
- [ ] 正しいブランチにタグを打ったか
